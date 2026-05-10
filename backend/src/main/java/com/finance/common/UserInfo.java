package com.finance.common;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String name;
    private String role;
    private String roleCode;

    public static UserInfo from(com.finance.entity.User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setName(user.getName());
        info.setRoleCode(user.getRole());
        info.setRole("ADMIN".equals(user.getRole()) ? "超级管理员" : "普通用户");
        return info;
    }
}
