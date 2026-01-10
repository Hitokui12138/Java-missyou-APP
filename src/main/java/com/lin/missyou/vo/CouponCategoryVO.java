package com.lin.missyou.vo;

import com.lin.missyou.api.v1.CouponController;
import com.lin.missyou.model.Category;
import com.lin.missyou.model.Coupon;

import java.util.ArrayList;
import java.util.List;

/**
 * Coupon自带Category导航属性
 */

public class CouponCategoryVO extends CouponPureVO{
    private List<CategoryPureVO> categories = new ArrayList<>();

    public CouponCategoryVO(Coupon coupon){
        super(coupon);
        List<Category> categories = coupon.getCategoryList();
        categories.forEach(category -> {
            CategoryPureVO vo = new CategoryPureVO(category);
            this.categories.add(vo);
        });
    }
}
