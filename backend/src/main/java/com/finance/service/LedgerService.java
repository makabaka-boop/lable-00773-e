package com.finance.service;

import com.finance.common.PageResult;
import com.finance.entity.AccountBalance;
import com.finance.entity.VoucherEntry;
import com.finance.mapper.AccountBalanceMapper;
import com.finance.mapper.VoucherEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LedgerService {
    
    private final VoucherEntryMapper entryMapper;
    private final AccountBalanceMapper balanceMapper;

    // 明细账查询
    public Map<String, Object> getDetailLedger(String accountCode, String period) {
        Map<String, Object> result = new HashMap<>();
        List<VoucherEntry> entries = entryMapper.findByAccountAndPeriod(accountCode, period);
        AccountBalance balance = balanceMapper.findByAccountAndPeriod(accountCode, period);
        result.put("entries", entries);
        result.put("balance", balance);
        return result;
    }
    
    // 明细账分页查询
    public Map<String, Object> getDetailLedgerPage(String accountCode, String period, int page, int size) {
        int offset = (page - 1) * size;
        List<VoucherEntry> entries = entryMapper.findByAccountAndPeriodPage(accountCode, period, offset, size);
        AccountBalance balance = balanceMapper.findByAccountAndPeriod(accountCode, period);
        int total = entryMapper.countByAccountAndPeriod(accountCode, period);
        Map<String, Object> result = new HashMap<>();
        result.put("list", entries);
        result.put("balance", balance);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    // 科目余额表
    public List<AccountBalance> getBalanceSheet(String period) {
        return balanceMapper.findByPeriod(period);
    }
    
    // 科目余额表分页
    public PageResult<AccountBalance> getBalanceSheetPage(String period, int page, int size) {
        int offset = (page - 1) * size;
        List<AccountBalance> list = balanceMapper.findByPeriodPage(period, offset, size);
        int total = balanceMapper.countByPeriod(period);
        return PageResult.of(list, total, page, size);
    }
}
