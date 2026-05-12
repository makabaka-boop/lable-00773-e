package com.finance.service;

import com.finance.common.PageResult;
import com.finance.entity.Voucher;
import com.finance.entity.VoucherEntry;
import com.finance.enums.VoucherStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Test
    void testSaveVoucher() {
        Voucher voucher = createTestVoucher();
        voucherService.save(voucher);
        
        assertNotNull(voucher.getId());
        assertEquals(VoucherStatus.DRAFT.getCode(), voucher.getStatus());
    }

    @Test
    void testFindByCondition() {
        List<Voucher> vouchers = voucherService.findByCondition("2026-01", null, null);
        assertNotNull(vouchers);
    }

    @Test
    void testFindByConditionPage() {
        PageResult<Voucher> result = voucherService.findByConditionPage("2026-01", null, null, 1, 10);
        assertNotNull(result.getList());
        assertNotNull(result.getTotal());
    }

    @Test
    void testValidateVoucherBalance() {
        Voucher voucher = new Voucher();
        voucher.setVoucherDate(LocalDate.now());
        voucher.setPreparer("测试");
        
        VoucherEntry entry1 = new VoucherEntry();
        entry1.setAccountCode("1002");
        entry1.setAccountName("银行存款");
        entry1.setDebitAmount(new BigDecimal("1000"));
        entry1.setCreditAmount(BigDecimal.ZERO);
        
        VoucherEntry entry2 = new VoucherEntry();
        entry2.setAccountCode("6001");
        entry2.setAccountName("主营业务收入");
        entry2.setDebitAmount(BigDecimal.ZERO);
        entry2.setCreditAmount(new BigDecimal("500")); // 借贷不平衡
        
        voucher.setEntries(Arrays.asList(entry1, entry2));
        
        assertThrows(RuntimeException.class, () -> voucherService.save(voucher));
    }

    @Test
    void testPostVoucher() {
        // 先创建凭证
        Voucher voucher = createTestVoucher();
        voucherService.save(voucher);
        
        // 过账
        voucherService.post(voucher.getId(), "审核人");
        
        // 验证状态
        Voucher posted = voucherService.findById(voucher.getId());
        assertEquals(VoucherStatus.POSTED.getCode(), posted.getStatus());
    }

    @Test
    void testVoidVoucher() {
        Voucher voucher = createTestVoucher();
        voucherService.save(voucher);
        
        voucherService.voidVoucher(voucher.getId());
        
        Voucher voided = voucherService.findById(voucher.getId());
        assertEquals(VoucherStatus.VOID.getCode(), voided.getStatus());
    }

    @Test
    void testDeleteDraftVoucher() {
        Voucher voucher = createTestVoucher();
        voucherService.save(voucher);
        Long id = voucher.getId();
        
        voucherService.deleteById(id);
        
        assertNull(voucherService.findById(id));
    }

    @Test
    void testCannotDeletePostedVoucher() {
        Voucher voucher = createTestVoucher();
        voucherService.save(voucher);
        voucherService.post(voucher.getId(), "审核人");
        
        assertThrows(RuntimeException.class, () -> voucherService.deleteById(voucher.getId()));
    }

    private Voucher createTestVoucher() {
        Voucher voucher = new Voucher();
        voucher.setVoucherDate(LocalDate.now());
        voucher.setPreparer("测试用户");
        voucher.setAttachmentCount(0);
        
        VoucherEntry entry1 = new VoucherEntry();
        entry1.setAccountCode("1002");
        entry1.setAccountName("银行存款");
        entry1.setSummary("测试分录");
        entry1.setDebitAmount(new BigDecimal("1000"));
        entry1.setCreditAmount(BigDecimal.ZERO);
        
        VoucherEntry entry2 = new VoucherEntry();
        entry2.setAccountCode("6001");
        entry2.setAccountName("主营业务收入");
        entry2.setSummary("测试分录");
        entry2.setDebitAmount(BigDecimal.ZERO);
        entry2.setCreditAmount(new BigDecimal("1000"));
        
        voucher.setEntries(Arrays.asList(entry1, entry2));
        return voucher;
    }
}
