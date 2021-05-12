package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Autor HumgTop
 * @Date 2021/5/12 15:29
 * @Version 1.0
 */
@FeignClient("XC-SERVICE-MANAGE-CMS")   //Spring容器管理此接口
public interface CmsPageClient {

    //Feign从Eureka中获得服务地址后，创建此接口的动态代理并发起请求，将Response封装成对象并返回
    @GetMapping("/cms/page/get/{id}")
    CmsPage findCmsPageById(@PathVariable("id") String id);
}
