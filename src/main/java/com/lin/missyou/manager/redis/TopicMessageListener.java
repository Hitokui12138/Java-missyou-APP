package com.lin.missyou.manager.redis;

import com.lin.missyou.bo.OrderMessageBO;
import com.lin.missyou.service.OrderCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class TopicMessageListener implements MessageListener {
    @Autowired
    private OrderCancelService orderCancelService;

    /**
     * 定义一个监听器, 用于监听redis的通知
     * @param message
     * @param bytes
     */
    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();

        String expiredKey = new String(body);
        String topic = new String(channel);

        System.out.println("Redis已生效: ");
        System.out.println(expiredKey);
        System.out.println(channel);

        /**
         * 这里还有一种写法
         * TopListener使用publish()仅在app中发布消息
         * 在OrderService中使用@EventListener来接受发布的信息
         */

        //归还库存
        OrderMessageBO messageBO = new OrderMessageBO(expiredKey);
        orderCancelService.cancel(messageBO);
        // 归还优惠券

    }
}
