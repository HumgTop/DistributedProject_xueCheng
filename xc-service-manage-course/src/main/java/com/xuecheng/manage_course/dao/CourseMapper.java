package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator.
 */
@Mapper  //Mybatis注解，可生成动态代理对象注入Service层中进行数据库操作
@Repository
public interface CourseMapper {
    //根据id查询课程基本信息
    CourseBase findCourseBaseById(String id);

    //通过courseListRequest中的companyId查询记录，查询结果封装为CourseInfo
    Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
