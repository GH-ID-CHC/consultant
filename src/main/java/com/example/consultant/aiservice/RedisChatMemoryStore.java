package com.example.consultant.aiservice;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Redis聊天内存存储
 *
 * @author 柴和程
 * @date 2026/04/16
 */
@Component
public class RedisChatMemoryStore implements ChatMemoryStore {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = redisTemplate.opsForValue().get(memoryId.toString());
        List<ChatMessage> chatMessage = ChatMessageDeserializer.messagesFromJson(json);
        return chatMessage;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        // 1. 将会话数据转换为json数据
        String json = ChatMessageSerializer.messagesToJson(list);
        // 2. 存储会话数据
        redisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(memoryId.toString());
    }
}
