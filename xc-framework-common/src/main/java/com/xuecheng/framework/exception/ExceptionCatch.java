package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.Response;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常捕获类
 *
 * @Autor HumgTop
 * @Date 2021/4/28 15:46
 * @Version 1.0
 */
@ControllerAdvice   //控制器增强
public class ExceptionCatch {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);
    //定义map的builder对象
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();
    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;

    static {
        //定义异常类型对应的错误代码
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }


    //AOP？
    @ExceptionHandler(CustomException.class)    //动态代理，捕获此类型的异常
    @ResponseBody   //return的对象转为JSON
    public Response customException(CustomException customException) {
        //记录日志
        LOGGER.error("catch exception:{}", customException.getMessage());

        return new ResponseResult(customException.resultCode);
    }

    @ExceptionHandler(Exception.class)    //动态代理，捕获不可预知异常
    @ResponseBody   //return的对象转为JSON
    public Response exception(Exception exception) {
        //记录日志
        LOGGER.error("catch exception:{}", exception.getMessage());
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build();    //延迟加载
        }
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());   //获取该异常类型对应的错误代码

        if (resultCode!=null){
            return new ResponseResult(resultCode);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }
}
