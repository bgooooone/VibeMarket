import request from './request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/v1/orders/create',
    method: 'post',
    data
  })
}

// 获取用户订单列表
export function getUserOrders(status) {
  return request({
    url: '/v1/orders/list',
    method: 'get',
    params: { status }
  })
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return request({
    url: `/v1/orders/${orderId}`,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: `/v1/orders/${orderId}/cancel`,
    method: 'put'
  })
}

// 支付订单
export function payOrder(orderId) {
  return request({
    url: `/v1/orders/${orderId}/pay`,
    method: 'put'
  })
}

// 管理员：获取所有订单
export function getAllOrders(status, page, pageSize) {
  return request({
    url: '/v1/admin/orders/list',
    method: 'get',
    params: { status, page, pageSize }
  })
}

// 管理员：获取订单详情
export function getAdminOrderDetail(orderId) {
  return request({
    url: `/v1/admin/orders/${orderId}`,
    method: 'get'
  })
}

// 管理员：发货
export function shipOrder(orderId) {
  return request({
    url: `/v1/admin/orders/${orderId}/ship`,
    method: 'put'
  })
}

// 管理员：更新订单状态
export function updateOrderStatus(orderId, status) {
  return request({
    url: `/v1/admin/orders/${orderId}/status`,
    method: 'put',
    params: { status }
  })
}

