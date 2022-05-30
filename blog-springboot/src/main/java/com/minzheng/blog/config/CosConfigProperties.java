package com.minzheng.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lunatic
 * @since 2022/5/26 10:41
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.cos")
public class CosConfigProperties {

    private String url;

    private String secretId;

    private String secretKey;

    private String region;

    private String bucketName;


}
