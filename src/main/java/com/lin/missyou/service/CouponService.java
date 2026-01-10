package com.lin.missyou.service;

import com.lin.missyou.core.enumeration.CouponStatus;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.model.Activity;
import com.lin.missyou.model.Coupon;
import com.lin.missyou.model.UserCoupon;
import com.lin.missyou.repository.ActivityRepository;
import com.lin.missyou.repository.CouponRepository;
import com.lin.missyou.repository.UserCouponRepository;
import com.lin.missyou.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CouponService {
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserCouponRepository userCouponRepository;

    /**
     * 注意, 查出来的优惠券不能是过期的
     * @param cid
     * @return
     */
    public List<Coupon> getByCategory(Long cid){
        Date now = new Date();
        return couponRepository.findByCategory(cid, now);
    }

    public List<Coupon> getWholeStoreCoupons(){
        Date now = new Date();
//        return couponRepository.findByWholeStoreAndStartTimeLessThanAndEndTimeGreaterThan(
//                true, now, now);
        return couponRepository.findByWholeStore(true, now);
    }

    /**
     * 领取一张优惠券
     * UserCoupon那个有业务意义的第三张表可以协助完成领取coupon的操作
     * 1. 校验couponId是否存在, 有没有过期(在Activity上记录)
     */
    public void collectOneCoupon(Long uid, Long couponId){
        //1. 是否存在
        this.couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException(40003));
        //2. 是否过期, 有效日期在Activity上保存
        Activity activity = this.activityRepository.findByCouponListId(couponId)
                .orElseThrow(() -> new NotFoundException(40010));
        Date now = new Date();
        Boolean isIn = CommonUtil.isInTimeLine(now,
                activity.getStartTime(),
                activity.getEndTime());
        if(!isIn){
            throw new ParameterException(40005);
        }
        //3. 将用户和优惠券写入,若存在, 则说明已经领取了
        this.userCouponRepository.findFirstByUserIdAndCouponId(uid, couponId)
                .ifPresent((uc)->{throw new ParameterException(40006);});
        UserCoupon userCouponNew = UserCoupon.builder()
                .userId(uid)
                .couponId(couponId)
                .status(CouponStatus.AVALIABLE.getValue())
                .createTime(now)
                .build();
        userCouponRepository.save(userCouponNew);
    }

    /**
     * 按Status查找已领取的优惠券
     */
    public List<Coupon> getMyAvailableCoupons(Long id){
        Date now = new Date();
        return couponRepository.findMyAvailable(id, now);
    }
    public List<Coupon> getMyUsedCoupons(Long id){
        Date now = new Date();
        return couponRepository.findMyUsed(id, now);
    }
    public List<Coupon> getMyExpiredCoupons(Long id){
        Date now = new Date();
        return couponRepository.findMyExpired(id, now);
    }
}
