package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;

    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;

    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;

    @Value("${course-publish.siteId}")
    private String publish_siteId;

    @Value("${course-publish.templateId}")
    private String publish_templateId;

    @Value("${course-publish.previewUrl}")
    private String previewUrl;

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

    //查询课程视图
    public CourseView getCourseView(String courseId) {
        CourseView courseView = new CourseView();
        courseBaseRepository.findById(courseId).ifPresent(courseView::setCourseBase);
        coursePicRepository.findById(courseId).ifPresent(courseView::setCoursePic);
        courseMarketRepository.findById(courseId).ifPresent(courseView::setCourseMarket);
        courseView.setTeachplanNode(teachplanMapper.selectList(courseId));
        return courseView;
    }

    //课程预览
    public CoursePublishResult preview(String courseId) {
        //创建CmsPage对象，添加cmsPage到cms_page中
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        cmsPage.setPageName(courseId + ".html");
        //页面别名就是课程名称
        cmsPage.setPageAliase(findCourseBaseById(courseId).getName());

        //远程调用请求cms添加页面
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        //拼装previewUrl
        String url = previewUrl + cmsPageResult.getCmsPage().getPageId();
        return new CoursePublishResult(CommonCode.SUCCESS, url);
    }

    //根据Id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId) {
        CourseBase courseBase = null;
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            courseBase = optional.get();
            return courseBase;
        }

        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return courseBase;
    }

    //课程发布
    @Transactional  //更改课程发布状态，涉及到事务提交使用注解
    public CoursePublishResult publish(String courseId) {
        //创建CmsPage对象，添加cmsPage到cms_page中
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        cmsPage.setPageName(courseId + ".html");
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //页面别名就是课程名称
        cmsPage.setPageAliase(findCourseBaseById(courseId).getName());

        //远程调用：一键发布页面
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //更新课程发布状态
        saveCoursePubState(courseId);
        //保存课程索引信息
        //创建coursePub对象
        CoursePub coursePub = createCoursePub(courseId);
        //将coursePub对象保存到数据库
        saveCoursePub(courseId, coursePub);
        //缓存课程信息

        return new CoursePublishResult(CommonCode.SUCCESS, cmsPostPageResult.getPageUrl());
    }

    //创建CoursePub对象
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);
        //查询courseBase
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //查询coursePic
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()) {
            CoursePic coursePic = coursePicOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //查询courseMarket
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        //转为json
        String teachPlanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachPlanString);

        return coursePub;
    }

    //将coursePub保存到数据库
    private CoursePub saveCoursePub(String courseId, CoursePub coursePub) {
        CoursePub coursePubNew;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(courseId);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else coursePubNew = new CoursePub();

        BeanUtils.copyProperties(coursePub, coursePubNew);   //source对象会覆盖target对象的所有字段
        coursePubNew.setId(courseId);
        //时间戳，给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    //更改课程发布状态
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = courseBaseRepository.getOne(courseId);
        courseBase.setStatus("202002"); //已发布状态
        courseBaseRepository.save(courseBase);
        return courseBase;
    }
}
