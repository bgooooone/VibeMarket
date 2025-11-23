import { defineStore } from 'pinia'
import { adminApi } from '@/api/admin'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    token: localStorage.getItem('adminToken') || '',
    adminInfo: null
  }),

  getters: {
    isLoggedIn: (state) => {
      // 检查token是否存在且有效
      if (!state.token) {
        return false
      }
      return true
    },
    isAdmin: () => true // 管理员store始终返回true
  },

  actions: {
    async login(loginData) {
      // 如果用户已登录，先退出用户登录
      const { useUserStore } = await import('@/stores/user')
      const userStore = useUserStore()
      if (userStore.isLoggedIn) {
        await userStore.logout()
      }
      
      const response = await adminApi.login(loginData)
      this.token = response.token
      localStorage.setItem('adminToken', this.token)
      await this.getAdminInfo()
      return response
    },
    
    // 验证token是否有效
    async validateToken() {
      if (!this.token) {
        return false
      }
      try {
        await this.getAdminInfo()
        return true
      } catch (error) {
        // token无效，清除登录状态
        this.logout()
        return false
      }
    },

    async getAdminInfo() {
      try {
        this.adminInfo = await adminApi.getProfile()
      } catch (error) {
        this.logout()
      }
    },

    async logout() {
      try {
        await adminApi.logout()
      } catch (error) {
        console.error('登出失败', error)
      } finally {
        this.token = ''
        this.adminInfo = null
        localStorage.removeItem('adminToken')
      }
    }
  }
})

