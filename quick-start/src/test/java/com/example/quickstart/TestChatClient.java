package com.example.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/26 周三 22:10
 * @Version 1.0
 **/
@SpringBootTest
public class TestChatClient {
    @Test
    void testChatClient(@Autowired ChatClient.Builder chatClient) {
        ChatClient build = chatClient.build();
        String hello = build.prompt().user("你好").call().content();
        System.out.println(hello);


    }
}
