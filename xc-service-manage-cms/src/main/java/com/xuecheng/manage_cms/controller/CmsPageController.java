package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 17:24
 * 接口的实现类
 **/
@RestController //返回json数据
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
    PageService pageService;

    /**
     * @param page             当前页码
     * @param size             每页显示记录数
     * @param queryPageRequest 查询请求参数对象
     * @return
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        //如果get请求中携带参数，会根据key自动将value注入queryPageRequest对象中的同名属性中
        return pageService.findList(page, size, queryPageRequest);
    }

    /**
     * post请求添加页面
     *
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {    //使用@RequestBody注解将JSON数据封装到Bean中
        //添加页面
        return pageService.add(cmsPage);
    }

    /**
     * 根据get请求中的id值，查询返回cmsPage
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.getById(id);
    }

    /**
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")   //put请求表示更新服务器资源
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id, cmsPage);
    }
}
