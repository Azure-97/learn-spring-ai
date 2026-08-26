package com.example.quickstart;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;


@SpringBootTest
class QuickStartApplicationTests {

    // 非流式调用
    @Test
    void sayHelloToDeepSeek(@Autowired DeepSeekChatModel model) {
        // 调用模型
        String hello = model.call("你好");
        System.out.println(hello);
    }

    // 流式调用
    @Test
    void sayHelloToDeepSeekStream(@Autowired DeepSeekChatModel model) {
        // 调用模型
        Flux<String> stram = model.stream("你好");
        stram.toIterable().forEach(System.out::println);
    }


    // 设置模型参数
    // temperature越高，生成内容越不确定
    // maxtokens 生成内容的长度
    // stop 生成内容的结束符
    // DeepSeekAssistantMessage 是 DeepSeek 的输出

    @Test
    void setTempteraturetoDeepSeek(@Autowired DeepSeekChatModel model) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("\n");
        // 调用模型
        DeepSeekChatOptions build = DeepSeekChatOptions
                .builder()
                .model("deepseek-v4-flash")
                .temperature(0.5)
                .maxTokens(10240)
                .stop(objects)
                .build();
        Prompt prompt = new Prompt("写一句诗歌描述中国",build);
        ChatResponse response = model.call(prompt);
        DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) response.getResult().getOutput();

        System.out.println(output.getReasoningContent());
        System.out.println("------------------");
        System.out.println(output.getText());
    }

    @Test
    void getStreamThinkingLinkDeepSeek(@Autowired DeepSeekChatModel model) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("\n");
        // 调用模型
        DeepSeekChatOptions build = DeepSeekChatOptions
                .builder()
                .model("deepseek-v4-flash")
                .temperature(0.5)
                .build();
        Prompt prompt = new Prompt("写一句诗歌描述中国",build);
        Flux<ChatResponse> response = model.stream(prompt);
        response.toIterable().forEach(res->{
            DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) res.getResult().getOutput();

            System.out.println(output.getReasoningContent());
        });
        System.out.println("------------------");
        response.toIterable().forEach(res->{
            DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) res.getResult().getOutput();


            System.out.println(output.getText());
        });

    }

    // 调用AlibabaDashScope
    @Test
    void testqwen(@Autowired DashScopeChatModel model) {
        String aaa = model.call("写一句诗歌描述中国");
        System.out.println(aaa);
    }

    // 调用AlibabaDashScope
    @Test
    void text2image(  @Autowired DashScopeImageModel model) {

        DashScopeImageOptions options = DashScopeImageOptions.builder().model("wanx-v1").build();


        ImagePrompt prompt = new ImagePrompt("小狗",options);

        ImageResponse response = model.call(prompt);
        System.out.println(response.getResult().getOutput().getUrl());
        //  base64 编码的图片
        //response.getResult().getOutput().getB64Json();
        // 文件流



    }

    @Test
    void text2v(  @Autowired DashScopeImageModel model) {




    }
}
