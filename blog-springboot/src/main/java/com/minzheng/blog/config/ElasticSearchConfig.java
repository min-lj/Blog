package com.minzheng.blog.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * elasticsearch配置类
 *
 * @author: yezhiqiu
 * @date: 2020-12-26
 **/
@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("139.196.6.14:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

}
