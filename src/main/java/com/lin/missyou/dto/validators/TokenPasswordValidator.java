package com.lin.missyou.dto.validators;

import com.lin.missyou.dto.PersonDTO;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 要校验的密码是String, 所以<TokenPassword, String>
 */

public class TokenPasswordValidator implements ConstraintValidator<TokenPassword, String> {

    /**
     * 获取注解中的参数
     * @param constraintAnnotation
     */
    private Integer min;
    private Integer max;
    @Override
    public void initialize(TokenPassword constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)){
            return true; //密码为空时认为可能是小程序的免密登录
        }
        //不为空时, 认为不是小程序登录, 则进行验证
        return s.length() >= this.min && s.length() <= this.max;
    }
}
