package com.lin.missyou.dto.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented//将注释也加入文档
@Retention(RetentionPolicy.RUNTIME)//保留至运行阶段
@Target({ElementType.TYPE, ElementType.FIELD})//这个注解可以运行到那些对象上
@Constraint(validatedBy = TokenPasswordValidator.class)//传入元类进行关联
public @interface TokenPassword {
    String message() default "字段不符合要求";
    int min() default 6;
    int max() default 32;

    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};

}
