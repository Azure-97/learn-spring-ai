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
public class TestAdvisors {


     // 拦截器 日志拦截器 敏感词拦截器
    @Test
    void testChatClientAdvisors(@Autowired DashScopeChatModel dashScopeChatModel, @Value ("classpath:/file/prompt.st")  Resource resource) {

        ChatClient build = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(resource)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor( )
                        ,new SafeGuardAdvisor(List.of("敏感词1","敏感词2"))
                        ,new ReReadingAdvisor() // 重读拦截器
                )
                .build();



        String content = build.prompt()
                .user("我帅不帅")
                .call().content();
        System.out.println(content);
    }

}
