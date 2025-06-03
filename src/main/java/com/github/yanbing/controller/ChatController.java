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


    @PostMapping(value = "ms",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatQuery(@RequestBody ChatRequest request) {
        try {
            return chatClient.prompt()
                    .user(request.getMessage())
                    .advisors(sps -> {
                        if (request.getConversationId() != null) {
                            sps.param("chat_memory_conversation_id", request.getConversationId());
                        }
                    })
                    .stream()
                    .content()
                    .map(ChatResponse::new)
                    .onErrorResume(e->Flux.just(new ChatResponse("请求超时",false)));

        } catch (Exception e) {
            return Flux.error(e);
        }
    }


    @PostMapping(value = "block")
    public Mono<ChatResponse> chatBlock(@RequestBody ChatRequest request) {
        try {
            String userMessage = request.getMessage();
            return chatClient.prompt()
                    .user(u -> {
                        ChatClient.PromptUserSpec text = u.text(userMessage);
                    })
                    .advisors(sps -> {
                        if (request.getConversationId() != null) {
                            sps.param("chat_memory_conversation_id", request.getConversationId());
                        }
                    })
                    .stream()
                    .content()
                    .collect(Collectors.joining())
                    .map(ChatResponse::new);

        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}