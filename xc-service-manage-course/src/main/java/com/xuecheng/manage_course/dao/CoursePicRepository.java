package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/11 17:03
 * @Version 1.0
 */
@Repository
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {
}
