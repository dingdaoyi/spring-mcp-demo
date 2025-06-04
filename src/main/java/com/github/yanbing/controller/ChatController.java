package com.github.yanbing.controller;

import com.github.yanbing.model.ChatRequest;
import com.github.yanbing.model.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * @author dingyanbing
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {


    @Resource
    private ChatClient chatClient;

    // SSE流式响应（适合前端实时展示）
    @PostMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatStream(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.getMessage())
                .advisors(advisor -> {
                    if (request.getConversationId() != null) {
                        advisor.param("chat_memory_conversation_id",
                                request.getConversationId());
                    }
                })
                .stream()
                .content()
                .map(ChatResponse::new)
                .onErrorResume(e -> Flux.just(
                        new ChatResponse("服务处理异常: " + e.getMessage(), false)));
    }

    // 阻塞式响应（适合API调用）
    @PostMapping("blocking")
    public Mono<ChatResponse> chatBlocking(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.getMessage())
                .advisors(advisor -> {
                    if (request.getConversationId() != null) {
                        advisor.param("chat_memory_conversation_id",
                                request.getConversationId());
                    }
                })
                .stream()
                .content()
                .collect(Collectors.joining())
                .map(ChatResponse::new)
                .onErrorResume(e -> Mono.just(
                        new ChatResponse("服务处理异常: " + e.getMessage(), false)));
    }
}