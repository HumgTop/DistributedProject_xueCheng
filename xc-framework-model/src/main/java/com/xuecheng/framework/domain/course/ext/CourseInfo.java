package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class CourseInfo extends CourseBase {

    private String id;
    //课程图片
    private String pic;
    //课程名称
    private String name;
    //课程描述
    private String description;
}
