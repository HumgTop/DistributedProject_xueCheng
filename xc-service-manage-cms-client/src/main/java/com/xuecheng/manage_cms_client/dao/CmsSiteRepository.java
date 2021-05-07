package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/6 15:32
 * @Version 1.0
 */
@Repository
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
    //继承了crud方法
}
