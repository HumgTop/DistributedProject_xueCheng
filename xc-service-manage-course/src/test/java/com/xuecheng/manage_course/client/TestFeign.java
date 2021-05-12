package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Autor HumgTop
 * @Date 2021/5/12 15:32
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {
    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void testFeign(){
        CmsPage cmsPage = cmsPageClient.findCmsPageById("5a754adf6abb500ad05688d9");
        System.out.println(cmsPage);
    }
}
