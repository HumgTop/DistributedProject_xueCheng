package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 *
 * 从Spring data jpa中继承基本的crud方法
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
