package com.lin.missyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.missyou.exception.http.ParameterException;
import com.lin.missyou.model.User;
import com.lin.missyou.repository.UserRepository;
import com.lin.missyou.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WxAuthenticationService {
    /**
     * 读取配置文件中的appid等信息
     * code2session: 微信验证用URL
     */
    @Value("${wx.code2session}")
    private String code2session;
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.appsecret}")
    private String appsecret;

    //用于反序列化
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    /**
     *
     * @param code ACCOUNT
     * @return JWT
     */
    public String code2Session(String code){
        /*
        1. 给微信API发送请求,验证用户身份 RestTemplate
        2. 成功时颁布Token
         */
        String url = MessageFormat.format(
                this.code2session,
                this.appid,
                this.appsecret,
                code
        );

        /**
         * 使用RestTemplate发送REST请求
         */
        RestTemplate rest = new RestTemplate();
        String sessionText =  rest.getForObject(
                url,
                String.class   //传入的是一个JSON字符串
        );

        System.out.println(sessionText);

        /**
         * code码->openid
         * 将返回结果反序列化, 或者用Map简单处理
         * session_key:
         * openid: 微信标识当前小程序,当前用户
         * unionId: 可以适用于多个小程序, 但有另外的获取方法
         *
         *
         * 但考虑到安全性,这个openid不能返回给客户端
         * 数据库中有保存这个用户的uid
         * 应该把openid保存到数据库里(注册), 然后把对应的uid传到客户端
         * uid->写入jwt, jwt发送给前端
         */
        Map<String, Object> session = new HashMap<>();
        try {
            // 将返回结果反序列化成Map
            session = mapper.readValue(sessionText, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return this.registerUser(session);

    }


    /**
     *
     * @param session
     * @return
     */
    private String registerUser(Map<String, Object> session){
        String openid = (String) session.get("openid");
        if(openid == null){
            throw new ParameterException(20004);
        }
        /**
         * 注册或者登录
         * 不过像这种无论User是否存在,都需要执行很多东西的逻辑不适合Java8的Optional
         * 需要用到Java9的ifPresentOrElse(Consumer, Runnable)
         */
        Optional<User> optionalUser = this.userRepository.findByOpenid(openid);
//        optionalUser.ifPresentOrElse(
//                //返回JWT令牌
//                o->{ },
//                //返回
//                o->{return ""; });
        if(optionalUser.isPresent()){
            // 用户已经注册时
            return JwtToken.makeToken(optionalUser.get().getId());
        }
        // 用户未注册时, 先注册, 再返回
        User user = User.builder().openid(openid).build();
        userRepository.save(user);
        Long uid = user.getId();
        return JwtToken.makeToken(uid);
    }


}
