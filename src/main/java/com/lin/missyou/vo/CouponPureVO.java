package com.lin.missyou.vo;

import com.lin.missyou.model.Coupon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 去掉Coupon的CategoryList
 */
@Getter
@Setter
@NoArgsConstructor
public class CouponPureVO {
    private Long id;
    private String title;
    private Date startTime; //可以使用的时间
    private Date endTime; // 截止时间
    private String description;
    private BigDecimal fullMoney; // 满减券的满的金额
    private BigDecimal minus; // 减多少
    private BigDecimal rate; // 用于折扣券, 打折rate
    private String remark; // 也是description, 用于前端显示用
    private Boolean wholeStore; // 是不是全场券?
    private Integer type; // 1.满减, 2.折扣, 3.无门槛, 4.满金额折扣

    public CouponPureVO(Coupon coupon){
        BeanUtils.copyProperties(coupon, this);
    }

    public static List<CouponPureVO> getList(List<Coupon> coupons){
//        List<CouponPureVO> cpos = new ArrayList<>();
//        coupons.forEach(c -> {
//            cpos.add(new CouponPureVO(c));
//        });
//        return cpos;
        return coupons.stream()
                .map(CouponPureVO::new)
                .collect(Collectors.toList());
    }
}
