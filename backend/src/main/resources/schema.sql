-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    name VARCHAR(100) COMMENT '姓名',
    role VARCHAR(50) NOT NULL COMMENT '角色: ADMIN管理员/USER普通用户',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会计科目表
CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '科目编码',
    name VARCHAR(100) NOT NULL COMMENT '科目名称',
    parent_code VARCHAR(20) COMMENT '父级科目编码',
    level INT DEFAULT 1 COMMENT '科目级次',
    direction VARCHAR(10) NOT NULL COMMENT '余额方向: DEBIT借方/CREDIT贷方',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_account_parent (parent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 凭证主表
CREATE TABLE IF NOT EXISTS voucher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(30) NOT NULL UNIQUE COMMENT '凭证号',
    voucher_date DATE NOT NULL COMMENT '凭证日期',
    period VARCHAR(7) NOT NULL COMMENT '会计期间 yyyy-MM',
    attachment_count INT DEFAULT 0 COMMENT '附件张数',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT草稿/POSTED已过账/VOID作废',
    preparer VARCHAR(50) COMMENT '制单人',
    reviewer VARCHAR(50) COMMENT '审核人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_voucher_period (period),
    INDEX idx_voucher_status (status),
    INDEX idx_voucher_date (voucher_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 凭证分录表
CREATE TABLE IF NOT EXISTS voucher_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id BIGINT NOT NULL COMMENT '凭证ID',
    account_code VARCHAR(20) NOT NULL COMMENT '科目编码',
    account_name VARCHAR(100) COMMENT '科目名称',
    summary VARCHAR(200) COMMENT '摘要',
    debit_amount DECIMAL(18,2) DEFAULT 0 COMMENT '借方金额',
    credit_amount DECIMAL(18,2) DEFAULT 0 COMMENT '贷方金额',
    seq INT DEFAULT 0 COMMENT '序号',
    INDEX idx_entry_voucher_id (voucher_id),
    INDEX idx_entry_account_code (account_code),
    FOREIGN KEY (voucher_id) REFERENCES voucher(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 科目余额表
CREATE TABLE IF NOT EXISTS account_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_code VARCHAR(20) NOT NULL COMMENT '科目编码',
    period VARCHAR(7) NOT NULL COMMENT '会计期间',
    opening_debit DECIMAL(18,2) DEFAULT 0 COMMENT '期初借方',
    opening_credit DECIMAL(18,2) DEFAULT 0 COMMENT '期初贷方',
    current_debit DECIMAL(18,2) DEFAULT 0 COMMENT '本期借方',
    current_credit DECIMAL(18,2) DEFAULT 0 COMMENT '本期贷方',
    closing_debit DECIMAL(18,2) DEFAULT 0 COMMENT '期末借方',
    closing_credit DECIMAL(18,2) DEFAULT 0 COMMENT '期末贷方',
    UNIQUE KEY uk_account_period (account_code, period),
    INDEX idx_balance_period (period),
    INDEX idx_balance_account (account_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator VARCHAR(50) COMMENT '操作人',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    module VARCHAR(50) NOT NULL COMMENT '模块',
    content VARCHAR(500) COMMENT '操作内容',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
