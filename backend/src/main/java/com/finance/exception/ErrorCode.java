package com.finance.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试"),

    ACCOUNT_CODE_EXISTS(1001, "科目编码已存在"),
    ACCOUNT_HAS_CHILDREN(1002, "存在下级科目，无法删除"),
    ACCOUNT_NOT_FOUND(1003, "科目不存在"),

    VOUCHER_ENTRIES_EMPTY(2001, "凭证分录不能为空"),
    VOUCHER_BALANCE_NOT_MATCH(2002, "借贷不平衡"),
    VOUCHER_NOT_DRAFT(2003, "只能修改草稿状态的凭证"),
    VOUCHER_NOT_FOUND(2004, "凭证不存在"),
    VOUCHER_CANNOT_POST(2005, "只能过账草稿状态的凭证"),
    VOUCHER_ALREADY_VOID(2006, "凭证已作废"),
    VOUCHER_CANNOT_DELETE(2007, "只能删除草稿状态的凭证"),

    USERNAME_OR_PASSWORD_ERROR(3001, "用户名或密码错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
