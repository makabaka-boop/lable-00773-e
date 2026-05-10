package com.finance.controller;

import com.finance.common.PageResult;
import com.finance.common.Result;
import com.finance.entity.AccountBalance;
import com.finance.service.LedgerService;
import com.finance.service.LedgerService.DetailLedgerResult;
import com.finance.service.LedgerService.DetailLedgerPageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {
    
    private final LedgerService ledgerService;

    @GetMapping("/detail")
    public Result<DetailLedgerResult> getDetailLedger(
            @RequestParam String accountCode,
            @RequestParam String period) {
        return Result.success(ledgerService.getDetailLedger(accountCode, period));
    }
    
    @GetMapping("/detail/page")
    public Result<DetailLedgerPageResult> getDetailLedgerPage(
            @RequestParam String accountCode,
            @RequestParam String period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(ledgerService.getDetailLedgerPage(accountCode, period, page, size));
    }

    @GetMapping("/balance")
    public Result<List<AccountBalance>> getBalanceSheet(@RequestParam String period) {
        return Result.success(ledgerService.getBalanceSheet(period));
    }
    
    @GetMapping("/balance/page")
    public Result<PageResult<AccountBalance>> getBalanceSheetPage(
            @RequestParam String period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(ledgerService.getBalanceSheetPage(period, page, size));
    }
}
