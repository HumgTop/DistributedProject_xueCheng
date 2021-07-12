package com.xuecheng.order.service;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Autor HumgTop
 * @Date 2021/7/9 22:22
 * @Version 1.0
 */
@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    //查询updateTime之前的n条任务
    public List<XcTask> findXcTaskListBeforTimeNow(Date updateTime, int size) {
        PageRequest pageable = new PageRequest(0, size);
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return all.getResult();
    }

    //发布（支付成功）添加选课消息到mq
    public void publish(XcTask xcTask, String ex, String routingKey) {
        Optional<XcTask> xcTaskOptional = xcTaskRepository.findById(xcTask.getId());
        if (xcTaskOptional.isPresent()) {
            rabbitTemplate.convertAndSend(ex, routingKey, xcTask);
            //更新该任务的操作时间(updateTime)
            XcTask xcTaskFromDb = xcTaskOptional.get();
            xcTaskFromDb.setUpdateTime(new Date());
            xcTaskRepository.save(xcTaskFromDb);
        }
    }

    @Transactional  //数据库的增删改查需要事务控制
    //更新版本号
    public int versionIsModify(String id, int version) {
        return xcTaskRepository.updateTaskVersion(id, version);
    }
}
