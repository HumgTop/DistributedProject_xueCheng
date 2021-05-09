package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Autor HumgTop
 * @Date 2021/5/8 9:48
 * @Version 1.0
 */
@Api(value = "课程管理接口", description = "课程管理接口，提供页面的增删改查！")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachPlanList(String courseId);

    @ApiOperation("课程计划添加")
    public ResponseResult addTeachplan(Teachplan teachplan);
}
