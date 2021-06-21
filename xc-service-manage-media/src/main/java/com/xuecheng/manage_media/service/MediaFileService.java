package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Autor HumgTop
 * @Date 2021/6/17 17:07
 * @Version 1.0
 */
@Service
public class MediaFileService {
    private static Logger logger = LoggerFactory.getLogger(MediaFileService.class);

    @Autowired
    MediaFileRepository mediaFileRepository;

    //媒资查询
    public QueryResponseResult<MediaFile> findList(int page,
                                                   int size,
                                                   QueryMediaFileRequest queryMediaFileRequest) {
        MediaFile mediaFile = new MediaFile();
        //查询条件
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //定义查询模板
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }

        //查询条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                //精确匹配处理状态（如果不设置匹配器，默认为精确匹配
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());
        //定义example条件对象
        Example<MediaFile> example = Example.of(mediaFile, matcher);
        //分页查询对象
        page = page < 1 ? 0 : page - 1;
        if (size < 0) {
            size = 10;
        }
        Pageable pageable = new PageRequest(page, size);
        //按分页条件和查询条件返回结果集
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        long total = all.getTotalElements();    //记录数
        List<MediaFile> content = all.getContent(); //查询结果集合

        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setList(content);
        queryResult.setTotal(total);
        return new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
    }
}
