package com.finance.service;

import com.finance.entity.Account;
import com.finance.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    void testFindAll() {
        List<Account> accounts = accountService.findAll();
        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
    }

    @Test
    void testFindEnabled() {
        List<Account> accounts = accountService.findEnabled();
        assertNotNull(accounts);
        accounts.forEach(a -> assertTrue(a.getIsEnabled()));
    }

    @Test
    void testFindByCode() {
        Account account = accountService.findByCode("1002");
        assertNotNull(account);
        assertEquals("银行存款", account.getName());
    }

    @Test
    void testSaveNewAccount() {
        Account account = new Account();
        account.setCode("9999");
        account.setName("测试科目");
        account.setDirection("DEBIT");
        
        accountService.save(account);
        
        assertNotNull(account.getId());
    }

    @Test
    void testDuplicateCodeThrowsException() {
        Account account = new Account();
        account.setCode("1002"); // 已存在的编码
        account.setName("重复科目");
        account.setDirection("DEBIT");
        
        assertThrows(BusinessException.class, () -> accountService.save(account));
    }

    @Test
    void testUpdateAccount() {
        Account account = accountService.findByCode("1001");
        assertNotNull(account);
        
        account.setName("库存现金-修改");
        accountService.save(account);
        
        Account updated = accountService.findById(account.getId());
        assertEquals("库存现金-修改", updated.getName());
    }

    @Test
    void testCannotDeleteAccountWithChildren() {
        // 1002 银行存款 有子科目 100201, 100202
        Account parent = accountService.findByCode("1002");
        assertNotNull(parent);
        
        assertThrows(BusinessException.class, () -> accountService.deleteById(parent.getId()));
    }
}
