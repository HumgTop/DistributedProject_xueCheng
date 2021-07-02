package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Autor HumgTop
 * @Date 2021/7/1 10:59
 * @Version 1.0
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;

    //定义过滤器类型
    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器优先级
    @Override
    public int filterOrder() {
        return 0;
    }

    //判断该过滤器是否执行
    @Override
    public boolean shouldFilter() {
        return true;
    }

    //过滤器执行方法逻辑
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //获取cookie中的身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if (access_token == null) {
            //用户未登录
            access_deny();
        }

        //判断身份令牌是否过期
        long expire = authService.getExpire(access_token);
        if (expire < 0) {
            //令牌已过期或者redis中不存在该令牌（不存在令牌返回-2）
            access_deny();
        }

        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (jwtFromHeader == null) {
            //header不存在Authrization字段
            access_deny();
        }
        return null;
    }

    private void access_deny() {
        //上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.setSendZuulResponse(false);  //拒绝访问
        //设置响应内容
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(responseResult);
        //设置响应体
        requestContext.setResponseBody(jsonString);
        //设置状态码
        requestContext.setResponseStatusCode(200);
        HttpServletResponse response = requestContext.getResponse();

        response.setContentType("application/json;charset=utf-8");
    }
}
