package com.example.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/30 周日 16:33
 * @Version 1.0
 **/
@SpringBootTest
public class TestStructure  {

    @Test
    void testChatClient(@Autowired ChatClient.Builder chatClient) {
        ChatClient build = chatClient.build();
        Boolean _boolean = build.prompt().system("如果是投诉就返回true，否则返回false").user("你好").call().entity(Boolean.class);
        System.out.println(_boolean);
    }


    @Test
    void testChatClient2(@Autowired DashScopeChatModel dashScopeChatModel) {

            ChatClient build = ChatClient.builder(dashScopeChatModel).build();
            ShippingAddress hello = build.prompt().user("浙江省嘉兴市南湖区中港路88号翡翠花园3栋2单元1501室，张明，13812345678").call().entity(ShippingAddress.class);
            System.out.println(hello);
    }
    static class ShippingAddress implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 地址ID */
        public Long id;

        /** 用户ID */
        private Long userId;

        /** 收货人姓名 */
        public String receiverName;

        /** 收货人手机号 */
        public String phone;

        /** 省 */
        public String province;

        /** 市 */
        public String city;

        /** 区/县 */
        public String district;

        /** 详细地址 */
        public String address;

        /** 邮编 */
        public String zipCode;

        /** 是否默认地址（0-否，1-是） */
        public Integer isDefault;
        @Override
        public String toString() {
            return  id + "," + userId + "," + receiverName + "," + phone + "," + province + "," + city + "," + district + "," + address + "," + zipCode + "," + isDefault;
        }
    }


}
