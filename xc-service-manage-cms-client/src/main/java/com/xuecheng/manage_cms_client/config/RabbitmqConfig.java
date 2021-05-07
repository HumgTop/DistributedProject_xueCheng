package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Autor HumgTop
 * @Date 2021/5/6 15:00
 * @Version 1.0
 */
@Configuration
public class RabbitmqConfig {
    //队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机bean的名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    //队列的名称（自动注入）
    @Value("${xuecheng.mq.queue}")
    String queue_cms_postpage_name; //routingKey 即站点Id

    @Value("${xuecheng.mq.routinKey}")
    String routingKey;

    /*** 交换机配置使用direct类型 * @return the exchange */
    @Bean(EX_ROUTING_CMS_POSTPAGE)  //注册bean
    public Exchange EXCHANGE_TOPICS_INFORM() {
        //routing模式交换机：direct
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE() {
        return new Queue(queue_cms_postpage_name);
    }

    /**
     * 绑定队列到交换机
     *
     * @param queue    自动注入
     * @param exchange 自动注入
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue, @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
}

