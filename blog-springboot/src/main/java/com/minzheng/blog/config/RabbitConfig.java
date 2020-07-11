package com.minzheng.blog.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbitmq配置类
 *
 * @author 11921
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue insertDirectQueue() {
        return new Queue("article", true);
    }

    @Bean
    FanoutExchange maxWellExchange() {
        return new FanoutExchange("maxwell", false, false);
    }

    @Bean
    Binding bindingArticleDirect() {
        return BindingBuilder.bind(insertDirectQueue()).to(maxWellExchange());
    }

}
