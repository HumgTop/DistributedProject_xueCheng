package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 10:28
 * @Version 1.0
 */
@RestController //向前端返回json数据
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    PageService pageService;

    @Autowired
    public CmsConfigController(PageService pageService) {
        this.pageService = pageService;
    }


    @Override
    @GetMapping("/getmodel/{id}")
    public CmsConfig getModel(@PathVariable("id") String id) {
        return pageService.getModelById(id);
    }


}
