package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增删改查！")
public interface CmsPageControllerApi {
    /**
     * @param page             当前页码
     * @param size             每页显示记录数
     * @param queryPageRequest 查询请求参数对象
     * @return 查询结果的列表用响应对象封装
     */
    @ApiOperation("分页查询页面列表")   //功能注释
    @ApiImplicitParams({    //参数注释
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * 添加页面方法
     *
     * @param cmsPage
     * @return
     */
    CmsPageResult add(CmsPage cmsPage);

    @ApiOperation("通过ID查询页面")
    public CmsPage findById(String id);

    @ApiOperation("修改页面")
    public CmsPageResult edit(String id, CmsPage cmsPage);

    @ApiOperation("发布页面")
    public ResponseResult post(String pageId);

    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);

    @ApiOperation("一键发布页面（供课程管理服务远程调用）")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
