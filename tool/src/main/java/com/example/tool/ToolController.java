package com.example.tool;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ToolController {

    private final ChatClient chatClient;
    private final ToolService toolService;

    public ToolController(ChatClient.Builder builder, ToolService toolService) {
        // 构造期只 build 一次；绝不使用 defaultToolCallbacks —— 它是累加语义，
        // 挂在共享 Builder 上会跨请求累积，第二次请求就 "Multiple tools with the same name"
        this.chatClient = builder.build();
        this.toolService = toolService;
    }

    @GetMapping("/tool")
    public Flux<String> ticket(@RequestParam String message) {
        // 仍在 Tomcat 线程，SecurityContext 有效
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 匿名时 auth 是 AnonymousAuthenticationToken，非 null 且 isAuthenticated() 为 true，
        // 所以不能用 "auth == null || !auth.isAuthenticated()" 来判断
        boolean anonymous = auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken;

        boolean isAdmin = !anonymous && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        List<ToolCallback> callbacks = toolService.getTools(isAdmin);

        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put("username", anonymous ? null : auth.getName());
        toolContext.put("isAdmin", isAdmin);

        return chatClient.prompt()
                .toolCallbacks(callbacks)   // 请求级，每次新建 spec，不污染共享状态
                .toolContext(toolContext)
                .user(message)
                .stream()
                .content();
    }
}
