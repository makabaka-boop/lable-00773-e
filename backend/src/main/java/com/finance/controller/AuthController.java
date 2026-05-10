package com.finance.controller;

import com.finance.common.Result;
import com.finance.entity.User;
import com.finance.service.UserService;
import com.finance.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     * 前端发送的密码已经过 SHA-256 加密
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password"); // 已加密的密码
        
        log.info("用户登录请求: username={}", username);

        // 使用数据库进行登录验证（密码已经是 SHA-256 加密后的）
        User user = userService.authenticateWithHashedPassword(username, password);
        
        if (user != null) {
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole().equals("ADMIN") ? "超级管理员" : "普通用户");
            userInfo.put("roleCode", user.getRole());
            data.put("user", userInfo);
            
            log.info("用户登录成功: username={}, role={}", username, user.getRole());
            return Result.success(data);
        } else {
            log.warn("用户登录失败: username={}", username);
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 是无状态的，客户端删除 token 即可
        // 如果需要服务端主动失效，可以使用黑名单机制（存 Redis）
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        // 从拦截器设置的属性中获取用户信息
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");
        
        if (userId != null) {
            User user = userService.findById(userId);
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("name", user.getName());
                userInfo.put("role", user.getRole().equals("ADMIN") ? "超级管理员" : "普通用户");
                userInfo.put("roleCode", user.getRole());
                return Result.success(userInfo);
            }
        }
        
        return Result.error(401, "未登录或登录已过期");
    }
}
