package com.xuecheng.framework.domain.cms.request;


import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data   //设置getter和setter方法
@ToString
public class QueryPageRequest extends RequestData {
    //接收页面查询的查询条件
    private String siteId;
    private String pageId;
    private String pageName;
    private String pageAliase;
    private String templateId;
}
