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
  },

  // 仪表盘 - 概览
  getDashboardOverview() {
    return request({
      url: '/v1/admin/dashboard/overview',
      method: 'get'
    })
  },

  // 仪表盘 - 订单趋势
  getDashboardTrends(params) {
    return request({
      url: '/v1/admin/dashboard/trends',
      method: 'get',
      params
    })
  },

  // 仪表盘 - 分类分布
  getCategoryDistribution() {
    return request({
      url: '/v1/admin/dashboard/categories',
      method: 'get'
    })
  },

  // 用户管理
  getUsers(params) {
    return request({
      url: '/v1/admin/users',
      method: 'get',
      params
    })
  },

  // 更新用户状态
  updateUserStatus(id, status) {
    return request({
      url: `/v1/admin/users/${id}/status`,
      method: 'put',
      params: { status }
    })
  }
}

// 管理员商品管理
export function getAdminProducts() {
  return request({
    url: '/v1/admin/products/list',
    method: 'get'
  })
}

export function createProduct(data) {
  return request({
    url: '/v1/admin/products/create',
    method: 'post',
    data
  })
}

export function updateProduct(id, data) {
  return request({
    url: `/v1/admin/products/${id}`,
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/v1/admin/products/${id}`,
    method: 'delete'
  })
}

export function updateProductStatus(id, status) {
  return request({
    url: `/v1/admin/products/${id}/status`,
    method: 'put',
    params: { status }
  })
}

