import { defineStore } from 'pinia'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => {
      // 检查token是否存在且有效
      if (!state.token) {
        return false
      }
      // 可以在这里添加token过期检查（如果需要）
      // 目前依赖后端验证，前端只检查token是否存在
      return true
    },
    isAdmin: (state) => state.userInfo?.role === 'admin'
  },

  actions: {
    async login(loginData) {
      // 如果管理员已登录，先退出管理员登录
      const { useAdminStore } = await import('@/stores/admin')
      const adminStore = useAdminStore()
      if (adminStore.isLoggedIn) {
        await adminStore.logout()
      }
      
      const response = await userApi.login(loginData)
      this.token = response.token
      localStorage.setItem('token', this.token)
      await this.getUserInfo()
      return response
    },
    
    // 验证token是否有效
    async validateToken() {
      if (!this.token) {
        return false
      }
      try {
        await this.getUserInfo()
        return true
      } catch (error) {
        // token无效，清除登录状态
        this.logout()
        return false
      }
    },

    async register(registerData) {
      const response = await userApi.register(registerData)
      return response
    },

    async getUserInfo() {
      try {
        this.userInfo = await userApi.getProfile()
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      } catch (error) {
        this.logout()
      }
    },

    async logout() {
      try {
        await userApi.logout()
      } catch (error) {
        console.error('登出失败', error)
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    }
  }
})

