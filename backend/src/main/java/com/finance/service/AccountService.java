package com.finance.service;

import com.finance.common.PageResult;
import com.finance.entity.Account;
import com.finance.exception.BusinessException;
import com.finance.exception.ErrorCode;
import com.finance.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountMapper accountMapper;

    public List<Account> findAll() {
        return accountMapper.findAll();
    }
    
    public PageResult<Account> findPage(int page, int size) {
        int offset = PageResult.calculateOffset(page, size);
        List<Account> list = accountMapper.findPage(offset, size);
        long total = accountMapper.count();
        return PageResult.of(list, total, page, size);
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
                throw new BusinessException(ErrorCode.ACCOUNT_CODE_EXISTS);
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
                throw new BusinessException(ErrorCode.ACCOUNT_HAS_CHILDREN);
            }
            accountMapper.deleteById(id);
        }
    }

    public List<Account> buildTree() {
        return accountMapper.findAll();
    }
}
