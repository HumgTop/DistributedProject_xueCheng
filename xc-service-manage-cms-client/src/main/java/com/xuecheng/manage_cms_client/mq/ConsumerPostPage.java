package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Autor HumgTop
 * @Date 2021/5/6 21:02
 * @Version 1.0
 * 监听消息队列，收到消息后调用Service层方法从GridFS下载html文件，并存储到服务器指定物理路径
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);
    @Autowired
    PageService pageService;

    /**
     * @param msg
     */
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})  //监听队列
    public void postPage(String msg) {
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive cms post page:{}", msg.toString());
        //取出页面ID
        String pageId = (String) map.get("pageId");
        //调用Service方法
        pageService.savePageToServerPath(pageId);
    }
}
