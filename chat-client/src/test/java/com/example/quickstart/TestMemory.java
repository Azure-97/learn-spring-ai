package com.example.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.beans.BeanProperty;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/26 周三 22:10
 * @Version 1.0
 **/
@SpringBootTest
public class TestMemory {
    // 手工ChatMemory
    @Test
    void test(@Autowired DashScopeChatModel dashScopeChatModel) {


        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        String conversationId = "uuid123456";

        // 第一轮
        UserMessage text = new UserMessage("你好,我是天下第一帅");
        chatMemory.add(conversationId, text);
        ChatResponse response = dashScopeChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response.getResult().getOutput());
        // 第二轮
        UserMessage text2 = new UserMessage("我是谁");
        chatMemory.add(conversationId, text2);
        ChatResponse response2 = dashScopeChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response2.getResult().getOutput());

        System.out.println(response2.getResult().getOutput().getText());
    }

    // PromptChatMemoryAdvisor 自动ChatMemory
    @Test
    void testChatClient(@Autowired ChatClient.Builder builder, @Autowired ChatMemory chatMemory) {
        ChatClient chatClient1 = builder
                .defaultAdvisors(PromptChatMemoryAdvisor
                        .builder(chatMemory).build())
                .build();
        String content = chatClient1.prompt().user("你好,我是天下第一帅").call().content();
        System.out.println("=====++++++++++++++" + content);
        String content1 = chatClient1.prompt().user("我是谁").call().content();
        System.out.println("=====++++++++++++++" + content1);


    }

    //  @TestConfiguration 用于测试环境的配置
    //  配置ChatMemory
    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ChatMemory chatMemoryRepository(ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(100)
                    .chatMemoryRepository(chatMemoryRepository).build();
        }
    }

    //多用户记忆隔离
    @Test
    void testChatClientMultiUser(@Autowired ChatClient.Builder builder, @Autowired ChatMemory chatMemory) {
        ChatClient chatClient = builder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("    你是一个AI助手。\n" +
                        "    重要规则：\n" +
                        "    1. 你只能基于当前对话历史中的信息回答问题。\n" +
                        "    2. 如果用户问你是否认识某人，而对话历史中没有提到过这个人，你必须回答\"我不认识\"。\n" +
                        "    3. 不要猜测、假设或编造任何不在当前对话历史中的信息。\n" +
                        "    4. 每个用户是独立的，不要将不同用户的身份混淆。")
                .build();

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

        System.out.println("-------------------隔离线-------------------");

        // ===== 用户B：小杰，固定 conversationId = "user-B" =====
        String content2 = chatClient.prompt()
                .user("你好,我是小杰")
                .advisors(advisorSpec -> advisorSpec.params(Map.of(ChatMemory.CONVERSATION_ID, "user-B1")))
                .call()
                .content();
        System.out.println("===== 小杰第1次 ++++++" + content2);

        // 同一个 conversationId，但问是否认识晓云
        String content22 = chatClient.prompt()
                .user("你好,我是谁，你认识aacc的晓云吗")
                .advisors(advisorSpec -> advisorSpec.params(Map.of(ChatMemory.CONVERSATION_ID, "user-B1")))
                .call()
                .content();
        System.out.println("===== 小杰第2次 ++++++" + content22);
        // 预期：模型知道当前用户是小杰，但不认识晓云（因为晓云的记忆在 user-A 中）
    }


}
