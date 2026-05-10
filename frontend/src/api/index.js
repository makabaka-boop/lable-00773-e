import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { hashPassword } from '../utils/crypto'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        // 未授权，清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
      } else if (error.response.status >= 500) {
        ElMessage.error('服务器错误，请稍后重试')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// 会计科目
export const accountApi = {
  list: () => api.get('/accounts'),
  page: (page = 1, size = 10) => api.get('/accounts/page', { params: { page, size } }),
  listEnabled: () => api.get('/accounts/enabled'),
  getById: (id) => api.get(`/accounts/${id}`),
  save: (data) => data.id ? api.put(`/accounts/${data.id}`, data) : api.post('/accounts', data),
  delete: (id) => api.delete(`/accounts/${id}`)
}

// 凭证
export const voucherApi = {
  list: (params) => api.get('/vouchers', { params }),
  page: (params) => api.get('/vouchers/page', { params }),
  getById: (id) => api.get(`/vouchers/${id}`),
  save: (data) => data.id ? api.put(`/vouchers/${data.id}`, data) : api.post('/vouchers', data),
  post: (id, reviewer) => api.post(`/vouchers/${id}/post`, { reviewer }),
  void: (id) => api.post(`/vouchers/${id}/void`),
  delete: (id) => api.delete(`/vouchers/${id}`)
}

// 账簿
export const ledgerApi = {
  detail: (accountCode, period) => api.get('/ledger/detail', { params: { accountCode, period } }),
  detailPage: (accountCode, period, page = 1, size = 10) => api.get('/ledger/detail/page', { params: { accountCode, period, page, size } }),
  balance: (period) => api.get('/ledger/balance', { params: { period } }),
  balancePage: (period, page = 1, size = 10) => api.get('/ledger/balance/page', { params: { period, page, size } })
}

// 认证
export const authApi = {
  /**
   * 用户登录
   * 密码会在前端使用 SHA-256 加密后传输
   */
  login: async (username, password) => {
    // 对密码进行 SHA-256 加密
    const hashedPassword = await hashPassword(password)
    return api.post('/auth/login', { username, password: hashedPassword })
  },
  logout: () => api.post('/auth/logout'),
  getCurrentUser: () => api.get('/auth/current')
}
