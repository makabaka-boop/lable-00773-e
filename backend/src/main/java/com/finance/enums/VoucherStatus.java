package com.finance.enums;

import lombok.Getter;

@Getter
public enum VoucherStatus {

    DRAFT("DRAFT", "草稿"),
    POSTED("POSTED", "已过账"),
    VOID("VOID", "已作废");

    private final String code;
    private final String desc;

    VoucherStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VoucherStatus fromCode(String code) {
        for (VoucherStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
