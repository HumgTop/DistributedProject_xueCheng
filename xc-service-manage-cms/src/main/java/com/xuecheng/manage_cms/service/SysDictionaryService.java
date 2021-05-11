package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Autor HumgTop
 * @Date 2021/5/10 16:22
 * @Version 1.0
 */
@Service
public class SysDictionaryService {
    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    public SysDictionary findDictionaryByType(String dType) {
        //根据字典分类type查询字典信息
        return sysDictionaryRepository.findByDType(dType);
    }
}
