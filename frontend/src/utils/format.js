export function formatMoney(value) {
  const num = parseFloat(value) || 0
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
}

export function getStatusText(status) {
  const statusMap = {
    DRAFT: '草稿',
    POSTED: '已过账',
    VOID: '已作废'
  }
  return statusMap[status] || status
}

export function getDirectionText(direction) {
  return direction === 'DEBIT' ? '借方' : '贷方'
}
