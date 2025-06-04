package com.github.yanbing.config;

import com.github.yanbing.meory.ExpireMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

/**
 * @author dingyunwei
 */
@Configuration
public class ChatClientConfig {

    // 配置基于时间窗口的对话记忆
    @Bean
    public MessageChatMemoryAdvisor messageWindowChatMemory() {
        ExpireMemoryRepository memoryRepository = new ExpireMemoryRepository();
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository)
                .maxMessages(20) // 保留最近20条对话
                .build();

        return MessageChatMemoryAdvisor.builder(chatMemory)
                .scheduler(Schedulers.parallel())
                .build();
    }

    // 配置ChatClient
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider toolCallbackProvider,
                                 MessageChatMemoryAdvisor messageChatMemoryAdvisor) {

        return builder
                .defaultSystem("""
                    你是一个地理专家和天气预报助手，可以帮助用户查询天气预报信息。
                    工作流程：
                    1. 根据用户地址信息匹配到标准行政区划的区县级名称
                    2. 根据区县名称获取区域编码
                    3. 使用区域编码查询天气预报
                    4. 整理天气信息并提供出行建议
                    """)
                // 工具调用
                .defaultToolCallbacks(toolCallbackProvider)
                // 对话记忆
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }

}