package com.lin.missyou.manager.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 和Redis类似, 启动监听器
 * 定义了messageListener()方法之后, 谁来调用这个方法?
 * 让SpringBoot来触发这方法的调用
 * 1. 可以和Producer一样在构造函数后调用
 * 2. 也有另一种方式 使用SpringBoot的接口CommandLineRunner
 *
 */
@Component
public class ConsumerSchedule implements CommandLineRunner {

    @Value("${rocketmq.consumer.consumer-group}")
    private String consumerGroup;

    @Value("${rocketmq.namesrv-addr}")
    private String namesrvAddr;
    public void messageListener() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe("testTopic","*");//监听所有消息
        consumer.setConsumeMessageBatchMaxSize(1);//每次消费几条消息

        /*
            注册监听器 参数是 MessageListenerConcurrently 是一个接口, 如何获取这个对象?
            类似于回调函数的写法, 参数是回调的结果,
            也可以写成匿名类那种, new MessageListenerConcurrently(){@Override}
            Byte[]转String
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for(Message message : messages){
                System.out.println("RocketMQ消费者接受到消息: "+ new String(message.getBody()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.println("RocketMQ的消费者已启动.");

    }

    /**
     * 优点: 这个方法能确保只执行一次, 多用于程序启动时初始化资源
     * 可以替代@PostConstruct
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        this.messageListener();
    }
}
