package com.example.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
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
    // 使用默认的DashScopeChatModel（若依赖多个大模型，就会报错）
    @Test
    void testChatClient(@Autowired ChatClient.Builder chatClient) {
        ChatClient build = chatClient.build();
        String hello = build.prompt().user("你好").call().content();
        System.out.println(hello);
    }

    // 使用指定的DashScopeChatModel
    @Test
    void testChatClient2(@Autowired DashScopeChatModel dashScopeChatModel) {
        ChatClient build = ChatClient.builder(dashScopeChatModel).build();
        String hello = build.prompt().user("你好").call().content();
        System.out.println(hello);
    }
}
