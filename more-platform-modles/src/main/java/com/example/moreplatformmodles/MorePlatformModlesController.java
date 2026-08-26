package com.example.moreplatformmodles;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/26 周三 22:43
 * @Version 1.0
 **/
@RestController
public class MorePlatformModlesController {

    HashMap <String, ChatModel> platformModleMap = new HashMap<>();

    MorePlatformModlesController(DashScopeChatModel dashScopeChatModel, DeepSeekChatModel deepSeekChatModel){
        platformModleMap.put("dashscope", dashScopeChatModel);
        platformModleMap.put("deepseek", deepSeekChatModel);

    }

    //http://localhost:8080/chat?message=hello&platform=dashscope&modle=qwen-plus&temperature=0.7
    @RequestMapping(value = "/chat",produces =  "text/stream;charset=UTF-8")
    public  Flux<String> chat (String message,MorePlatformModlesOption option) {

        ChatModel chatModel = platformModleMap.get(option.getPlatform());
        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient chatClient = builder.defaultOptions(
                ChatOptions
                        .builder()
                        .temperature(option.getTemperature())
                        .build()).build();
        Flux<String> content = chatClient.prompt().user(message).stream().content();

        return content;
    }
}
