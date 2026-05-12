package com.finance.enums;

import lombok.Getter;

@Getter
public enum AccountDirection {

    DEBIT("DEBIT", "借方"),
    CREDIT("CREDIT", "贷方");

    private final String code;
    private final String desc;

    AccountDirection(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AccountDirection fromCode(String code) {
        for (AccountDirection direction : values()) {
            if (direction.getCode().equals(code)) {
                return direction;
            }
        }
        return null;
    }
}
