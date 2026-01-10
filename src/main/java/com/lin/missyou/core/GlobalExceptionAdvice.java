package com.lin.missyou.core;

import com.lin.missyou.core.configuration.ExceptionCodeatioConfiguration;
import com.lin.missyou.exception.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 1. 统一捕获异常
 * 2. 把捕获到的异常转换成UnifyResponse,并抛给前端
 */
@ControllerAdvice //标明一个用于处理异常的类
public class GlobalExceptionAdvice {

    /**
     * 读取异常处理配置文件
     */
    @Autowired
    private ExceptionCodeatioConfiguration codeatioConfiguration;

    /**
     * 不推荐的做法, 不推荐直接返回UnifyResponse
     * 通用异常处理方法(处理未知异常),返回UnifyResponse
     * @param req   取得当前请求的URL
     * @param e 取得传入该类的异常信息
     */
//    @ExceptionHandler(value=Exception.class) //具体的处理方法,参数表示通用异常
//    @ResponseBody //Java里面不能直接return对象或者String到页面上,因此加上@ResponseBody
//    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)//需要修改返回的Http状态码
//    public UnifyResponse handleException(HttpServletRequest req, Exception e){
//        String requestUrl = req.getRequestURI();
//        String method = req.getMethod();
//        System.out.println(e);//打印这个异常用来调试,生产环境应当记录日志
//        UnifyResponse unifyResponse = new UnifyResponse(9999,"服务器内部异常",method + " " + requestUrl);
//        return unifyResponse;
//    }

    /**
     * 推荐做法 HTTPException处理
     * 返回ResponseEntity<UnifyResponse>
     * 传入三个参数
     * 1. 序列化返回前端的UnifyResponse
     * 2. HttpHeads
     * 3. HttpsStatus
     * @param req
     * @param e
     */
    @ExceptionHandler(value= HttpException.class)
    public ResponseEntity<UnifyResponse> handleHttpException(HttpServletRequest req, HttpException e){
        System.out.println("HttpException");
        String requestUrl = req.getRequestURI();
        String method = req.getMethod();

        //UnifyResponse是自己定义的
        UnifyResponse unifyResponse = new UnifyResponse(
                e.getCode(),
                codeatioConfiguration.getMessage(e.getCode()),
                method + " " + requestUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);//之前有@ResponseBody,SpringBoot会帮忙设置ContentType
        HttpStatus httpStatus = HttpStatus.resolve(e.getHttpStatusCode());

        // ResponseEntity需要的三个参数
        ResponseEntity<UnifyResponse> r = new ResponseEntity<>(
                unifyResponse,
                httpHeaders,
                httpStatus);
        return r;
    }


    /**
     * 参数异常校验1:MethodArgumentNotValidException
     * 处理HttpBody里的参数校验异常 POST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody//UnifyResponse是自己定义的
    @ResponseStatus(HttpStatus.BAD_REQUEST)//这里不需要动态设置Http状态码
    public UnifyResponse handleBeanValidation(HttpServletRequest req, MethodArgumentNotValidException e){
        System.out.println("参数校验异常");
        String requestUrl = req.getRequestURI();
        String method = req.getMethod();
        //1. 从e里面获得所有message
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        //2. 调用下面的方法
        String messages = this.formatAllErrorMessages(errors);
        //3.还差一个参数错误的错误码
        return new UnifyResponse(10001,messages,method + " " + requestUrl);
    }

    /**
     * 参数异常校验2:ConstraintViolationException //限制,约束
     * 处理URL里面的参数异常 GET
     * 具体应该处理的异常的名称可以从堆栈里面找到
     * 注意不同的Exception获取Message的方式也不同,点进去看看
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody//UnifyResponse是自己定义的
    @ResponseStatus(HttpStatus.BAD_REQUEST)//这里不需要动态设置Http状态码
    public UnifyResponse handleConstraintValidation(HttpServletRequest req, ConstraintViolationException e) {
        System.out.println("URL异常: ConstraintViolationException!");
        //test2.num: ID范围错误,如果controller的名字不想传给前端,可以修改一下
        //处理默认的Message的格式
        StringBuffer errorMsg = new StringBuffer();
        for(ConstraintViolation error : e.getConstraintViolations()){
            String msg = error.getMessage();
            String m = error.getPropertyPath().toString();
            String name = m.split("[.]")[1]; //分成数组,取第二部分
            errorMsg.append(name).append(" : ").append(msg);
        }


        String requestUrl = req.getRequestURI();
        String method = req.getMethod();
        //String message = e.getMessage();//直接取得message
        String message = errorMsg.toString();
        return new UnifyResponse(10001, message,method + " " + requestUrl);
    }





    /**
     * 一个用来拼接Message的方法
     */
    private String formatAllErrorMessages(List<ObjectError> errors){
        StringBuffer errorMsg = new StringBuffer();
        errors.forEach(error -> errorMsg.append(error.getDefaultMessage()).append(';'));
        return errorMsg.toString();
    }

}
