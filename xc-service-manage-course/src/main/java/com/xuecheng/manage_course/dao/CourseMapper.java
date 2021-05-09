package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator.
 */
@Mapper  //Mybatis注解，可生成动态代理对象注入Service层中进行数据库操作
@Repository
public interface CourseMapper {
    CourseBase findCourseBaseById(String id);
}
