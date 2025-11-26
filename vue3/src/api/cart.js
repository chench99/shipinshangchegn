import request from '@/utils/request'

/**
 * 添加商品到购物车
 * 功能描述：将指定数量的商品添加到用户购物车中，如果商品已存在则累加数量
 * 入参：{ snackId: number, quantity: number }
 * 返回参数：{ id: number, snackId: number, snackName: string, snackImage: string, price: number, quantity: number, stock: number, subtotal: number, canPurchase: boolean, categoryName: string }
 * url地址：/cart/add
 * 请求方式：POST
 */
export function addToCart(params, config = {}) {
  return request.post('/cart/add', params, config)
}

/**
 * 获取用户购物车列表
 * 功能描述：获取当前登录用户的购物车商品列表，包含商品详情和价格统计
 * 入参：无（从JWT Token获取用户信息）
 * 返回参数：{ items: array, totalQuantity: number, totalAmount: number, validQuantity: number, validAmount: number, isEmpty: boolean }
 * url地址：/cart/list
 * 请求方式：GET
 */
export function getCartList(config = {}) {
  return request.get('/cart/list', null, config)
}

/**
 * 更新购物车商品数量
 * 功能描述：修改购物车中指定商品的数量
 * 入参：cartId: number, { quantity: number }
 * 返回参数：{ id: number, snackId: number, snackName: string, snackImage: string, price: number, quantity: number, stock: number, subtotal: number, canPurchase: boolean, categoryName: string }
 * url地址：/cart/update/{cartId}
 * 请求方式：PUT
 */
export function updateCartQuantity(cartId, params, config = {}) {
  return request.put(`/cart/update/${cartId}`, params, config)
}

/**
 * 删除购物车商品
 * 功能描述：从购物车中删除指定的商品
 * 入参：cartId: number
 * 返回参数：无
 * url地址：/cart/delete/{cartId}
 * 请求方式：DELETE
 */
export function deleteCartItem(cartId, config = {}) {
  return request.delete(`/cart/delete/${cartId}`, config)
}

/**
 * 批量删除购物车商品
 * 功能描述：从购物车中批量删除多个商品
 * 入参：{ cartIds: number[] }
 * 返回参数：无
 * url地址：/cart/batch-delete
 * 请求方式：DELETE
 */
export function batchDeleteCartItems(params, config = {}) {
  return request.deleteWithBody('/cart/batch-delete', params, config)
}

/**
 * 清空购物车
 * 功能描述：清空当前用户的所有购物车商品
 * 入参：无
 * 返回参数：无
 * url地址：/cart/clear
 * 请求方式：DELETE
 */
export function clearCart(config = {}) {
  return request.delete('/cart/clear', config)
}

/**
 * 获取购物车商品数量统计
 * 功能描述：获取当前用户购物车中的商品总数量，用于导航栏徽章显示
 * 入参：无
 * 返回参数：number（商品总数量）
 * url地址：/cart/count
 * 请求方式：GET
 */
export function getCartItemCount(config = {}) {
  return request.get('/cart/count', null, config)
}
