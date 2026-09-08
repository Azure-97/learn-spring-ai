package com.example.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工具由本类手工构建，不使用 @Tool 注解 + MethodToolCallbackProvider 自动扫描。
 *
 * 两种方式不要混用：注解和手工 schema 会形成"两份真相"（工具名、描述、参数
 * 各维护一份），而且一旦有人再调用 defaultTools(toolService) 就会重复注册，
 * 触发 "Multiple tools with the same name"。
 */
@Service
public class ToolService {

    private static final ToolDefinition TICKET = ToolDefinition.builder()
            .name("ticket")
            .description("预定车票")
            .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "ticketNumber": {
                                "type": "string",
                                "description": "车票号码"
                            }
                        },
                        "required": ["ticketNumber"]
                    }
                    """)
            .build();

    private static final ToolDefinition GET_WEATHER = ToolDefinition.builder()
            .name("getWeather")
            .description("获取今天的天气")
            .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "where": {
                                "type": "string",
                                "description": "地点"
                            }
                        },
                        "required": ["where"]
                    }
                    """)
            .build();

    private final TicketService ticketService;

    // ToolCallback 无状态且线程安全，构造期建好复用，不必每次请求反射重建
    private final ToolCallback weatherCallback;
    private final ToolCallback ticketCallback;

    public ToolService(TicketService ticketService) {
        this.ticketService = ticketService;
        this.weatherCallback = build(GET_WEATHER, "getWeather", String.class);
        this.ticketCallback = build(TICKET, "bookTicket", String.class, ToolContext.class);
    }

    /**
     * 按角色获取工具列表。
     *
     * @param isAdmin 是否管理员；管理员额外获得订票工具
     */
    public List<ToolCallback> getTools(boolean isAdmin) {
        List<ToolCallback> tools = new ArrayList<>();
        tools.add(this.weatherCallback);          // 任何人都可以查天气
        if (isAdmin) {
            tools.add(this.ticketCallback);       // 只有管理员能订票
        }
        return tools;
    }

    /**
     * 关键点：必须用 ToolService.class 而不是 getClass()。
     * 后者在 Bean 被代理时拿到的是 CGLIB 子类（ToolService$$SpringCGLIB$$0），
     * findMethod 可能返回 null，导致 build() 抛 "toolMethod cannot be null"。
     */
    private ToolCallback build(ToolDefinition definition, String methodName, Class<?>... paramTypes) {
        Method method = ReflectionUtils.findMethod(ToolService.class, methodName, paramTypes);
        Assert.notNull(method, "找不到工具方法 [" + methodName + "]，请核对参数签名");
        return MethodToolCallback.builder()
                .toolDefinition(definition)
                .toolMethod(method)
                .toolObject(this)
                .build();
    }

    /**
     * ToolContext 参数由 Spring AI 自动注入，不出现在上面手写的 inputSchema 里。
     */
    public String bookTicket(String ticketNumber, ToolContext toolContext) {
        Map<String, Object> ctx = toolContext.getContext();

        if (ctx.get("username") == null) {
            return "权限不足：预订车票需要管理员(ADMIN)权限，您当前尚未登录，请先登录后再试。";
        }
        if (!Boolean.TRUE.equals(ctx.get("isAdmin"))) {
            return "权限不足：预订车票需要 ADMIN 角色，您当前的角色为 USER，请联系管理员开通权限。";
        }
        return ticketService.bookTicket((String) ctx.get("username"), ticketNumber);
    }

    public String getWeather(String where) {
        return where + "的天气是大晴天，温度20度";
    }
}
