import http from '../utils/http'

export const ledgerApi = {
  getDetail(accountCode, period) {
    return http.get('/ledger/detail', { params: { accountCode, period } })
  },

  getDetailPage(accountCode, period, page = 1, size = 10) {
    return http.get('/ledger/detail/page', { params: { accountCode, period, page, size } })
  },

  getBalance(period) {
    return http.get('/ledger/balance', { params: { period } })
  },

  getBalancePage(period, page = 1, size = 10) {
    return http.get('/ledger/balance/page', { params: { period, page, size } })
  }
}
