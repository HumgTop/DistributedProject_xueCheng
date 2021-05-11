package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Autor HumgTop
 * @Date 2021/5/11 13:39
 * @Version 1.0
 */
public interface FileSystemControllerApi {
    /**
     * 文件系统服务工程接口：上传文件到fastdfs
     * @param multipartFile 文件
     * @param fileTag 文件标签
     * @param businessKey 业务key
     * @param metadata 元信息
     * @return
     */
    UploadFileResult upload(MultipartFile multipartFile, String fileTag, String businessKey, String metadata);
}
