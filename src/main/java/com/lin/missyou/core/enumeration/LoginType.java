package com.lin.missyou.core.enumeration;

/**
 * 1. 实例化枚举对象
 * 2. 枚举可以有成员变量和构造函数(必须是private)
 */
public enum LoginType {
    //USER_WX, USER_EMAIL 默认数字是 0,1,2
    USER_WX(0, "微信登录"),
    USER_EMAIL(1, "邮箱登录");

    private Integer value;//枚举可以有成员变量


    //枚举构造函数只能是私有的
    private LoginType(
            int value,
            String description
    ){
        this.value = value;
    }
}
