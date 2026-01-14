package com.lin.missyou.api.v1;

import com.lin.missyou.manager.rocketmq.ProducerSchedule;
import com.lin.missyou.repository.OrderRepository;
import com.lin.missyou.repository.UserCouponRepository;
import com.lin.missyou.sample.ISkill;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private ObjectFactory<ISkill> iSkillObjectFactory;

    @Autowired
    private ProducerSchedule producerSchedule;

    /**
     * 测试调用这个方法生产一个消息
     */
    @GetMapping("/push")
    public void pushMessageToMQ(){
        try {
            producerSchedule.send("testTopic", "testOrderId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
