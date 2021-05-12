package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Autor HumgTop
 * @Date 2021/5/8 14:03
 * @Version 1.0
 */
@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CoursePicRepository coursePicRepository;

    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId) {
        if (courseId == null || courseId.equals("")) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        return teachplanMapper.selectList(courseId);
    }

    //添加课程计划
    @Transactional  //事务
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if ((teachplan == null
                || StringUtils.isEmpty(teachplan.getCourseid())
                || StringUtils.isEmpty(teachplan.getPname()))
        ) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = teachplan.getCourseid();
        String parentId = teachplan.getParentid();
        teachplan.setGrade("3");
        if (StringUtils.isEmpty(parentId)) {
            /**
             * 为空说明
             * 1. 表示该章节为该课程的一级节点（使用当前页面的课程id找到该课程并添加为一级节点）
             * 2. 如果该章节所属课程不存在数据库中，则在数据库teachplan中创建此课程的根节点，并添加此章节
             */
            parentId = getTeachplanRoot(courseId);
            teachplan.setParentid(parentId);
            teachplan.setGrade("2");
        }
        //添加课程计划
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程id查询课程（不存在，则添加课程），并返回课程根节点id
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }

        CourseBase courseBase = optional.get(); //获取课程信息

        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() == 0) {
            //该课程不存在教学计划根节点，则创建教学计划根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setParentid("0");
            teachplan.setPname(courseBase.getName());
            teachplan.setGrade("1");
            teachplan.setStatus("0");   //未发布

            return teachplan.getId();   //主键id会回写到teachplan对象中
        }
        return teachplanList.get(0).getId();
    }

    //课程列表分页查询
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        if (page < 0) {
            page = 0;
        }
        if (size < 1) {
            size = 20;
        }
        //设置分页参数
        PageHelper.startPage(page, size);
        //分页查询
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //结果集
        List<CourseInfo> result = courseListPage.getResult();
        //总记录数
        long total = courseListPage.getTotal();
        //封装返回结果
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
        courseInfoQueryResult.setList(result);
        courseInfoQueryResult.setTotal(total);
        return new QueryResponseResult<>(CommonCode.SUCCESS, courseInfoQueryResult);
    }

    //保存courseId和pic地址的映射关系到数据库中
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        //先从数据库查询是否已有记录
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (optional.isPresent()) {
            coursePic = optional.get();
        } else {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}