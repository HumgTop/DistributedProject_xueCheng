package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 定义静态方法，抛出指定错误代码的异常
 * @Autor HumgTop
 * @Date 2021/4/28 15:41
 * @Version 1.0
 */
public class ExceptionCast {
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
