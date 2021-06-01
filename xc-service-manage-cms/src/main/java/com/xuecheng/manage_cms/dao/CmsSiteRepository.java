package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/22 15:02
 * @Version 1.0
 */
@Repository
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {

}
