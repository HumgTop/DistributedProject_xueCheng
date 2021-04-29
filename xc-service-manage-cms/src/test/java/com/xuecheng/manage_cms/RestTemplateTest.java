package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 12:09
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)    //创建Spring测试环境（初始化IOC容器，因此下文可以使用依赖注入）
public class RestTemplateTest {
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testOkhttp() {
        //测试http请求
        ResponseEntity<Map> map = restTemplate.getForEntity("http://localhost:8000/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(map);
    }
}
