package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Autor HumgTop
 * @Date 2021/5/21 16:14
 * @Version 1.0
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    String previewUrl;  //预览URL（请求cms服务的URL）

    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);  //初始化从父类中继承的字段
        this.previewUrl = previewUrl;
    }
}
