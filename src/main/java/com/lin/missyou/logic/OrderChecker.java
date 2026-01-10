package com.lin.missyou.logic;

import com.lin.missyou.bo.SkuOrderBO;
import com.lin.missyou.dto.OrderDTO;
import com.lin.missyou.dto.SkuInfoDTO;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.model.OrderSku;
import com.lin.missyou.model.Sku;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderService->Orderchecker->CouponChecker
 * facade 设计模式: 外观模式
 *
 * 1. 校验
 * 2. 把校验好的数据整理成一个可以导入数据库的Order,返回给调用方
 * 3. 调用CouponChecker
 */
public class OrderChecker {
    private OrderDTO orderDTO;
    private List<Sku> serverSkuList;
    private CouponChecker couponChecker;
    private Integer maxSkuLimit;
    @Getter
    private List<OrderSku> orderSkuList = new ArrayList<>();
    public OrderChecker(OrderDTO orderDTO, List<Sku> serverSkuList,
                        CouponChecker couponChecker, Integer maxSkuLimit){
        this.orderDTO = orderDTO;
        this.serverSkuList = serverSkuList;
        this.couponChecker = couponChecker;
        this.maxSkuLimit = maxSkuLimit;
    }

    // 生成的订单需要这个
    public String getLeaderImg(){
        return this.serverSkuList.get(0).getImg();
    }
    public String getLeaderTitle(){
        return this.serverSkuList.get(0).getTitle();
    }
    public Integer getTotalCount(){
        return this.orderDTO.getSkuInfoList()
                .stream()
                .map(SkuInfoDTO::getCount)
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * 校验一下Order的总价(原价)和Server的总价
     * 某些Spu可能已经下架?可能已经售罄?
     * 单人购买上限,
     */
    public void isOK(){
        //0. CouponChecker需要的数据
        BigDecimal serverTotalPrice = new BigDecimal("0");
        List<SkuOrderBO> skuOrderBOList = new ArrayList<>();

        //1. 是否售罄
        this.skuNotOnSale(orderDTO.getSkuInfoList().size(),
                serverSkuList.size());
        for(int i=0; i< this.serverSkuList.size(); i++){
            Sku sku = this.serverSkuList.get(i);
            SkuInfoDTO skuInfoDTO = this.orderDTO.getSkuInfoList().get(i);
            this.containsSoldOutSku(sku);
            this.beyondSkuStock(sku, skuInfoDTO);
            this.beyondMaxSkuLimit(skuInfoDTO);

            serverTotalPrice = serverTotalPrice.add(this.calculateSkuOrderPrice(sku, skuInfoDTO));
            skuOrderBOList.add(new SkuOrderBO(sku, skuInfoDTO));
            this.orderSkuList.add(new OrderSku(sku, skuInfoDTO));
        }

        this.totalPriceIsOk(orderDTO.getTotalPrice(), serverTotalPrice);

        //优惠券校验
        if(this.couponChecker != null){
            this.couponChecker.isOk();
            this.couponChecker.canBeUsed(skuOrderBOList, serverTotalPrice);
            this.couponChecker.finalTotalPriceIsOk(orderDTO.getFinalTotalPrice(), serverTotalPrice);
        }
    }

    private void skuNotOnSale(int count1, int count2){
        if(count1 != count2){
            throw new ParameterException(50002);
        }
    }

    private void containsSoldOutSku(Sku sku){
        // 这里只是个预判断, 不能完全依靠这个
        // 这样可以尽快把问题反映给前端
        if(sku.getStock() == 0){
            throw new ParameterException(50001);
        }
    }

    private void beyondSkuStock(Sku sku, SkuInfoDTO skuInfoDTO){
        if(sku.getStock() < skuInfoDTO.getCount()){
            throw new ParameterException(50003);
        }
    }

    private void beyondMaxSkuLimit(SkuInfoDTO skuInfoDTO){
        if(skuInfoDTO.getCount() > this.maxSkuLimit){
            throw new ParameterException(50004);
        }
    }

    private BigDecimal calculateSkuOrderPrice(Sku sku, SkuInfoDTO skuInfoDTO){
        if(skuInfoDTO.getCount() <= 0){
            throw new ParameterException(50007);
        }
        return sku.getActualPrice().multiply(new BigDecimal(skuInfoDTO.getCount()));
    }

    private void totalPriceIsOk(BigDecimal orderTotalPrice, BigDecimal serverTotalPrice){
        if(orderTotalPrice.compareTo(serverTotalPrice)!=0){
            throw new ParameterException(50005);
        }
    }




}
