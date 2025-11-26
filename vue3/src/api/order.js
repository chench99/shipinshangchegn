import request from "@/utils/request";

/**
 * 创建订单
 * @param {object} params - 订单创建参数
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function createOrder(params, callbacks = {}) {
  return request.post('/order/create', params, callbacks);
}

/**
 * 订单支付
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function payOrder(orderId, callbacks = {}) {
  return request.put(`/order/${orderId}/pay`, {}, callbacks);
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function cancelOrder(orderId, callbacks = {}) {
  return request.put(`/order/${orderId}/cancel`, {}, callbacks);
}

/**
 * 确认收货
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function completeOrder(orderId, callbacks = {}) {
  return request.put(`/order/${orderId}/complete`, {}, callbacks);
}

/**
 * 分页获取用户订单列表
 * @param {object} params - 查询参数
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getUserOrderPage(params, callbacks = {}) {
  return request.get('/order/page', params, callbacks);
}

/**
 * 获取订单详情
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getOrderDetail(orderId, callbacks = {}) {
  return request.get(`/order/${orderId}`, null, callbacks);
}

/**
 * 管理员分页获取所有订单列表
 * @param {object} params - 查询参数
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getAdminOrderPage(params, callbacks = {}) {
  return request.get('/order/admin/page', params, callbacks);
}

/**
 * 管理员订单发货
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function shipOrder(orderId, callbacks = {}) {
  return request.put(`/order/admin/${orderId}/ship`, {}, callbacks);
}

/**
 * 管理员获取订单详情
 * @param {number} orderId - 订单ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getAdminOrderDetail(orderId, callbacks = {}) {
  return request.get(`/order/admin/${orderId}`, null, callbacks);
}
