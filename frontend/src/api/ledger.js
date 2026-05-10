import request from './request'

export const ledgerApi = {
  detail: (accountCode, period) => request.get('/ledger/detail', { params: { accountCode, period } }),
  detailPage: (accountCode, period, page = 1, size = 10) => request.get('/ledger/detail/page', { params: { accountCode, period, page, size } }),
  balance: (period) => request.get('/ledger/balance', { params: { period } }),
  balancePage: (period, page = 1, size = 10) => request.get('/ledger/balance/page', { params: { period, page, size } })
}
