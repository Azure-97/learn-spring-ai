package com.example.quickstart;

import com.alibaba.cloud.ai.memory.redis.JedisRedisChatMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description: Redis 记忆多用户隔离测试
 * @author: azurewang
 * @date: 2026/8/26 周三 22:10
 * @Version 1.0
 **/
@SpringBootTest
public class TestRedisMemory {

    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired ChatClient.Builder builder,
                     @Autowired ChatMemory chatMemory) {
        chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("你是一个AI助手。")
                .build();
    }

    // 配置 ChatMemory
    @TestConfiguration
    public static class TestConfig {

        @Value("${spring.ai.memory.redis.host}")
        private String redisHost;

        @Value("${spring.ai.memory.redis.port}")
        private int redisPort;

        // 如果配置了密码，取消下面的注释
        // @Value("${spring.ai.memory.redis.password}")
        // private String redisPassword;

        @Bean
        public JedisRedisChatMemoryRepository redisChatMemoryRepository() {
            return JedisRedisChatMemoryRepository.builder()
                    .host(redisHost)
                    .port(redisPort)
                    // 有密码时取消注释
                    // .password(redisPassword)
                    .build();
        }

        @Bean
        public ChatMemory chatMemory(JedisRedisChatMemoryRepository repository) {
            // ✅ 修正：用 MessageWindowChatMemory.builder() 而不是 JedisRedisChatMemoryRepository.builder()
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(repository)
                    .maxMessages(10)  // 滑动窗口，最多保留10条消息
                    .build();
        }
    }

    // 多用户记忆隔离
    @Test
    void testChatClientMultiUser() {

        // ===== 用户A：晓云，固定 conversationId = "user-A1" =====

        String content = chatClient.prompt()
                .user("你好,我是aacc的晓云")
                .advisors(advisorSpec -> advisorSpec.params(
                        Map.of(ChatMemory.CONVERSATION_ID, "user-A1")))
                .call()
                .content();
        System.out.println("===== 晓云第1次 ++++++" + content);

        // 同一个 conversationId，记忆关联
        String content11 = chatClient.prompt()
                .user("你好,我是谁")
                .advisors(advisorSpec -> advisorSpec.params(
                        Map.of(ChatMemory.CONVERSATION_ID, "user-A1")))
                .call()
                .content();
        System.out.println("===== 晓云第2次 ++++++" + content11);

        // ✅ 建议加上断言验证记忆是否生效
        assertNotNull(content11);
        assertTrue(content11.contains("晓云"),
                "AI 应该记住用户叫晓云，但实际回复：" + content11);
    }
}