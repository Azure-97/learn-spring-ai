package com.example.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // 启用方法级安全控制 (如 @PreAuthorize)
public class SecurityConfig {

    /**
     * 1. 配置用户信息服务 (内存模式)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // 注意：这里传入的 password 必须是加密后 的，或者配合下面的 PasswordEncoder Bean 使用
        // 为了演示方便，这里直接构建用户对象，实际运行时 Spring Security 会自动使用下方的 encoder 进行校验
        UserDetails user = User.withUsername("user")
                .password("111111")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("111111")
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 3. 配置安全过滤链 (核心拦截规则)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 授权规则
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/tool", "/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )

                // 开启 Basic，方便用 curl -u 验证不同角色
                .httpBasic(Customizer.withDefaults())
                .with(new FormLoginConfigurer<>(), Customizer.withDefaults());


        return http.build();
    }
}