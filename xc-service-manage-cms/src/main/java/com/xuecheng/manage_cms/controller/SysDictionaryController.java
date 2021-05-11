package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Autor HumgTop
 * @Date 2021/5/10 16:19
 * @Version 1.0
 */
@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDicthionaryControllerApi {
    @Autowired
    SysDictionaryService sysDictionaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String dType) {
        return sysDictionaryService.findDictionaryByType(dType);
    }
}
