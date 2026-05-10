package com.finance.controller;

import com.finance.common.Result;
import com.finance.common.UserInfo;
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

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        log.info("用户登录请求: username={}", username);

        User user = userService.authenticateWithHashedPassword(username, password);
        
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", UserInfo.from(user));
            
            log.info("用户登录成功: username={}, role={}", username, user.getRole());
            return Result.success(data);
        } else {
            log.warn("用户登录失败: username={}", username);
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/current")
    public Result<UserInfo> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId != null) {
            User user = userService.findById(userId);
            if (user != null) {
                return Result.success(UserInfo.from(user));
            }
        }
        
        return Result.error(401, "未登录或登录已过期");
    }
}
