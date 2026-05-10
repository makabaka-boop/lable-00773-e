package com.finance.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.common.Result;
import com.finance.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String authorization = request.getHeader("Authorization");
        
        // 检查 Authorization 头
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendUnauthorizedResponse(response, "未提供认证令牌");
            return false;
        }
        
        String token = authorization.substring(7);
        
        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, "认证令牌无效或已过期");
            return false;
        }
        
        // 将用户信息存入请求属性，供后续使用
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            
            log.debug("JWT认证成功: userId={}, username={}, role={}", userId, username, role);
        } catch (Exception e) {
            log.error("解析JWT失败", e);
            sendUnauthorizedResponse(response, "认证令牌解析失败");
            return false;
        }
        
        return true;
    }
    
    /**
     * 发送未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
