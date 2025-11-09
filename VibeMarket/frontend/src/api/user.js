import request from './request'

export const userApi = {
  // 用户注册
  register(data) {
    return request({
      url: '/v1/auth/register',
      method: 'post',
      data
    })
  },

  // 用户登录
  login(data) {
    return request({
      url: '/v1/auth/login',
      method: 'post',
      data
    })
  },

  // 用户登出
  logout() {
    return request({
      url: '/v1/auth/logout',
      method: 'post'
    })
  },

  // 获取用户信息
  getProfile() {
    return request({
      url: '/v1/auth/profile',
      method: 'get'
    })
  }
}

