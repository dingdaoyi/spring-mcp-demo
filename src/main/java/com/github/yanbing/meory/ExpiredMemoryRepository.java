package com.github.yanbing.meory;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.Assert;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author dingyunwei
 */
@Slf4j
public class ExpiredMemoryRepository implements ChatMemoryRepository {

    private final Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();

    private final Map<String, LocalDateTime> conversationHistoryTask = new ConcurrentHashMap<>();

    private final Scheduler schedule = Schedulers.single();

    private  final int MAX_TIME_OUT = 60;

    public ExpiredMemoryRepository() {
        this.initTask();
    }

    private void initTask() {
        schedule.schedule(()-> conversationHistoryTask.forEach((key, value) -> {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(value)) {
                chatMemoryStore.remove(key);
                conversationHistoryTask.remove(key);
            }
        }),2, TimeUnit.MINUTES);
    }


    @Override
    public List<String> findConversationIds() {
        return new ArrayList<>(this.chatMemoryStore.keySet());
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        List<Message> messages = this.chatMemoryStore.get(conversationId);
        conversationHistoryTask.put(conversationId, LocalDateTime.now().plusMinutes(MAX_TIME_OUT));
        return messages != null ? new ArrayList<>(messages) : List.of();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");
        this.chatMemoryStore.put(conversationId, messages);
        conversationHistoryTask.put(conversationId, LocalDateTime.now().plusMinutes(MAX_TIME_OUT));
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        this.chatMemoryStore.remove(conversationId);
    }
}
