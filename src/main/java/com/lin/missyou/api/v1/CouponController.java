package com.lin.missyou.api.v1;

import com.lin.missyou.core.LocalUser;
import com.lin.missyou.core.enumeration.CouponStatus;
import com.lin.missyou.core.interceptors.ScopeLevel;
import com.lin.missyou.exception.CreateSuccess;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.model.Coupon;
import com.lin.missyou.model.User;
import com.lin.missyou.service.CouponService;
import com.lin.missyou.vo.CouponCategoryVO;
import com.lin.missyou.vo.CouponPureVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("coupon")
public class CouponController {
    @Autowired
    CouponService couponService;

    /**
     * 查看某商品可用的优惠券
     * @param cid
     * @return
     */
    @GetMapping("/by/category/{cid}")
    public List<CouponPureVO> getCouponListByCategory(
            @PathVariable Long cid){
        List<Coupon> coupons = couponService.getByCategory(cid);
        if(coupons.isEmpty()){
            return Collections.emptyList();
        }
        List<CouponPureVO> vos = CouponPureVO.getList(coupons);
        return vos;
    }


    @GetMapping("/whole_store")
    public List<CouponPureVO> getWholeStoreCouponList(){
        List<Coupon> coupons = couponService.getWholeStoreCoupons();
        if(coupons.isEmpty()){
            return Collections.emptyList();
        }
        List<CouponPureVO> vos = CouponPureVO.getList(coupons);
        return vos;
    }

    /**
     * 领取优惠券API, 需要权限操作
     * 谁领取? 用户id, 但用户id不应该显示传入后段(权限问题), 生成的token里面有用户的id,直接获取
     * 领取哪个? 优惠券id
     */
    @ScopeLevel()
    @PostMapping("collect/{id}")
    public void collectCoupon(@PathVariable Long id){
        Long uid = LocalUser.getUser().getId();
        couponService.collectOneCoupon(uid, id);
        //该返回什么样的数据给前端? 应该只要告诉前端操作成功即可
        throw new CreateSuccess(0);
    }

    /**
     * 根据状态取得已领取的优惠券列表
     * 考虑Status没能更新成功的状态
     * 1. 领取优惠券 Status: "" -> 1
     * 2. 下单成功 1 -> 2
     * 3. 已过期? 2 -> 3, 如何触发?
     *
     */
    @ScopeLevel()
    @GetMapping("myself/by/status/{status}")
    public List<CouponPureVO> getMyCouponByStatus(@PathVariable Integer status){
        Long uid = LocalUser.getUser().getId();
        List<Coupon> couponList;
        switch(CouponStatus.toType(status)){
            case AVALIABLE:
                couponList = couponService.getMyAvailableCoupons(uid);
                break;
            case USED:
                couponList = couponService.getMyUsedCoupons(uid);
                break;
            case EXPIRED:
                couponList = couponService.getMyExpiredCoupons(uid);
                break;
            default:
                throw new ParameterException(40001);
        }
        return CouponPureVO.getList(couponList);
    }

    /**
     * 用于结账时,显示所选商品的可用优惠券
     * 有了分类, 才能计算满减条件
     */
    @ScopeLevel()
    @GetMapping("myself/available/with_category")
    public List<CouponCategoryVO> getUserCouponWithCategory(){
        User user = LocalUser.getUser();
        List<Coupon> coupons = couponService.getMyAvailableCoupons(user.getId());
        if(coupons.isEmpty()){
            return Collections.emptyList();
        }
        return coupons.stream().map(coupon -> {
            CouponCategoryVO vo = new CouponCategoryVO(coupon);
            return vo;
        }).collect(Collectors.toList());
    }

}
