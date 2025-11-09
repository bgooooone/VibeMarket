import request from './request'

export const adminApi = {
  // 管理员登录
  login(data) {
    return request({
      url: '/v1/admin/auth/login',
      method: 'post',
      data
    })
  },

  // 管理员登出
  logout() {
    return request({
      url: '/v1/admin/auth/logout',
      method: 'post'
    })
  },

  // 获取管理员信息
  getProfile() {
    return request({
      url: '/v1/admin/auth/profile',
      method: 'get'
    })
  }
}

