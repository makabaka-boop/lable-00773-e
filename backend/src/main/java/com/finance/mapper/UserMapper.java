package com.finance.mapper;

import com.finance.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_enabled = TRUE")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    @Insert("INSERT INTO sys_user (username, password, name, role, is_enabled) VALUES (#{username}, #{password}, #{name}, #{role}, #{isEnabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE sys_user SET name = #{name}, role = #{role}, is_enabled = #{isEnabled}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(User user);
}
