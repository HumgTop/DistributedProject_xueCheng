package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * dao对象：操作mongodb
 */
@Repository
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
    //基本的CRUD方法从MongoRepository中继承

    //自定义方法
    CmsPage findByPageName(String pageName);

    //根据唯一索引查询数据库
    CmsPage findByPageNameAndPageWebPathAndSiteId(String pageName, String pageWebPath, String siteId);


}



