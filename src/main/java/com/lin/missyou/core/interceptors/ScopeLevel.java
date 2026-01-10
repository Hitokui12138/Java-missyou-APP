package com.lin.missyou.core.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//保留至运行阶段
@Target({ElementType.TYPE, ElementType.METHOD})//这个注解可以运行到那些对象上
public @interface ScopeLevel {
    int value() default 4;
}
