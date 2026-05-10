package com.finance.service;

import com.finance.entity.User;
import com.finance.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    /**
     * 验证用户登录（明文密码）
     */
    public User authenticate(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        // 验证密码（使用SHA-256加密比对）
        String hashedPassword = hashPassword(password);
        if (hashedPassword.equals(user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    /**
     * 验证用户登录（前端已加密的密码）
     * 前端使用 SHA-256 加密后传输，直接与数据库密码比对
     */
    public User authenticateWithHashedPassword(String username, String hashedPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        // 直接比对加密后的密码
        if (hashedPassword.equals(user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    /**
     * 密码加密（SHA-256）
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}
