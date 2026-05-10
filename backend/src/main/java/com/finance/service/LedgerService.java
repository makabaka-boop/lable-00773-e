package com.finance.service;

import com.finance.common.PageResult;
import com.finance.entity.AccountBalance;
import com.finance.entity.VoucherEntry;
import com.finance.mapper.AccountBalanceMapper;
import com.finance.mapper.VoucherEntryMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {
    
    private final VoucherEntryMapper entryMapper;
    private final AccountBalanceMapper balanceMapper;

    @Data
    public static class DetailLedgerResult {
        private List<VoucherEntry> entries;
        private AccountBalance balance;

        public static DetailLedgerResult of(List<VoucherEntry> entries, AccountBalance balance) {
            DetailLedgerResult result = new DetailLedgerResult();
            result.setEntries(entries);
            result.setBalance(balance);
            return result;
        }
    }

    @Data
    public static class DetailLedgerPageResult {
        private List<VoucherEntry> entries;
        private AccountBalance balance;
        private long total;
        private int page;
        private int size;

        public static DetailLedgerPageResult of(List<VoucherEntry> entries, AccountBalance balance, long total, int page, int size) {
            DetailLedgerPageResult result = new DetailLedgerPageResult();
            result.setEntries(entries);
            result.setBalance(balance);
            result.setTotal(total);
            result.setPage(page);
            result.setSize(size);
            return result;
        }
    }

    public DetailLedgerResult getDetailLedger(String accountCode, String period) {
        List<VoucherEntry> entries = entryMapper.findByAccountAndPeriod(accountCode, period);
        AccountBalance balance = balanceMapper.findByAccountAndPeriod(accountCode, period);
        return DetailLedgerResult.of(entries, balance);
    }
    
    public DetailLedgerPageResult getDetailLedgerPage(String accountCode, String period, int page, int size) {
        int offset = PageResult.calcOffset(page, size);
        List<VoucherEntry> entries = entryMapper.findByAccountAndPeriodPage(accountCode, period, offset, size);
        AccountBalance balance = balanceMapper.findByAccountAndPeriod(accountCode, period);
        int total = entryMapper.countByAccountAndPeriod(accountCode, period);
        return DetailLedgerPageResult.of(entries, balance, total, page, size);
    }

    public List<AccountBalance> getBalanceSheet(String period) {
        return balanceMapper.findByPeriod(period);
    }
    
    public PageResult<AccountBalance> getBalanceSheetPage(String period, int page, int size) {
        int offset = PageResult.calcOffset(page, size);
        List<AccountBalance> list = balanceMapper.findByPeriodPage(period, offset, size);
        int total = balanceMapper.countByPeriod(period);
        return PageResult.of(list, total, page, size);
    }
}
