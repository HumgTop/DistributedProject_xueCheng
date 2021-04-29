package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)    //创建Spring测试环境（初始化IOC容器，因此下文可以使用依赖注入）
public class CmsPageRepositoryTest {
    @Autowired
    CmsPageRepository cmsPageRepository;    //生成接口的代理对象，并注入；


    @Test
    public void testFindAll() {
        List<CmsPage> list = cmsPageRepository.findAll();
        int cnt = 1;
        for (CmsPage cmsPage : list) {
            System.out.println(cnt + " " + cmsPage);
            cnt++;
        }
    }

    /**
     * 进行分页查询
     */
    @Test
    public void testFindPage() {
        int page = 0;   //页码为0
        int size = 10;  //每页显示10条记录
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    @Test
    public void testUpdate() {
        //查询对象
        Optional<CmsPage> page = cmsPageRepository.findById("5abefd525b05aa293098fca6");
        //更新对象
        if (page.isPresent()) {
            CmsPage cmsPage = page.get();
            cmsPage.setPageAliase("使用Spring Data MongoDB修改");
            //提交修改
            cmsPageRepository.save(cmsPage);
            System.out.println(cmsPage);
        }
    }

    @Test
    public void testFindByPageName() {
        CmsPage byPageName = cmsPageRepository.findByPageName("4028e581617f945f01617f9dabc40000.html");
        System.out.println(byPageName);
    }

    @Test
    public void testFindAllByExample() {
        int page = 0;
        int size = 10;
        //分页条件
        Pageable pageable = PageRequest.of(page, size);
        CmsPage cmsPage = new CmsPage();
        //设置查询条件
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //设置模板id
//        cmsPage.setTemplateId("5abf57965b05aa2ebcfce6d1");
        //设置页面别名
        cmsPage.setPageAliase("Spring");
        //条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching();
        //设置pageAliase属性的匹配方式：包含关键字即可匹配
        ExampleMatcher withMatcher = matcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
//定义Example
        Example<CmsPage> example = Example.of(cmsPage, withMatcher);
        //根据查询对象查询数据库
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);

        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }


}
