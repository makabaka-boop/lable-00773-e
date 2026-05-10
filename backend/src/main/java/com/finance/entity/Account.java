package com.finance.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Account {
    private Long id;
    private String code;
    private String name;
    private String parentCode;
    private Integer level;
    private String direction;  // DEBIT借方 / CREDIT贷方
    private Boolean isEnabled;
    private LocalDateTime createTime;
}
