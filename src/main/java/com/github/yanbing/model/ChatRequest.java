package com.github.yanbing.model;

import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class ChatRequest {
    /**
     * 消息
     */
    private String message;

    /**
     * 回话id
     */
    private String conversationId;
}
