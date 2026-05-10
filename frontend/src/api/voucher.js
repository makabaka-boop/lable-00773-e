import request from './request'

export const voucherApi = {
  list: (params) => request.get('/vouchers', { params }),
  page: (params) => request.get('/vouchers/page', { params }),
  getById: (id) => request.get(`/vouchers/${id}`),
  save: (data) => data.id ? request.put(`/vouchers/${data.id}`, data) : request.post('/vouchers', data),
  post: (id, reviewer) => request.post(`/vouchers/${id}/post`, { reviewer }),
  void: (id) => request.post(`/vouchers/${id}/void`),
  delete: (id) => request.delete(`/vouchers/${id}`)
}
