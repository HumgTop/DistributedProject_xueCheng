package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/10 16:24
 * @Version 1.0
 */
@Repository
public interface SysDictionaryRepository extends MongoRepository<SysDictionary, String> {
    //根据字典分类查询
    SysDictionary findByDType(String dType);
}
