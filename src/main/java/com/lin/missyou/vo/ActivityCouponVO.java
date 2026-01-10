package com.lin.missyou.vo;

import com.lin.missyou.model.Activity;
import com.lin.missyou.model.Coupon;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity -> Category -> Coupon
 * 想办法去掉Coupon里面的Category信息
 */
@Getter
@Setter
public class ActivityCouponVO extends ActivityPureVO{
    private List<CouponPureVO> coupon; //想要没有CategoryList的纯Coupon信息

    /**
     * 在构造函数里写一些基本的转化
     * 把CouponList转为CouponPureVOList
     * @param activity
     */
    public ActivityCouponVO(Activity activity){
        super(activity);//继承另一个类, 而不是一个个set
        this.coupon = activity.getCouponList().stream()
                .map(c -> {return new CouponPureVO(c);})
                .collect(Collectors.toList());
    }
}
