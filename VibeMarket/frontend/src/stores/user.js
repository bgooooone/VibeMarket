import { defineStore } from 'pinia'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 'admin'
  },

  actions: {
    async login(loginData) {
      const response = await userApi.login(loginData)
      this.token = response.token
      localStorage.setItem('token', this.token)
      await this.getUserInfo()
      return response
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

