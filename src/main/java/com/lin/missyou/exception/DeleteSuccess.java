package com.lin.missyou.exception;

import com.lin.missyou.exception.http.HttpException;

/**
 * 但神奇的是 状态码为204时, 返回结果为空
 * 因为默认HTTPSTATUS为204删除资源成功时, 不应该向前端返回东西, 因此 建议这个也用200
 *
 * 原因是RESTFUL默认都是返回该资源本身, 204 DELETE删掉了, 因此204什么也不返回
 */
public class DeleteSuccess extends HttpException {
    public DeleteSuccess(int code){
        this.httpStatusCode = 200;
        this.code = code;
    }
}
