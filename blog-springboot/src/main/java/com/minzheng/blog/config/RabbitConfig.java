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
    public Queue articleQueue() {
        return new Queue("article", true);
    }

    @Bean
    FanoutExchange maxWellExchange() {
        return new FanoutExchange("maxwell", true, false);
    }

    @Bean
    Binding bindingArticleDirect() {
        return BindingBuilder.bind(articleQueue()).to(maxWellExchange());
    }


}
