package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeoutException;

/**
 * 入门程序
 *
 * @Autor HumgTop
 * @Date 2021/4/29 21:05
 * @Version 1.0
 */
public class Producer01 {
    public static void main(String[] args) {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("linux.humg.top");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq服务可以设置多个虚拟机
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        try {
            //建立新连接
            connection = connectionFactory.newConnection();
            //创建会话通道
            Channel channel = connection.createChannel();
            //声明队列
            channel.queueDeclare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
