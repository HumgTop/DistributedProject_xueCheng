package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Autor HumgTop
 * @Date 2021/5/11 13:44
 * @Version 1.0
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {
    @Autowired
    FileSystemService fileSystemService;

    @Override
    @PostMapping("/upload") //使用@RequestParam注解进行参数映射
    public UploadFileResult upload(@RequestParam("multipartFile") MultipartFile multipartFile,
                                   @RequestParam("filetag") String fileTag,
                                   @RequestParam("businesskey") String businessKey,
                                   @RequestParam("metadata") String metadata) {

        return fileSystemService.upload(multipartFile, fileTag, businessKey, metadata);
    }
}
