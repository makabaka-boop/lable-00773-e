import http from '../utils/http'
import { hashPassword } from '../utils/crypto'

export const authApi = {
  async login(username, password) {
    const hashedPassword = await hashPassword(password)
    return http.post('/auth/login', { username, password: hashedPassword })
  },

  logout() {
    return http.post('/auth/logout')
  },

  getCurrentUser() {
    return http.get('/auth/current')
  }
}
