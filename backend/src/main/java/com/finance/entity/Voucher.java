package com.finance.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Voucher {
    private Long id;
    private String voucherNo;
    private LocalDate voucherDate;
    private String period;
    private Integer attachmentCount;
    private String status;  // DRAFT草稿 / POSTED已过账 / VOID作废
    private String preparer;
    private String reviewer;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<VoucherEntry> entries;
}
