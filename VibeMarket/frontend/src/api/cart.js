import request from './request'

// 获取购物车列表
export function getCartList() {
  return request({
    url: '/v1/cart/list',
    method: 'get'
  })
}

// 添加到购物车
export function addToCart(data) {
  return request({
    url: '/v1/cart/add',
    method: 'post',
    data
  })
}

// 更新购物车
export function updateCart(cartId, data) {
  return request({
    url: `/v1/cart/${cartId}`,
    method: 'put',
    data
  })
}

// 删除购物车项
export function removeFromCart(cartId) {
  return request({
    url: `/v1/cart/${cartId}`,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/v1/cart/clear',
    method: 'delete'
  })
}

// 选择/取消选择购物车项
export function selectCartItem(cartId, selected) {
  return request({
    url: `/v1/cart/${cartId}/select`,
    method: 'put',
    params: { selected }
  })
}

