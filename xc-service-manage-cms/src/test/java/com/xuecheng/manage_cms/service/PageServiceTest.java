package com.xuecheng.manage_cms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 17:58
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)    //创建Spring测试环境（初始化IOC容器，因此下文可以使用依赖注入）
public class PageServiceTest {
    @Autowired
    PageService pageService;

    @Test
    public void testGenerateHtml() {
        pageService.getTemplateId("5abefd525b05aa293098fca6");
    }
}
