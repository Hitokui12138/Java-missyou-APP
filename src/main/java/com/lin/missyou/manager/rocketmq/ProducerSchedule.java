package com.lin.missyou.manager.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 如果要在构造函数里实例化一个对象的话, 可能读取不到配置文件里的值
 * 此时@Value可能为空值
 *
 * 之前用@Autowired注入的时候, A,B都需要加入容器, A又依赖于B
 * 如果要把B注入进A中, 就要求A必须先实例化完成, 类似@Value也是如此
 *
 * 之前给一个静态成员变量注入一个对象时, 当时使用了Setter
 *
 *
 * SpringBoot考虑过这个问题, 使用@PostConstruct
 *
 * 总结: 如果你想在实例化时做一些事情, 尤其是需要依赖注入的东西@Value@Autowired的东西时
 * 最好写一个自定义方法加上@PostConstruct
 *
 * 顺序:
 * A的Constructor -》 @Autowired/@Value B -》@PostConstruct
 */
@Component
public class ProducerSchedule {
    private DefaultMQProducer producer;

    @Value("${rocketmq.producer.producer-group}")
    private String producerGroup;

    @Value("${rocketmq.namesrv-addr}")
    private String namesrvAddr;

    public ProducerSchedule() {
        // 容器实例化这个对象时, 肯定会调用构造函数
        //构造函数中无法取得@Value的值, 因此把这个逻辑放在外面
        //this.producer = new DefaultMQProducer(this.producerGroup);
        //this.producer.setNamesrvAddr(this.namesrvAddr);
    }

    /**
     * SpringBoot提供@PostConstruct来应对这种方法
     * SpringBoot会在构造函数之后执行这个方法
     */
    @PostConstruct
    public void defaultMQProducer(){
        if(this.producer == null){
            this.producer = new DefaultMQProducer(this.producerGroup);
            this.producer.setNamesrvAddr(this.namesrvAddr);
        }
        try{
            this.producer.start();
            System.out.println("RocketMQ的生产者已启动.");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public String send(String topic, String messageText) throws Exception {
        // 转为byte数组
        Message message = new Message(topic, messageText.getBytes());
        // 还需要设置一个延时级别(1s,5s,10s,30s,1m,2m,3m,4m,5m,6m,7m,8m,9m,10m,20m,30m,1h..)
        message.setDelayTimeLevel(4); //30s
        // 发送到队列, 有一个结果类可以接受结果
        SendResult result = producer.send(message);
        System.out.println("RocketMQ生产者已发送信息, ID: " + result.getMsgId());
        return result.getMsgId();
    }
}
