package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Autor HumgTop
 * @Date 2021/5/22 14:35
 * @Version 1.0
 * CMS
 */
@Data
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    String pageUrl;

    public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
