package com.example.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.stream.Stream;


@SpringBootTest
class QuickStartApplicationTests {

    @Test
    void sayHelloToDeepSeek(@Autowired DeepSeekChatModel model) {
        // 调用模型
        String hello = model.call("你好");
        System.out.println(hello);
    }

    @Test
    void sayHelloToDeepSeekStream(@Autowired DeepSeekChatModel model) {
        // 调用模型
        Flux<String> stram = model.stream("你好");
        stram.toIterable().forEach(System.out::println);
    }


    // 设置模型参数
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
        System.out.println(response.getResult().getOutput().getText());



    }
}
