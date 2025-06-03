package com.github.yanbing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "server.amap")
public class AmapConfigProperties {
    /**
     * 高德地址
     */
    private String url;

    /**
     * 获取用户高德的key
     */
    private String key;
}
