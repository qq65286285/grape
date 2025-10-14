package com.grape.grape.config.jwttools;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 鈥済in"
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 豁免路径：不需要校验token的接口
        if (requestURI.startsWith("/user/register") || requestURI.startsWith("/auth/login")) {
            return true;
        }
        
        // 获取Authorization头
        String authorizationHeader = request.getHeader("Authorization");
        
        // 检查是否有Authorization头且以Bearer开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 去掉"Bearer "前缀
            String token = authorizationHeader.substring(7);
            
            // 验证token有效性（这里需要您实现具体的JWT验证逻辑）
            if (isValidToken(token)) {
                // token有效，放行请求
                return true;
            }
        }
        
        // token无效或缺失，返回401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"error\":\"token过期，请重新登录\"}");
        return false;
    }
    
    // 验证token有效性的方法（需要根据实际JWT库实现）
    private boolean isValidToken(String token) {
        try {
            // 这里使用JWT库验证token，例如jjwt
            // 如果没有抛出异常，则token有效
            return JwtUtils.verify(token);
        } catch (Exception e) {
            // token无效
            return false;
        }
    }
}
