package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @Autor HumgTop
 * @Date 2021/7/7 21:46
 * @Version 1.0
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    @Scheduled(fixedDelay = 60000)      //每隔一分钟执行一次任务
    //定时发送添加选课任务
    public void sendChooseCourseTask() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskListBeforTimeNow(time, 100);
        for (XcTask xcTask : xcTaskList) {
            //如果版本号已经被修改，说明其他线程已经执行了发送消息的任务，本线程不再发送消息
            if (taskService.versionIsModify(xcTask.getId(), xcTask.getVersion()) > 0)
                //指定路由，向交换机发送消息
                taskService.publish(xcTask, xcTask.getMqExchange(), xcTask.getMqRoutingkey());
        }
    }
}
