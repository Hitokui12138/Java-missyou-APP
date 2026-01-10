package com.lin.missyou.api.v1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lin.missyou.dto.TokenDTO;
import com.lin.missyou.dto.TokenGetDto;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.model.Banner;
import com.lin.missyou.service.WxAuthenticationService;
import com.lin.missyou.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("token")
@RestController
public class TokenController {
    @Autowired
    private WxAuthenticationService wxAuthenticationService;

    /**
     * 机密数据要用POST
     * 哪怕只是一个String, 返回前端时最好也是Map
     * 原则上返回前端的只能是JSON
     * @param userData
     * @return
     */
    @PostMapping("")
    public Map<String, String> getToken(
            @RequestBody @Validated TokenGetDto userData
    ){
        System.out.println("微信Code:"+userData.getAccount());
        Map<String, String> map = new HashMap<>();
        String token = null;
        switch (userData.getType()){
            case USER_WX:
                token = wxAuthenticationService.code2Session(userData.getAccount());
                break;
            case USER_EMAIL:
                //去数据库对比账号密码
                break;
            default:
                throw new NotFoundException(10003);
        }
        map.put("token", token);
        return map;
    }

    /**
     * 用于让小程序工具来测试
     */
    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody TokenDTO token){
        Map<String, Boolean> map = new HashMap<>();
        Boolean valid = JwtToken.verifyToken(token.getToken());
        map.put("is_valid", valid);
        return map;
    }
}
