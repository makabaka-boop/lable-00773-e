import request from './request'

export const accountApi = {
  list: () => request.get('/accounts'),
  page: (page = 1, size = 10) => request.get('/accounts/page', { params: { page, size } }),
  listEnabled: () => request.get('/accounts/enabled'),
  getById: (id) => request.get(`/accounts/${id}`),
  save: (data) => data.id ? request.put(`/accounts/${data.id}`, data) : request.post('/accounts', data),
  delete: (id) => request.delete(`/accounts/${id}`)
}
