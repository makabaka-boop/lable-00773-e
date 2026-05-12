package com.finance.controller;

import com.finance.common.Result;
import com.finance.entity.User;
import com.finance.exception.BusinessException;
import com.finance.exception.ErrorCode;
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
        String password = loginRequest.get("password");
        
        log.info("用户登录请求: username={}", username);

        User user = userService.authenticateWithHashedPassword(username, password);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("name", user.getName());
        userInfo.put("role", "ADMIN".equals(user.getRole()) ? "超级管理员" : "普通用户");
        userInfo.put("roleCode", user.getRole());
        data.put("user", userInfo);
        
        log.info("用户登录成功: username={}, role={}", username, user.getRole());
        return Result.success(data);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId != null) {
            User user = userService.findById(userId);
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("name", user.getName());
                userInfo.put("role", "ADMIN".equals(user.getRole()) ? "超级管理员" : "普通用户");
                userInfo.put("roleCode", user.getRole());
                return Result.success(userInfo);
            }
        }
        
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
    }
}
