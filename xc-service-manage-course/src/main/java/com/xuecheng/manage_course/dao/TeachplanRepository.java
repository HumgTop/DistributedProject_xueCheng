package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Autor HumgTop
 * @Date 2021/5/9 16:18
 * @Version 1.0
 */
@Repository
public interface TeachplanRepository extends JpaRepository<Teachplan, String> {
    //如果parentId!=0，则可能会查询到多条记录
    List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
