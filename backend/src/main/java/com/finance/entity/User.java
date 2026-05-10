package com.finance.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;  // ADMIN管理员 / USER普通用户
    private Boolean isEnabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
