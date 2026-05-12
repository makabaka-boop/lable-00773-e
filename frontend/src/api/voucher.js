import http from '../utils/http'

export const voucherApi = {
  list(params) {
    return http.get('/vouchers', { params })
  },

  page(params) {
    return http.get('/vouchers/page', { params })
  },

  getById(id) {
    return http.get(`/vouchers/${id}`)
  },

  save(data) {
    if (data.id) {
      return http.put(`/vouchers/${data.id}`, data)
    }
    return http.post('/vouchers', data)
  },

  post(id, reviewer) {
    return http.post(`/vouchers/${id}/post`, { reviewer })
  },

  void(id) {
    return http.post(`/vouchers/${id}/void`)
  },

  delete(id) {
    return http.delete(`/vouchers/${id}`)
  }
}
