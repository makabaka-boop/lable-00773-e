package com.finance.controller;

import com.finance.common.PageResult;
import com.finance.common.Result;
import com.finance.entity.Account;
import com.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;

    @GetMapping
    public Result<List<Account>> list() {
        return Result.success(accountService.findAll());
    }
    
    @GetMapping("/page")
    public Result<PageResult<Account>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(accountService.findPage(page, size));
    }

    @GetMapping("/enabled")
    public Result<List<Account>> listEnabled() {
        return Result.success(accountService.findEnabled());
    }

    @GetMapping("/{id}")
    public Result<Account> getById(@PathVariable Long id) {
        return Result.success(accountService.findById(id));
    }

    @GetMapping("/code/{code}")
    public Result<Account> getByCode(@PathVariable String code) {
        return Result.success(accountService.findByCode(code));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Account account) {
        accountService.save(account);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        accountService.save(account);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.deleteById(id);
        return Result.success();
    }
}
