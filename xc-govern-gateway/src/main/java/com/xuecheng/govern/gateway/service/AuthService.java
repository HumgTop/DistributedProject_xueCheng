package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Autor HumgTop
 * @Date 2021/7/1 14:54
 * @Version 1.0
 */
@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //取出cookie中的身份令牌
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> cookie = CookieUtil.readCookie(request, "uid");
        String access_token = cookie.get("uid");
        if (StringUtils.isEmpty(access_token)) {
            return null;
        }
        return access_token;
    }

    //从header中取出jwt令牌
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            //拒绝访问
            return null;
        }
        if (!authorization.startsWith("Bearer ")) {
            //拒绝访问
            return null;
        }
        String jwt=authorization.substring(7);
        return jwt;
    }

    //从redis中查询令牌有效期
    public long getExpire(String access_token) {
        String key = "user_token:" + access_token;
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
