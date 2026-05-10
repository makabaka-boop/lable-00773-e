package com.finance.service;

import com.finance.entity.User;
import com.finance.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    public User authenticateWithHashedPassword(String username, String hashedPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (hashedPassword.equals(user.getPassword())) {
            return user;
        }
        return null;
    }
}
