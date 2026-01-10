package com.lin.missyou.exception;

import com.lin.missyou.exception.http.HttpException;

/**
 * 把更新成功也当作异常返回前端, 从而简化代码
 * 1. 更新成功 202/201, 不管了也用200
 * 2. 创建成功 201, 表示创建资源成功
 * 3. 删除成功 204/200
 * 4. 查询成功 200
 * 也可以分别创建这三种Success
 *
 *
 * 关于代码202的争议
 * 202原本表示已经接收到请求, 但请求尚未处理完成
 *
 *
 */
public class CreateSuccess extends HttpException {
    public CreateSuccess(int code){
        this.httpStatusCode = 201;
        this.code = code;
    }
}
