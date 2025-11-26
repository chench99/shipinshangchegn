import request from "@/utils/request";

/**
 * 创建收货地址
 * @param {object} params - 地址信息
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function createAddress(params, callbacks = {}) {
  return request.post('/address', params, callbacks);
}

/**
 * 更新收货地址
 * @param {number} addressId - 地址ID
 * @param {object} params - 地址信息
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function updateAddress(addressId, params, callbacks = {}) {
  return request.put(`/address/${addressId}`, params, callbacks);
}

/**
 * 删除收货地址
 * @param {number} addressId - 地址ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function deleteAddress(addressId, callbacks = {}) {
  return request.delete(`/address/${addressId}`, callbacks);
}

/**
 * 设置默认地址
 * @param {number} addressId - 地址ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function setDefaultAddress(addressId, callbacks = {}) {
  return request.put(`/address/${addressId}/default`, {}, callbacks);
}

/**
 * 获取用户地址列表
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getUserAddressList(callbacks = {}) {
  return request.get('/address', null, callbacks);
}

/**
 * 根据ID获取地址详情
 * @param {number} addressId - 地址ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getAddressById(addressId, callbacks = {}) {
  return request.get(`/address/${addressId}`, null, callbacks);
}

/**
 * 获取用户默认地址
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getDefaultAddress(callbacks = {}) {
  return request.get('/address/default', null, callbacks);
}
