package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.GetMediaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Autor HumgTop
 * @Date 2021/7/10 21:05
 * @Version 1.0
 */
public interface CourseLearningControllerApi {
    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    GetMediaResult getmedia(@PathVariable("courseId") String courseId,
                            @PathVariable("teachplanId") String teachplanId);
}
