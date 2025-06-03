package com.github.yanbing.config;

import com.github.yanbing.meory.ExpiredMemoryRepository;
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

    @Bean
    public MessageChatMemoryAdvisor messageWindowChatMemory() {
        ExpiredMemoryRepository memoryRepository = new ExpiredMemoryRepository();
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository)
                .maxMessages(20)
                .build();

        return MessageChatMemoryAdvisor.builder(chatMemory)
                .scheduler(Schedulers.parallel())
                .build();
    }
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider,MessageChatMemoryAdvisor messageChatMemoryAdvisor) {

        return builder
                .defaultSystem("你是一个地理专家和天气预报助手，可以帮助户查询天气预报信息。" +
                        "当用户咨询天气预报相关问题时,你首先根据用户的地址信息对应到标准行政区划的区县级名称,再根据区县名称获取区域编码,拿到区域编码后根据区域编码查询天气预报" +
                        "回复时，请整理天气预报和咨询信息,并提供出行意见。")
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }

}