package com.finance.service;

import com.finance.entity.Account;
import com.finance.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountMapper accountMapper;

    public List<Account> findAll() {
        return accountMapper.findAll();
    }
    
    public Map<String, Object> findPage(int page, int size) {
        int offset = (page - 1) * size;
        List<Account> list = accountMapper.findPage(offset, size);
        int total = accountMapper.count();
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public List<Account> findEnabled() {
        return accountMapper.findEnabled();
    }

    public Account findByCode(String code) {
        return accountMapper.findByCode(code);
    }

    public Account findById(Long id) {
        return accountMapper.findById(id);
    }

    public void save(Account account) {
        if (account.getId() == null) {
            if (accountMapper.countByCode(account.getCode()) > 0) {
                throw new RuntimeException("科目编码已存在");
            }
            if (account.getLevel() == null) {
                account.setLevel(account.getParentCode() == null ? 1 : 2);
            }
            if (account.getIsEnabled() == null) {
                account.setIsEnabled(true);
            }
            accountMapper.insert(account);
        } else {
            accountMapper.update(account);
        }
    }

    public void deleteById(Long id) {
        Account account = accountMapper.findById(id);
        if (account != null) {
            List<Account> children = accountMapper.findByParentCode(account.getCode());
            if (!children.isEmpty()) {
                throw new RuntimeException("存在下级科目，无法删除");
            }
            accountMapper.deleteById(id);
        }
    }

    public List<Account> buildTree() {
        return accountMapper.findAll();
    }
}
