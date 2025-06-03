package com.github.yanbing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingyunwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {


    public ChatResponse(String content) {
        this.content = content;
        this.success = true;
    }

    private String content;

    private boolean success;
}
