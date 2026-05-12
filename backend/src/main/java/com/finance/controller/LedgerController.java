package com.finance.controller;

import com.finance.common.PageResult;
import com.finance.common.Result;
import com.finance.entity.AccountBalance;
import com.finance.entity.VoucherEntry;
import com.finance.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {
    
    private final LedgerService ledgerService;

    // 明细账
    @GetMapping("/detail")
    public Result<Map<String, Object>> getDetailLedger(
            @RequestParam String accountCode,
            @RequestParam String period) {
        return Result.success(ledgerService.getDetailLedger(accountCode, period));
    }
    
    // 明细账分页
    @GetMapping("/detail/page")
    public Result<Map<String, Object>> getDetailLedgerPage(
            @RequestParam String accountCode,
            @RequestParam String period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(ledgerService.getDetailLedgerPage(accountCode, period, page, size));
    }

    // 科目余额表
    @GetMapping("/balance")
    public Result<List<AccountBalance>> getBalanceSheet(@RequestParam String period) {
        return Result.success(ledgerService.getBalanceSheet(period));
    }
    
    // 科目余额表分页
    @GetMapping("/balance/page")
    public Result<PageResult<AccountBalance>> getBalanceSheetPage(
            @RequestParam String period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(ledgerService.getBalanceSheetPage(period, page, size));
    }
}
