package com.lin.missyou.service;

import com.lin.missyou.core.LocalUser;
import com.lin.missyou.core.enumeration.OrderStatus;
import com.lin.missyou.core.interceptors.PermissionInterceptor;
import com.lin.missyou.core.interceptors.ScopeLevel;
import com.lin.missyou.core.money.IMoneyDiscount;
import com.lin.missyou.dto.OrderDTO;
import com.lin.missyou.dto.SkuInfoDTO;
import com.lin.missyou.exception.http.ForbiddenException;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.logic.CouponChecker;
import com.lin.missyou.logic.OrderChecker;
import com.lin.missyou.model.*;
import com.lin.missyou.repository.CouponRepository;
import com.lin.missyou.repository.OrderRepository;
import com.lin.missyou.repository.SkuRepository;
import com.lin.missyou.repository.UserCouponRepository;
import com.lin.missyou.util.CommonUtil;
import com.lin.missyou.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    SkuService skuService;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    UserCouponRepository userCouponRepository;
    @Autowired
    IMoneyDiscount iMoneyDiscount;
    @Autowired
    SkuRepository skuRepository;

    @Value("${missyou.order.max-sku-limit}")
    private int maxSkuLimit;

    @Value("${missyou.order.pay-time-limit}")
    private int payTimeLimit;


    /**
     * 订单校验的主方法
     *
     * @param uid
     * @param orderDTO
     * @return 返回校验后的order
     */
    public OrderChecker isOk(Long uid, OrderDTO orderDTO){
        CouponChecker couponChecker = null;
        // 只是演示另一种校验方法
        if(orderDTO.getFinalTotalPrice().compareTo(new BigDecimal("0")) <= 0){
            throw new ParameterException(50011);
        }
        // 用SkuId取得SkuModelList
        List<Long> skuIdList = orderDTO.getSkuInfoList()
                .stream()
                .map(SkuInfoDTO::getId)
                .collect(Collectors.toList());
        List<Sku> skuList = skuService.getSkuListByIds(skuIdList);

        //获取CouponModel
        Long couponId = orderDTO.getCouponId();
        if (couponId != null){
            Coupon coupon = this.couponRepository.findById(couponId)
                    .orElseThrow(()-> new NotFoundException(40004));
            UserCoupon userCoupon = this.userCouponRepository
                    .findFirstByUserIdAndCouponIdAndStatus(uid, couponId, 1)
                    .orElseThrow(()->new NotFoundException(50006));
            couponChecker = new CouponChecker(coupon, userCoupon, iMoneyDiscount);
        }

        OrderChecker orderChecker = new OrderChecker(
                orderDTO,
                skuList,
                couponChecker,
                maxSkuLimit
                );

        orderChecker.isOK();
        return orderChecker;
    }


    /**
     * 2. 下单, 减库存, 核销优惠券,
     * 把数据加入延迟数据队列(用被动的方式,让redis通知库存的归还)
     *
     * 调用数据库模型把order导入数据库
     *
     *
     * 为什么需要加上事务注解?
     * 1. 因为连续更新操作了多张数据表, 这里必须确保整个操作的连续性
     * 2. 任何一次操作失败的话, 就应该恢复到执行之前
     */
    @Transactional //加上事务来确保原子性
    public Long placeOrder(Long uid, OrderDTO orderDTO, OrderChecker orderChecker){
        String orderNo = OrderUtil.makeOrderNo();
        Calendar now = Calendar.getInstance();

        Order order = Order.builder()
                .orderNo(orderNo)
                .totalPrice(orderDTO.getTotalPrice())
                .userId(uid)
                .totalCount(orderChecker.getTotalCount().longValue())
                .snapImg(orderChecker.getLeaderImg())
                .snapTitle(orderChecker.getLeaderTitle())
                .status(OrderStatus.UNPAID.value())
                .expiredTime(CommonUtil.addSomeSeconds(now, this.payTimeLimit).getTime())
                .build();
                //.snapAddress(orderDTO.getAddress()) 如何在builder中处理JSON
        System.out.println("打印生成的Order: "+order);
        //单独处理JSON
        order.setSnapAddress(orderDTO.getAddress());
        order.setSnapItems(orderChecker.getOrderSkuList());
        order.setPlacedTime(CommonUtil.addSomeSeconds(now, -this.payTimeLimit).getTime());
        this.orderRepository.save(order);

        //减库存
        reduceStock(orderChecker);

        //核销优惠券
        Long couponId = -1L; //设置一个默认coupon以确保格式相同
        if(orderDTO.getCouponId() != null){ //是否使用优惠券
            couponId = orderDTO.getCouponId();
            this.writeOffCoupon(orderDTO.getCouponId() ,order.getId(), uid);
        }

        //加入延迟队列
        this.sendToRedis(order.getId(), uid, couponId);

        return order.getId();// 返回给前端
    }


    private void reduceStock(OrderChecker orderChecker){
        List<OrderSku> orderSkuList = orderChecker.getOrderSkuList();

        for(OrderSku orderSku : orderSkuList){
            //如何避免出现负数库存
            int result = this.skuRepository.reduceStock(orderSku.getId(), orderSku.getCount().longValue());
            if(result != 1){
                throw new ParameterException(50003);
            }
        }
    }

    private void writeOffCoupon(Long couponId, Long oid, Long uid){
        int result = this.userCouponRepository.writeOff(couponId, oid, uid);
        if(result != 1){
            throw new ForbiddenException(40012);
        }
    }


    /**
     * 查询unpaid
     */
    public Page<Order> getUnpaid(Integer page, Integer size){
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createTime").descending()
        );
        Long uid = LocalUser.getUser().getId();
        Date now = new Date();
        return this.orderRepository.findByExpiredTimeGreaterThanAndStatusAndUserId(
                now,
                OrderStatus.UNPAID.value(),
                uid,
                pageable
        );
    }

    public Page<Order> getByStatus(Integer status, Integer page, Integer size){
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createTime").descending()
        );
        Long uid = LocalUser.getUser().getId();
        if(status == OrderStatus.All.value()){
            return this.orderRepository.findByUserId(uid,pageable);
        }
        return this.orderRepository.findByUserIdAndStatus(uid, status, pageable);
    }


    public Optional<Order> getOrder(Long oid){
        Long uid = LocalUser.getUser().getId();
        return this.orderRepository.findFirstByUserIdAndId(uid, oid);
    }

    /**
     * Redis相关
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate; //需要导入依赖

    /**
     * 写入一个key, 并指定过期时间
     */
    public void sendToRedis(Long oid, Long uid, Long couponId){
        String key = uid.toString()+","+oid.toString()+","+couponId.toString();
        try{
            stringRedisTemplate.opsForValue().set(key, "1", this.payTimeLimit, TimeUnit.SECONDS);
        }catch(Exception e){
            // 考虑Redis宕机可能, 不过整个下单流程是在一个事务里, 这里不希望抛出异常
            e.printStackTrace();
            // 计入日志,或者给CMD运维人员发送通知等等
        }
    }

    /**
     * 接受redis的通知
     */
}
