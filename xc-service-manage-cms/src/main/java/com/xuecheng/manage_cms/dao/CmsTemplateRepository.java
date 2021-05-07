package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 10:21
 * @Version 1.0
 */
@Repository
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate, String> {
    //基本的CRUD方法从父类继承

}
