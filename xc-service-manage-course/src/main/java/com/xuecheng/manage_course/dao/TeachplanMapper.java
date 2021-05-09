package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/8 10:36
 * @Version 1.0
 */
@Mapper
@Repository
public interface TeachplanMapper {
    //通过courseId查询Teachplan表，并返回树形结构的数据
    TeachplanNode selectList(@Param("courseId") String courseId);
}
