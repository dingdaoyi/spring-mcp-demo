package com.github.yanbing.config;

import com.github.yanbing.model.an.McpService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author dingyunwei
 */
@Configuration
public class McpServerConfig {
 
    @Resource
    private ApplicationContext applicationContext;
 
    @Bean
    public ToolCallbackProvider autoRegisterTools() {
        Map<String, Object> annotation = applicationContext.getBeansWithAnnotation(McpService.class);
        return MethodToolCallbackProvider.builder()
                .toolObjects(annotation.values().toArray())
                .build();
    }
}