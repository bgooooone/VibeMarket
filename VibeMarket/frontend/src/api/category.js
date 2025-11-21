import request from './request'

export const categoryApi = {
  // 获取所有分类
  getAllCategories() {
    return request({
      url: '/v1/categories',
      method: 'get'
    })
  },

  // 根据父分类获取子分类
  getCategoriesByParent(parentId) {
    return request({
      url: `/v1/categories/parent/${parentId}`,
      method: 'get'
    })
  }
}

