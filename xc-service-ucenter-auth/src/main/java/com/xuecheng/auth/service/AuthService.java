package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Autor HumgTop
 * @Date 2021/6/28 14:57
 * @Version 1.0
 */
@Service
public class AuthService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    //用户认证申请令牌，将令牌存储到redis和cookie中
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        //申请令牌失败
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
        //将token存储到redis
        String access_token = authToken.getAccess_token();
        String tokenJson = JSON.toJSONString(access_token);
        //保存是否成功
        boolean saveTokenRes = saveToken("user_token:" + access_token, tokenJson, tokenValiditySeconds);
        //保存失败则抛出异常
        if (!saveTokenRes){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }

        return authToken;
    }

    //获取httpBasic串（base64编码）
    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    private boolean saveToken(String key, String value, long ttl) {
        //存入key-value到redis
        stringRedisTemplate.boundValueOps(key).set(value, ttl, TimeUnit.SECONDS);
        //获取过期时间
        Long expire = stringRedisTemplate.getExpire(key);
        return expire > 0;
    }

    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //从eureka中获取认证服务的地址（因为spring security在认证服务中）
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址为http://ip:port
        URI uri = serviceInstance.getUri();
        //令牌的申请地址
        String authUrl = uri + "/auth/oauth/token";
        //定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        //指定restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400和401，也需要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        //获得http请求的响应
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        //令牌信息
        Map bodyMap = exchange.getBody();
        if (bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null
        ) {
            return null;
        }
        //创建AuthToken
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodyMap.get("jti"));
        authToken.setJwt_token((String) bodyMap.get("access_token"));
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));
        return authToken;
    }
}
