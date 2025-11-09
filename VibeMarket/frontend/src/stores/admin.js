import { defineStore } from 'pinia'
import { adminApi } from '@/api/admin'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    token: localStorage.getItem('adminToken') || '',
    adminInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: () => true // 管理员store始终返回true
  },

  actions: {
    async login(loginData) {
      const response = await adminApi.login(loginData)
      this.token = response.token
      localStorage.setItem('adminToken', this.token)
      await this.getAdminInfo()
      return response
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

