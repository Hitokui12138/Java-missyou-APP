package com.lin.missyou.repository;

import com.lin.missyou.model.Activity;
import com.lin.missyou.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Activity findByName(String name);


    /**
     * 用JPA命名方法和导航属性来实现SELELCT AVTIVITY JOIN ON COUPON
     * @param couponId
     * @return
     */
    Optional<Activity> findByCouponListId(Long couponId);
}
