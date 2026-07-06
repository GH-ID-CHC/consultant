package com.example.consultant.controller;

import com.example.consultant.service.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    
    @Autowired
    private OpenAiChatModel model;

    @Autowired
    private ConsultantService consultantService;

    /**
     * 直接调用消息
     *
     * @param message 消息
     * @return {@link String }
     */
    @PostMapping("/chat")
    public String chat(String message){
        return model.chat(message);
    }

    /**
     * 使用AiServices发送消息
     *
     * @param message 消息
     * @return {@link String }
     */
    @GetMapping("/chat2")
    public Flux<String> chat2(String memoryId, String message) {
        return consultantService.chat(memoryId, message); 
    }
}
