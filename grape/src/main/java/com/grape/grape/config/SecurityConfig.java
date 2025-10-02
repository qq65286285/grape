package com.grape.grape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    /**
     * 配置WebSecurity：注册WebSecurityCustomizer的一个实例
     * 对应：configure(WebSecurity)
     */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers
                        ("/js/**", "/css/**",
                                "/images/**", "/**");
            }
        };
    }

}