package com.lin.missyou.logic;

import com.lin.missyou.bo.SkuOrderBO;
import com.lin.missyou.core.enumeration.CouponType;
import com.lin.missyou.core.money.IMoneyDiscount;
import com.lin.missyou.exception.http.ForbiddenException;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.model.Coupon;
import com.lin.missyou.model.Sku;
import com.lin.missyou.model.UserCoupon;
import com.lin.missyou.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于校验订单里的优惠券相关逻辑
 */
public class CouponChecker {
    private Coupon coupon;
    private UserCoupon userCoupon;
    private IMoneyDiscount iMoneyDiscount;
    /**
     * 两个构造函数?
     * 第一个是
     */
    public CouponChecker(Coupon coupon, UserCoupon userCoupon, IMoneyDiscount iMoneyDiscount){
        this.coupon = coupon;
        this.userCoupon = userCoupon;
        this.iMoneyDiscount = iMoneyDiscount;
    }

//    /**
//     * 自己完成查询工作
//     */
//    public CouponChecker(Long couponId, Long uid){
//
//    }

    /**
     * 主要校验是否过期
     */
    public void isOk(){
        Date now = new Date();
        Boolean isInTimeLine = CommonUtil.isInTimeLine(now,
                coupon.getStartTime(),
                coupon.getEndTime());
        if(!isInTimeLine){
            throw new ForbiddenException(40007);
        }
    }

    /**
     * BigDecimal本质上是字符串, 只有字符串才能精确表示浮点数
     */
    public void finalTotalPriceIsOk(BigDecimal orderFinalTotalPrice,
                                    BigDecimal serverTotalPrice){
        BigDecimal serverFinalTotalPrice = null;
        switch (CouponType.toType(this.coupon.getType())){
            case FULL_MINUS:
            case NO_THRESHOLD_MINUS: //合并这两个分支
                //serverFinalTotalPrice = serverTotalPrice - this.coupon.getMinus();
                serverFinalTotalPrice = serverTotalPrice.subtract(this.coupon.getMinus());
                break;
            case FULL_OFF: //涉及浮点数运算
                serverFinalTotalPrice = this.iMoneyDiscount.discount(serverTotalPrice,
                        this.coupon.getRate());
                break;
//            case NO_THRESHOLD_MINUS:
//                serverFinalTotalPrice = serverTotalPrice.subtract(this.coupon.getMinus());
//                //这里应该再加一个折扣后价格是否小于等于0的判断
//                break;
            default:
                throw new ForbiddenException(40009);

        }
        //与前端计算的价格进行比较
        int compare = serverFinalTotalPrice.compareTo(orderFinalTotalPrice);
        if(compare != 0){
            throw new ForbiddenException(50008);
        }

    }

    /**
     * 优惠券种类, 金额是否满足等等
     * 需要有sku的price,count,所属的categoryId
     * 然后需要优惠券的couponCategory
     *
     * serverTotalPrice 服务端计算总价
     */
    public void canBeUsed(List<SkuOrderBO> skuOrderBOList, BigDecimal serverTotalPrice){
        BigDecimal orderCategoryPrice;//
        if(this.coupon.getWholeStore()){
            //如果是全场券,则直接等于总价格
            orderCategoryPrice = serverTotalPrice;
        }else{
            //取得cidList(当前优惠券所能使用的所有品类)
            List<Long> cidList = this.coupon.getCategoryList()
                    .stream().map(c -> c.getId())
                    .collect(Collectors.toList());
            orderCategoryPrice = this.getSumByCategoryList(cidList, skuOrderBOList);
        }
        this.couponCanBeUsed(orderCategoryPrice);
    }

    private void couponCanBeUsed(BigDecimal orderCategoryPrice){
        switch (CouponType.toType(this.coupon.getType())){
            case FULL_OFF:
            case FULL_MINUS:
                //优惠券门槛和分类的orderPrice做比较
                int compare = this.coupon.getFullMoney().compareTo(orderCategoryPrice);
                if(compare > 0){
                    throw new ParameterException(40008);
                }
                break;
            default:
                throw new ParameterException(40009);
        }
    }

    /**
     * 计算多个Category下所有sku的价格综合
     */
    private BigDecimal getSumByCategoryList(List<Long> cidList, List<SkuOrderBO> skuOrderBOList){
        BigDecimal sum = cidList.stream()
                .map(cid -> this.getSumByCategory(cid, skuOrderBOList))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
        return sum;
    }

    /**
     * 计算一个Category下所有sku的价格综合
     */
    private BigDecimal getSumByCategory(Long cid, List<SkuOrderBO> skuOrderBOList){
        BigDecimal sum = skuOrderBOList.stream()
                .filter(sku -> sku.getCategoryId().equals(cid))
                .map(sku -> sku.getTotalPrice())
                .reduce((a, b) -> a.add(b)) //累加 ,另外reduce计算结果是Optional
                .orElse(new BigDecimal("0"));
        return sum;
    }

}
