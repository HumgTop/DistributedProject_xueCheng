package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.management.resource.ResourceRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Autor HumgTop
 * @Date 2021/6/11 8:38
 * @Version 1.0
 */
@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

    @ApiOperation("文件上传注册")
    ResponseResult register(String fileMd5,
                            String fileName,
                            Long fileSize,
                            String mimetype,
                            String fileExt);

    /**
     * @param chunk     块的下标
     * @param chunkSize 块的大小
     */
    @ApiOperation("分块检查：校验分块是否存在")
    CheckChunkResult checkChunk(String fileMd5,
                                int chunk,
                                int chunkSize);

    @ApiOperation("上传分块")
    ResponseResult uploadChunk(MultipartFile file,
                               String fileMd5,
                               int chunk);

    @ApiOperation("合并分块")
    ResponseResult mergeChunks(String fileMd5,
                                String fileName,
                                Long fileSize,
                                String mimetype,
                                String fileExt);
}
