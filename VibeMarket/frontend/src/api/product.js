import request from './request'

export const productApi = {
  // 获取商品列表
  getProductList(params) {
    return request({
      url: '/v1/products',
      method: 'get',
      params
    })
  },

  // 获取商品详情
  getProductDetail(id) {
    return request({
      url: `/v1/products/${id}`,
      method: 'get'
    })
  },

  // 根据分类获取商品
  getProductsByCategory(categoryId) {
    return request({
      url: `/v1/products/category/${categoryId}`,
      method: 'get'
    })
  },

  // 搜索商品
  searchProducts(keyword) {
    return request({
      url: '/v1/products/search',
      method: 'get',
      params: { keyword }
    })
  }
}

// 获取商品详情（简化版）
export function getProductById(id) {
  return request({
    url: `/v1/products/${id}`,
    method: 'get'
  })
}

