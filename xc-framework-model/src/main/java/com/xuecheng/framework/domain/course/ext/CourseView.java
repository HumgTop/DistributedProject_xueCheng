package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Autor HumgTop
 * @Date 2021/5/13 9:03
 * @Version 1.0
 */
@Data
@ToString
public class CourseView implements Serializable {
    private CourseBase courseBase;
    private CoursePic coursePic;
    private CourseMarket courseMarket;
    private TeachplanNode teachplanNode;

    //todo 无参构造方法
}
