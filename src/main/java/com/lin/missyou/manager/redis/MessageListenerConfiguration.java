package com.lin.missyou.manager.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

/**
 * 让定义的 TopicMessageListener 生效
 */
@Configuration
public class MessageListenerConfiguration {
    @Value("${spring.redis.listen-pattern}")
    public String pattern;

    @Bean //让这个方法的返回的对象加入Ioc容器
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory redisConnection){
        // 这个container负责链接redis服务器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnection);

        // 决定监听主题topic
        Topic topic = new PatternTopic(this.pattern);

        // 把创建的监听类和topic加入container
        container.addMessageListener(new TopicMessageListener(), topic);

        return container;
    }
}
