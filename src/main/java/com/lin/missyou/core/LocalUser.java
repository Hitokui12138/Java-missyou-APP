package com.lin.missyou.core;

import com.lin.missyou.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于从Token中获取用户相关信息
 * 用拦截器触发这个
 *
 * 1. 静态类的问题: 只能保存数据, 无法保存状态
 * 那么如果同时接收多个API请求, 用户A,B,C 那么一个User对象就无法满足要求
 */
public class LocalUser {
    //考虑使用Map来存储多个用户, hashMap线程不安全, 因此使用ThreadLocal<Map>
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * 往map里面加user
     * 其实threadLocal也类似于Map有KV, 但是Key是内部管理的, 因此只需要传入Value即可使用
     */
    public static void set(User user, Integer scope){
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("scope", scope);
        LocalUser.threadLocal.set(map);
    }

    /**
     * 拦截器的AfterCompetuion很适合调用clear方法
     * 释放当前线程资源
     */
    public static void clear(){
        LocalUser.threadLocal.remove();
    }

    public static User getUser(){
        Map<String, Object> map = LocalUser.threadLocal.get();
        User user = (User) map.get("user");
        System.out.println("用户ID为: "+ user.getId());
        return user;
    }

    public static Integer getScope(){
        Map<String, Object> map = LocalUser.threadLocal.get();
        Integer scope = (Integer) map.get("scope");
        return scope;
    }
}
