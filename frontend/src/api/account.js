import http from '../utils/http'

export const accountApi = {
  list() {
    return http.get('/accounts')
  },

  page(page = 1, size = 10) {
    return http.get('/accounts/page', { params: { page, size } })
  },

  listEnabled() {
    return http.get('/accounts/enabled')
  },

  getById(id) {
    return http.get(`/accounts/${id}`)
  },

  getByCode(code) {
    return http.get(`/accounts/code/${code}`)
  },

  save(data) {
    if (data.id) {
      return http.put(`/accounts/${data.id}`, data)
    }
    return http.post('/accounts', data)
  },

  delete(id) {
    return http.delete(`/accounts/${id}`)
  }
}
