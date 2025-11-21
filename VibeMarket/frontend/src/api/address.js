import request from './request'

// 获取地址列表
export function getAddressList() {
  return request({
    url: '/v1/addresses/list',
    method: 'get'
  })
}

// 获取地址详情
export function getAddress(id) {
  return request({
    url: `/v1/addresses/${id}`,
    method: 'get'
  })
}

// 添加地址
export function addAddress(data) {
  return request({
    url: '/v1/addresses/add',
    method: 'post',
    data
  })
}

// 更新地址
export function updateAddress(id, data) {
  return request({
    url: `/v1/addresses/${id}`,
    method: 'put',
    data
  })
}

// 删除地址
export function deleteAddress(id) {
  return request({
    url: `/v1/addresses/${id}`,
    method: 'delete'
  })
}

// 设置默认地址
export function setDefaultAddress(id) {
  return request({
    url: `/v1/addresses/${id}/default`,
    method: 'put'
  })
}

