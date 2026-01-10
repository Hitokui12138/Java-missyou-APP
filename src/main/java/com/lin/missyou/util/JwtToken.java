package com.lin.missyou.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 主流库: java-jwt, Auth0
 * 可以看看JWTio的网站, 看看各个语言不同的主流JWT库
 */
@Component
public class JwtToken {

    private static Integer defaultScope = 8;

    /**
     * 如何对一个静态对象进行注入?
     * 使用setter()
     */
    //@Value(${})
    public static String jwtKey;
    @Value("${missyou.security.jwt-key}")
    public void setJwtKey(String jwtKey){
        JwtToken.jwtKey = jwtKey;
    }

    private static Integer expireTimeIn;
    @Value("${missyou.security.token-expired-in}")
    public void setJwtKey(Integer expireTimeIn){
        JwtToken.expireTimeIn = expireTimeIn;
    }


    public static String makeToken(
            Long uid,
            Integer scope //权限分级
    ){
        return JwtToken.getToken(uid, scope);
    }

    public static String makeToken(
            Long uid
    ){
        return JwtToken.getToken(uid, JwtToken.defaultScope);
    }

    private static String getToken(
            Long uid,
            Integer scope
    ){
        /**
         * 1. 选择算法
         * 需要传入一个随机字符串
         * 加密算法的yan?
         */
        Algorithm algorithm = Algorithm.HMAC256(JwtToken.jwtKey);
        Map<String, Date> map = JwtToken.calculateExpiredIssue();
        String token = JWT.create() //也是一种builder模式
                .withClaim("uid", uid) //指定claim的K和V
                .withClaim("scope", scope)
                .withExpiresAt(map.get("expiredTime")) //这里需要一个Date类型
                .withIssuedAt(map.get("now")) //再给一个签发时间
                .sign(algorithm); //指定算法
        return token;
    }

    /**
     * 计算过期时间
     * 推荐使用Calendar类
     */
    private static Map<String, Date> calculateExpiredIssue(){
        Map<String, Date> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.SECOND, JwtToken.expireTimeIn);
        map.put("now", now);
        map.put("expiredTime", calendar.getTime());
        return map;
    }


    /**
     * 解析并获取JWT里面的Claims
     * 在这个方法中, 自己尝试返回Optional实例
     * @param token
     * @return
     */
    public static Optional<Map<String, Claim>> getClaims(String token){
        //生成时有指定算法(钥匙), 那么解析时也需要这个钥匙
        Algorithm algorithm = Algorithm.HMAC256(JwtToken.jwtKey);
        // 专门用来解析Token的核心类
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        //校验异常可能会抛出校验失败的异常
        DecodedJWT decodedJWT;
        try{
            decodedJWT = jwtVerifier.verify(token);
        }catch (JWTVerificationException e){ //文档里有写
            // 参数校验之类的由用户引起的异常不必计入日志
            // 也可以在这里分别判断令牌无效或者令牌过期
            //return null; 返回null有空指针风险
            return Optional.empty();
        }
        return Optional.of(decodedJWT.getClaims());
    }

    /**
     * 小程序测试用
     * 不想让小程序每次测试都生成新令牌
     */
    public static Boolean verifyToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(JwtToken.jwtKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        }catch (JWTVerificationException e){
            return false;
        }
        return true;
    }
}
