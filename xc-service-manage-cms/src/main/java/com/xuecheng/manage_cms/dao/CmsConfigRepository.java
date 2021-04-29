package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 10:21
 * @Version 1.0
 */
@Repository
public interface CmsConfigRepository extends MongoRepository<CmsConfig, String> {
    //基本的CRUD方法从父类继承

}
