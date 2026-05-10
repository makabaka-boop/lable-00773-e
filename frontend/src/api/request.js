import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { hashPassword } from '../utils/crypto'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
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

export default request

export const authApi = {
  login: async (username, password) => {
    const hashedPassword = await hashPassword(password)
    return request.post('/auth/login', { username, password: hashedPassword })
  },
  logout: () => request.post('/auth/logout'),
  getCurrentUser: () => request.get('/auth/current')
}
