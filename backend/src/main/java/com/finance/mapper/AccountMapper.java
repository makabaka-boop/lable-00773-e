package com.finance.mapper;

import com.finance.entity.Account;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface AccountMapper {
    
    @Select("SELECT * FROM account ORDER BY code")
    List<Account> findAll();
    
    @Select("SELECT * FROM account ORDER BY code LIMIT #{size} OFFSET #{offset}")
    List<Account> findPage(@Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT COUNT(*) FROM account")
    int count();
    
    @Select("SELECT * FROM account WHERE code = #{code}")
    Account findByCode(String code);
    
    @Select("SELECT * FROM account WHERE id = #{id}")
    Account findById(Long id);
    
    @Select("SELECT * FROM account WHERE parent_code = #{parentCode} ORDER BY code")
    List<Account> findByParentCode(String parentCode);
    
    @Select("SELECT * FROM account WHERE is_enabled = true ORDER BY code")
    List<Account> findEnabled();
    
    @Insert("INSERT INTO account (code, name, parent_code, level, direction, is_enabled) " +
            "VALUES (#{code}, #{name}, #{parentCode}, #{level}, #{direction}, #{isEnabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Account account);
    
    @Update("UPDATE account SET name=#{name}, parent_code=#{parentCode}, level=#{level}, " +
            "direction=#{direction}, is_enabled=#{isEnabled} WHERE id=#{id}")
    int update(Account account);
    
    @Delete("DELETE FROM account WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM account WHERE code = #{code}")
    int countByCode(String code);
}
