package com.lin.missyou.api.v1;

import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.model.Activity;
import com.lin.missyou.service.ActivityService;
import com.lin.missyou.vo.ActivityCouponVO;
import com.lin.missyou.vo.ActivityPureVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("activity")
public class ActivityController {

    @Autowired
    ActivityService activityService;

    /**
     * 取得某个Activity
     * @param name
     * @return
     */
    @GetMapping("/name/{name}")
    public ActivityPureVO getHomeActivity(@PathVariable String name){
        Activity activity = activityService.getByName(name);
        if(activity == null){
            throw new NotFoundException(40001);
        }
        ActivityPureVO vo = new ActivityCouponVO(activity);
        return vo;
    }


    /**
     * 获取Activity点进去后的Coupon列表
     * "name/{name}/with_coupon"
     * ActivityCouponVO extends ActivityPureVO 二级属性精简
     */

    @GetMapping("/name/{name}/with_coupon")
    public ActivityCouponVO getActivityWithCoupon(@PathVariable String name){
        Activity activity = activityService.getByName(name);
        if(activity == null){
            throw new NotFoundException(40001);
        }
        return new ActivityCouponVO(activity);
    }
}
