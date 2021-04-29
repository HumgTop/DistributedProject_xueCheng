package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Autor HumgTop
 * @Date 2021/4/29 16:42
 * @Version 1.0
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")  //从配置文件中读取注入
    String db;

    @Bean  //将GridFSBucket注册到Bean容器中
    public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(db);
        return GridFSBuckets.create(database);
    }
}
