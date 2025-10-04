package com.grape.grape.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements  WebMvcConfigurer {


        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // 允许所有路径
                    .allowedOrigins("http://127.0.0.1:8080") // 允许前端的域名
                    .allowedOrigins("http://localhost:8080") // 允许前端的域名
                    .allowedOrigins("http://192.168.23.168:8080") // 允许前端的域名
                    .allowedOriginPatterns("http://192.168.*.*:*", "https://192.168.*.*:*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                    .allowedHeaders("*") // 允许所有请求头
                    .allowCredentials(true) // 允许携带凭证
                    .maxAge(3600); // 预检请求的缓存时间
    }
}

