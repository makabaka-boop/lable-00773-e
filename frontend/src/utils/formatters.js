export const formatMoney = (v) =>
  (v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

export const voucherStatusText = (s) =>
  ({ DRAFT: '草稿', POSTED: '已过账', VOID: '已作废' }[s] || s)

export const directionText = (d) =>
  d === 'DEBIT' ? '借方' : '贷方'

export const roleText = (r) =>
  r === 'ADMIN' ? '超级管理员' : '普通用户'
