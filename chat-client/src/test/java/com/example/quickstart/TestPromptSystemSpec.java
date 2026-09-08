package com.example.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.function.Consumer;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/26 周三 22:10
 * @Version 1.0
 **/

@SpringBootTest
public class TestPromptSystemSpec {
 // 可以使用文件加载系统提示词，也可以直接使用String
    @Test
    void testChatClient3(@Autowired DashScopeChatModel dashScopeChatModel, @Value ("classpath:/file/prompt.st")  Resource resource) {
        // ChatClient使用系统提示词
        // 预设角色 你是谁 你能干什么 你要注意什么
        ChatClient build = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(resource)
                .build();

        //当前对话使用提示词
        // 动态传入参数

        Consumer<ChatClient.PromptSystemSpec> systemSpecConsumer = systemSpec -> {
            systemSpec.text("你是一位资深后端技术专家，拥有超过10年的大型分布式系统设计与开发经验。我是${role}")
                    .param("role", "宇宙最帅管理员");
        };
        String hello = build.prompt()
                .system(systemSpecConsumer)
                .user("你好，你是谁,我是什么角色").call().content();
        System.out.println(hello);
    }



}
