package com.lin.missyou.service;

import com.lin.missyou.dto.TokenGetDto;
import org.springframework.stereotype.Service;

/**
 * Authentication: 登录验证
 * Authorize: 授权
 */
@Service
public class AuthenticationService {
    // 1. Email
    public void getTokenByEmail(TokenGetDto userData){
        System.out.println("数据库对比完毕, 密码正确");
    }
    public void register(TokenGetDto userData){
        System.out.println("已将用户信息登录数据库");
    }

}
