package com.lin.missyou.core.enumeration;

import com.lin.missyou.service.CouponService;

import java.util.stream.Stream;

public enum CouponStatus {
    AVALIABLE(1,"可以使用"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期");

    private Integer value;
    private String description;

    public Integer getValue(){
        return this.value;
    }
    CouponStatus(Integer value, String description){
        this.value = value;
        this.description = description;
    }

    /**
     * 一种生成枚举类型的Stream方法
     * @param value
     * @return
     */
    public static CouponStatus toType(int value){
        //switch(value) 传统方法, case特别多时使用下面这个方法
        return Stream.of(CouponStatus.values()) //获取所有Status
                .filter(c -> c.value.equals(value))
                .findAny()
                .orElse(null);
    }
}
