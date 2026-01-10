package com.lin.missyou.core;

import lombok.Data;

/**
 * 通用的返回格式, ResponseDto
 * @Data 相当于同时使用了@ToString、@EqualsAndHashCode、@Getter、@Setter
 * 和@RequiredArgsConstrutor这些注解，对于POJO类十分有用
 */
@Data
public class UnifyResponse {
    private int code;
    private String message;
    private String request;

    public UnifyResponse(int code, String message, String request) {
        this.code = code;
        this.message = message;
        this.request = request;
    }
}
