package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Autor HumgTop
 * @Date 2021/5/11 13:49
 * @Version 1.0
 */
@Repository //将fastdfs的文件id存入MongoDB中
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {

}
