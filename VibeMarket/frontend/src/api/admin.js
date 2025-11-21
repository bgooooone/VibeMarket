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

