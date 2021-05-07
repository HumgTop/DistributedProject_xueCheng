package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Autor HumgTop
 * @Date 2021/5/6 15:00
 * @Version 1.0
 * <p>
 * 由于cms作为页面发布方要面对很多不同站点的服务器，面对很多页面发布队列，所以这里不再配置队列，只需要配置交换机即可。
 * 在cms工程只配置交换机名称即可。
 */
@Configuration
public class RabbitmqConfig {
    //交换机bean的名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";


    /**
     * 交换机配置使用direct类型
     *
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)  //注册bean到IOC容器中
    public Exchange EXCHANGE_TOPICS_INFORM() {
        //routing模式交换机：direct
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
}

