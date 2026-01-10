package com.lin.missyou.core.interceptors;

import com.auth0.jwt.interfaces.Claim;
import com.lin.missyou.core.LocalUser;
import com.lin.missyou.exception.http.ForbiddenException;
import com.lin.missyou.exception.http.UnAuthenticatedException;
import com.lin.missyou.model.User;
import com.lin.missyou.service.UserService;
import com.lin.missyou.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 像是其他框架的拦截器只能做到main()入口方法里
 * 但Spring提供松散编程机制
 * 两种方案:
 *  1. implements HandlerInterceptor
 *  2. extends HandlerInterceptorAdapter 用^回车覆盖方法
 *
 *
 *  关于@Component没生效的问题去看InterceptorConfiguration
 */
//@Component//因为里面用到@Autowired
public class PermissionInterceptor extends HandlerInterceptorAdapter {
    //用于取得用户信息
    @Autowired
    UserService userService;

    /**
     * 1. 进入Controller之前
     * 这里的返回值boolean, 当为true时表示这个是公共API, 无需对比权限
     * 想拒绝访问则抛出异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println();
        System.out.println("-------------------------------------------------------");
        System.out.println(LocalDateTime.now() +": 访问了 "+ request.getRequestURI());
        // 1. 取得API的ScopeLevel
        Optional<ScopeLevel> scopeLevel = this.getScopeLevel(handler);
        if (!scopeLevel.isPresent()){
            return true;
        }
        // 2. 获取前端的令牌
        /*
        前端的Header里面是一个个键值对
        约定俗成token要放在“Authorization”这个键下面
        格式: Authorization:Bearer <token>
         */
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.isEmpty(bearerToken)){
            throw new UnAuthenticatedException(10004);
        }
        // 3. JWT的标准 `Authorization:Bearer <token>`
        if(!bearerToken.startsWith("Bearer")){
            throw new UnAuthenticatedException(10004);
        }
        //3.1 小程序测试工具BUG: 数组越界异常
        String tokens[] = bearerToken.split(" ");
        if(!(tokens.length == 2)){
            throw new UnAuthenticatedException(10004);
        }
        String token = tokens[1];

        //4. 提取JWT令牌, 如果有加密则还需要解密, 比较过期时间等等
        Optional<Map<String, Claim>> optionalMap = JwtToken.getClaims(token);
        Map<String, Claim> map = optionalMap.orElseThrow(
                () -> new UnAuthenticatedException(10004)
        );
        boolean isValid = this.hasPermission(scopeLevel.get(), map);

        //5. 验证通过后用uid查询数据库获取LocalUser
        if(isValid){
            // LocalUser类的
            this.setThreadLocal(map);
        }
        return isValid;
    }

    //2. 在覆盖页面之前,提供一个修改ModelAndView的机会
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    //3. 常用于最后清理资源
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LocalUser.clear();// 请求结束时,清除该用户的信息
        super.afterCompletion(request, response, handler, ex);
    }

    /**
     * 返回值是注解?
     * 参数参考上面几个方法
     * 获取注解值的方法
     */
    private Optional<ScopeLevel> getScopeLevel(Object handler){
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取Scope注解
            ScopeLevel scopeLevel = handlerMethod.getMethod()
                    .getAnnotation(ScopeLevel.class);
            if(scopeLevel == null){
                return Optional.empty();
            }
            return Optional.of(scopeLevel); //多用of, 为空直接报错, 不要让null蔓延
        }
        return Optional.empty();
    }

    /**
     *
     */
    private boolean hasPermission(ScopeLevel scopeLevel, Map<String, Claim> map){
        Integer apiLevel = scopeLevel.value();
        Integer userScope = map.get("scope").asInt();//Claim自带一些转型方法
        if(apiLevel > userScope){ //相同时则允许访问
            throw new ForbiddenException(10005);
        }
        return true;
    }


    /**
     * 获取用户uid
     */
    private void setThreadLocal(Map<String, Claim> map){
        Long uid = map.get("uid").asLong();
        Integer scope = map.get("scope").asInt();
        User user = userService.getUserById(uid);
        LocalUser.set(user, scope);
    }
}
