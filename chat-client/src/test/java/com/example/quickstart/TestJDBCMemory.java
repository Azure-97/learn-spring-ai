package com.example.quickstart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/26 周三 22:10
 * @Version 1.0
 **/
@SpringBootTest
public class    TestJDBCMemory {
    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired ChatClient.Builder builder, @Autowired ChatMemory chatMemory){
         chatClient = builder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("你是一个AI助手。")
                .build();
    }
    //  配置ChatMemory
    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(100)
                    .chatMemoryRepository(chatMemoryRepository).build();
        }
    }



    //多用户记忆隔离
    @Test
    void testChatClientMultiUser( ) {


        // ===== 用户A：晓云，固定 conversationId = "user-A" =====


        String content = chatClient.prompt()
                .user("你好,我是aacc的晓云")
                .advisors(advisorSpec -> advisorSpec.params(Map.of(ChatMemory.CONVERSATION_ID, "user-A1")))
                .call()
                .content();
        System.out.println("===== 晓云第1次 ++++++" + content);

        // 同一个 conversationId，记忆关联
        String content11 = chatClient.prompt()
                .user("你好,我是谁")
                .advisors(advisorSpec -> advisorSpec.params(Map.of(ChatMemory.CONVERSATION_ID, "user-A1")))
                .call()
                .content();
        System.out.println("===== 晓云第2次 ++++++" + content11);
        // 预期：模型能回答出"你是晓云"


    }


}
