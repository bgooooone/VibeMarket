import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 如果是FormData，不设置Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      // 移除Content-Type，让浏览器自动设置（包括boundary）
      delete config.headers['Content-Type']
    }
    
    // 优先使用管理员token（如果是管理员接口）
    const adminStore = useAdminStore()
    if (adminStore.token && (config.url?.includes('/admin/') || config.url?.includes('/upload/'))) {
      config.headers.Authorization = `Bearer ${adminStore.token}`
    } else {
      // 否则使用用户token
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = `Bearer ${userStore.token}`
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request

