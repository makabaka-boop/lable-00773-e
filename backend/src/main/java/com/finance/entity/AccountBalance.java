package com.finance.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountBalance {
    private Long id;
    private String accountCode;
    private String period;
    private BigDecimal openingDebit;
    private BigDecimal openingCredit;
    private BigDecimal currentDebit;
    private BigDecimal currentCredit;
    private BigDecimal closingDebit;
    private BigDecimal closingCredit;
}
