package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Autor HumgTop
 * @Date 2021/6/27 9:52
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        String key = "user_token:9734b68f-cf5e-456f-9bd6-df578c711390";
        //定义map
        HashMap<String, String> mapValue = new HashMap<>();
        mapValue.put("id", "101");
        mapValue.put("username", "humgtop");
        //转为json
        String value = JSON.toJSONString(mapValue);
        //向reids中存储字符串
        stringRedisTemplate.boundValueOps(key).set(value, 60, TimeUnit.SECONDS);
        //读取过期时间，已过期返回-2
        Long expireTime = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expireTime);
        //根据key获取value
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }
}
