package com.finance.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VoucherEntry {
    private Long id;
    private Long voucherId;
    private String accountCode;
    private String accountName;
    private String summary;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private Integer seq;
}
