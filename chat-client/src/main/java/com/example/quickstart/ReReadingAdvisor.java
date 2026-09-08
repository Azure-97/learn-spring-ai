package com.example.quickstart;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/29 周六 14:30
 * @Version 1.0
 **/
public class ReReadingAdvisor implements BaseAdvisor {
    private static final String DEFAULE_USER_TEXT_Advisor= """
                {re2_input_query}
                read agein:
                {re2_input_query}
            """;
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String contents = chatClientRequest.prompt().getContents();
        String re2InputQuery = PromptTemplate.builder().template(DEFAULE_USER_TEXT_Advisor).build().render(Map.of("re2_input_query", contents));
        ChatClientRequest request = chatClientRequest.mutate().prompt(Prompt.builder().content(re2InputQuery).build()).build();
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
