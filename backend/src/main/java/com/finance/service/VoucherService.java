package com.finance.service;

import com.finance.common.PageResult;
import com.finance.entity.Account;
import com.finance.entity.AccountBalance;
import com.finance.entity.Voucher;
import com.finance.entity.VoucherEntry;
import com.finance.exception.BusinessException;
import com.finance.mapper.AccountBalanceMapper;
import com.finance.mapper.AccountMapper;
import com.finance.mapper.VoucherMapper;
import com.finance.mapper.VoucherEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {
    
    private final VoucherMapper voucherMapper;
    private final VoucherEntryMapper entryMapper;
    private final AccountBalanceMapper balanceMapper;
    private final AccountMapper accountMapper;

    public List<Voucher> findByCondition(String period, String status, String voucherNo) {
        List<Voucher> vouchers = voucherMapper.findByCondition(period, status, voucherNo);
        for (Voucher v : vouchers) {
            v.setEntries(entryMapper.findByVoucherId(v.getId()));
        }
        return vouchers;
    }
    
    public PageResult<Voucher> findByConditionPage(String period, String status, String voucherNo, int page, int size) {
        int offset = PageResult.calcOffset(page, size);
        List<Voucher> vouchers = voucherMapper.findByConditionPage(period, status, voucherNo, offset, size);
        for (Voucher v : vouchers) {
            v.setEntries(entryMapper.findByVoucherId(v.getId()));
        }
        int total = voucherMapper.countByCondition(period, status, voucherNo);
        return PageResult.of(vouchers, total, page, size);
    }

    public Voucher findById(Long id) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher != null) {
            voucher.setEntries(entryMapper.findByVoucherId(id));
        }
        return voucher;
    }

    @Transactional
    public void save(Voucher voucher) {
        validateVoucher(voucher);
        
        if (voucher.getId() == null) {
            String period = voucher.getVoucherDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            voucher.setPeriod(period);
            voucher.setVoucherNo(generateVoucherNo(period));
            voucher.setStatus("DRAFT");
            voucherMapper.insert(voucher);
        } else {
            Voucher existing = voucherMapper.findById(voucher.getId());
            if (!"DRAFT".equals(existing.getStatus())) {
                throw new BusinessException("只能修改草稿状态的凭证");
            }
            voucherMapper.update(voucher);
            entryMapper.deleteByVoucherId(voucher.getId());
        }

        if (voucher.getEntries() != null && !voucher.getEntries().isEmpty()) {
            int seq = 1;
            for (VoucherEntry entry : voucher.getEntries()) {
                entry.setVoucherId(voucher.getId());
                entry.setSeq(seq++);
            }
            entryMapper.batchInsert(voucher.getEntries());
        }
    }

    private void validateVoucher(Voucher voucher) {
        if (voucher.getEntries() == null || voucher.getEntries().isEmpty()) {
            throw new BusinessException("凭证分录不能为空");
        }
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        for (VoucherEntry entry : voucher.getEntries()) {
            if (entry.getDebitAmount() != null) totalDebit = totalDebit.add(entry.getDebitAmount());
            if (entry.getCreditAmount() != null) totalCredit = totalCredit.add(entry.getCreditAmount());
        }
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessException("借贷不平衡");
        }
    }

    private String generateVoucherNo(String period) {
        String maxNo = voucherMapper.findMaxVoucherNo(period);
        int nextNum = 1;
        if (maxNo != null) {
            String numPart = maxNo.substring(maxNo.lastIndexOf("-") + 1);
            nextNum = Integer.parseInt(numPart) + 1;
        }
        return period + "-" + String.format("%04d", nextNum);
    }

    @Transactional
    public void post(Long id, String reviewer) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if (!"DRAFT".equals(voucher.getStatus())) {
            throw new BusinessException("只能过账草稿状态的凭证");
        }
        
        // 更新凭证状态
        voucherMapper.post(id, reviewer);
        
        // 更新科目余额表
        List<VoucherEntry> entries = entryMapper.findByVoucherId(id);
        String period = voucher.getPeriod();
        
        for (VoucherEntry entry : entries) {
            updateAccountBalance(entry.getAccountCode(), period, 
                entry.getDebitAmount(), entry.getCreditAmount());
        }
    }
    
    /**
     * 更新科目余额表
     */
    private void updateAccountBalance(String accountCode, String period, 
            BigDecimal debitAmount, BigDecimal creditAmount) {
        
        Account account = accountMapper.findByCode(accountCode);
        if (account == null) return;
        
        AccountBalance balance = balanceMapper.findByAccountAndPeriod(accountCode, period);
        
        if (balance == null) {
            // 新建余额记录
            balance = new AccountBalance();
            balance.setAccountCode(accountCode);
            balance.setPeriod(period);
            balance.setOpeningDebit(BigDecimal.ZERO);
            balance.setOpeningCredit(BigDecimal.ZERO);
            balance.setCurrentDebit(debitAmount != null ? debitAmount : BigDecimal.ZERO);
            balance.setCurrentCredit(creditAmount != null ? creditAmount : BigDecimal.ZERO);
            
            // 计算期末余额
            calculateClosingBalance(balance, account.getDirection());
            balanceMapper.insert(balance);
        } else {
            // 累加本期发生额
            BigDecimal newDebit = balance.getCurrentDebit()
                .add(debitAmount != null ? debitAmount : BigDecimal.ZERO);
            BigDecimal newCredit = balance.getCurrentCredit()
                .add(creditAmount != null ? creditAmount : BigDecimal.ZERO);
            
            balance.setCurrentDebit(newDebit);
            balance.setCurrentCredit(newCredit);
            
            // 重新计算期末余额
            calculateClosingBalance(balance, account.getDirection());
            balanceMapper.update(balance);
        }
    }
    
    /**
     * 计算期末余额
     * 借方科目: 期末借方 = 期初借方 + 本期借方 - 本期贷方
     * 贷方科目: 期末贷方 = 期初贷方 + 本期贷方 - 本期借方
     */
    private void calculateClosingBalance(AccountBalance balance, String direction) {
        BigDecimal openingDebit = balance.getOpeningDebit() != null ? balance.getOpeningDebit() : BigDecimal.ZERO;
        BigDecimal openingCredit = balance.getOpeningCredit() != null ? balance.getOpeningCredit() : BigDecimal.ZERO;
        BigDecimal currentDebit = balance.getCurrentDebit() != null ? balance.getCurrentDebit() : BigDecimal.ZERO;
        BigDecimal currentCredit = balance.getCurrentCredit() != null ? balance.getCurrentCredit() : BigDecimal.ZERO;
        
        if ("DEBIT".equals(direction)) {
            // 借方科目
            BigDecimal closing = openingDebit.add(currentDebit).subtract(currentCredit);
            if (closing.compareTo(BigDecimal.ZERO) >= 0) {
                balance.setClosingDebit(closing);
                balance.setClosingCredit(BigDecimal.ZERO);
            } else {
                balance.setClosingDebit(BigDecimal.ZERO);
                balance.setClosingCredit(closing.negate());
            }
        } else {
            // 贷方科目
            BigDecimal closing = openingCredit.add(currentCredit).subtract(currentDebit);
            if (closing.compareTo(BigDecimal.ZERO) >= 0) {
                balance.setClosingCredit(closing);
                balance.setClosingDebit(BigDecimal.ZERO);
            } else {
                balance.setClosingCredit(BigDecimal.ZERO);
                balance.setClosingDebit(closing.negate());
            }
        }
    }

    @Transactional
    public void voidVoucher(Long id) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher == null) {
            throw new BusinessException("凭证不存在");
        }
        if ("VOID".equals(voucher.getStatus())) {
            throw new BusinessException("凭证已作废");
        }
        voucherMapper.voidVoucher(id);
    }

    @Transactional
    public void deleteById(Long id) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher != null && !"DRAFT".equals(voucher.getStatus())) {
            throw new BusinessException("只能删除草稿状态的凭证");
        }
        voucherMapper.deleteById(id);
    }
}
