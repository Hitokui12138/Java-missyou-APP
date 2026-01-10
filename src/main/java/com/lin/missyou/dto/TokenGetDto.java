package com.lin.missyou.dto;

import com.lin.missyou.core.enumeration.LoginType;
import com.lin.missyou.dto.validators.TokenPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * password: 一些情况可以为空
 * LoginType(枚举): 账号密码, 邮箱, 第三方, 手机号
 */
@Getter
@Setter
public class TokenGetDto {
    @NotBlank(message = "account不能为空")
    private String account;
    @TokenPassword(max = 30, message = "{token.password}")
    private String password;
    private LoginType type;
}
