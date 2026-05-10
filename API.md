# 金蝶财务管理系统 - API 接口文档

## 基础信息

- **基础路径**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200成功，500失败 |
| message | string | 提示信息 |
| data | object | 响应数据 |

---

## 一、会计科目管理

### 1.1 获取所有科目

**GET** `/api/accounts`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "code": "1001",
      "name": "库存现金",
      "parentCode": null,
      "level": 1,
      "direction": "DEBIT",
      "isEnabled": true,
      "createTime": "2026-01-25T10:00:00"
    }
  ]
}
```

### 1.2 获取启用的科目

**GET** `/api/accounts/enabled`

**响应**: 同上

### 1.3 根据ID获取科目

**GET** `/api/accounts/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 科目ID |

### 1.4 根据编码获取科目

**GET** `/api/accounts/code/{code}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | string | 是 | 科目编码 |

### 1.5 新增科目

**POST** `/api/accounts`

**请求体**:
```json
{
  "code": "100203",
  "name": "农业银行",
  "parentCode": "1002",
  "direction": "DEBIT",
  "isEnabled": true
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | string | 是 | 科目编码（唯一） |
| name | string | 是 | 科目名称 |
| parentCode | string | 否 | 父级科目编码 |
| direction | string | 是 | 余额方向: DEBIT(借方)/CREDIT(贷方) |
| isEnabled | boolean | 否 | 是否启用，默认true |

### 1.6 修改科目

**PUT** `/api/accounts/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 科目ID |

**请求体**: 同新增

### 1.7 删除科目

**DELETE** `/api/accounts/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 科目ID |

**注意**: 存在下级科目时无法删除

---

## 二、凭证管理

### 2.1 查询凭证列表

**GET** `/api/vouchers`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| period | string | 否 | 会计期间，格式 yyyy-MM |
| status | string | 否 | 状态: DRAFT/POSTED/VOID |
| voucherNo | string | 否 | 凭证号（模糊匹配） |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "voucherNo": "2026-01-0001",
      "voucherDate": "2026-01-25",
      "period": "2026-01",
      "attachmentCount": 2,
      "status": "DRAFT",
      "preparer": "张三",
      "reviewer": null,
      "createTime": "2026-01-25T10:00:00",
      "entries": [
        {
          "id": 1,
          "voucherId": 1,
          "accountCode": "1001",
          "accountName": "库存现金",
          "summary": "提取现金",
          "debitAmount": 1000.00,
          "creditAmount": 0,
          "seq": 1
        },
        {
          "id": 2,
          "voucherId": 1,
          "accountCode": "1002",
          "accountName": "银行存款",
          "summary": "提取现金",
          "debitAmount": 0,
          "creditAmount": 1000.00,
          "seq": 2
        }
      ]
    }
  ]
}
```

### 2.2 根据ID获取凭证

**GET** `/api/vouchers/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 凭证ID |

### 2.3 新增凭证

**POST** `/api/vouchers`

**请求体**:
```json
{
  "voucherDate": "2026-01-25",
  "attachmentCount": 2,
  "preparer": "张三",
  "entries": [
    {
      "accountCode": "1001",
      "accountName": "库存现金",
      "summary": "提取现金",
      "debitAmount": 1000.00,
      "creditAmount": 0
    },
    {
      "accountCode": "1002",
      "accountName": "银行存款",
      "summary": "提取现金",
      "debitAmount": 0,
      "creditAmount": 1000.00
    }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| voucherDate | string | 是 | 凭证日期 yyyy-MM-dd |
| attachmentCount | int | 否 | 附件张数 |
| preparer | string | 否 | 制单人 |
| entries | array | 是 | 凭证分录（至少1条） |

**分录字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| accountCode | string | 是 | 科目编码 |
| accountName | string | 否 | 科目名称 |
| summary | string | 否 | 摘要 |
| debitAmount | decimal | 否 | 借方金额 |
| creditAmount | decimal | 否 | 贷方金额 |

**注意**: 借方合计必须等于贷方合计（借贷平衡）

### 2.4 修改凭证

**PUT** `/api/vouchers/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 凭证ID |

**请求体**: 同新增

**注意**: 只能修改草稿状态的凭证

### 2.5 凭证过账（审核）

**POST** `/api/vouchers/{id}/post`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 凭证ID |

**请求体**:
```json
{
  "reviewer": "李四"
}
```

**注意**: 只能过账草稿状态的凭证

### 2.6 凭证作废

**POST** `/api/vouchers/{id}/void`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 凭证ID |

### 2.7 删除凭证

**DELETE** `/api/vouchers/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 凭证ID |

**注意**: 只能删除草稿状态的凭证

---

## 三、账簿查询

### 3.1 明细账查询

**GET** `/api/ledger/detail`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| accountCode | string | 是 | 科目编码 |
| period | string | 是 | 会计期间 yyyy-MM |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "entries": [
      {
        "id": 1,
        "voucherId": 1,
        "accountCode": "1001",
        "accountName": "库存现金",
        "summary": "提取现金",
        "debitAmount": 1000.00,
        "creditAmount": 0,
        "voucherNo": "2026-01-0001",
        "voucherDate": "2026-01-25"
      }
    ],
    "balance": {
      "accountCode": "1001",
      "period": "2026-01",
      "openingDebit": 5000.00,
      "openingCredit": 0,
      "currentDebit": 1000.00,
      "currentCredit": 500.00,
      "closingDebit": 5500.00,
      "closingCredit": 0
    }
  }
}
```

### 3.2 科目余额表查询

**GET** `/api/ledger/balance`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| period | string | 是 | 会计期间 yyyy-MM |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "accountCode": "1001",
      "period": "2026-01",
      "openingDebit": 5000.00,
      "openingCredit": 0,
      "currentDebit": 1000.00,
      "currentCredit": 500.00,
      "closingDebit": 5500.00,
      "closingCredit": 0
    }
  ]
}
```

---

## 四、数据字典

### 4.1 凭证状态

| 值 | 说明 |
|------|------|
| DRAFT | 草稿 |
| POSTED | 已过账 |
| VOID | 已作废 |

### 4.2 余额方向

| 值 | 说明 |
|------|------|
| DEBIT | 借方 |
| CREDIT | 贷方 |

---

## 五、错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 500 | 操作失败，查看message获取详细信息 |

**常见错误信息**:
- `科目编码已存在` - 新增科目时编码重复
- `存在下级科目，无法删除` - 删除科目时存在子科目
- `凭证分录不能为空` - 保存凭证时未添加分录
- `借贷不平衡` - 凭证借方合计不等于贷方合计
- `只能修改草稿状态的凭证` - 尝试修改已过账/作废的凭证
- `只能过账草稿状态的凭证` - 尝试过账非草稿状态的凭证
- `凭证已作废` - 尝试作废已作废的凭证
- `只能删除草稿状态的凭证` - 尝试删除已过账/作废的凭证
