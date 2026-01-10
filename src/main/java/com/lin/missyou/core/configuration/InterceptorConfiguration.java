package com.lin.missyou.core.configuration;

import com.lin.missyou.core.interceptors.PermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 在这里注册拦截器
 *
 * 为什么@Component不行
 * 因为这里生效的是addInterceptors()里面的new的那个Interceptor, 而不是像定通过@Conponent加入的那个
 * 因此利用@Configureation+@Bean的机制,
 */
//@Component //这个也需要加入容器
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Bean//加入容器
    public HandlerInterceptor getPermissionInterceptor(){
        return new PermissionInterceptor();

    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new PermissionInterceptor());
        registry.addInterceptor(this.getPermissionInterceptor());
    }
}
