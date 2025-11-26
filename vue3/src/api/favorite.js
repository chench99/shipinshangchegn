import request from '@/utils/request'

/**
 * 切换收藏状态（收藏/取消收藏）
 * 功能描述：切换指定商品的收藏状态，如果已收藏则取消收藏，如果未收藏则添加收藏
 * 入参：{ snackId: number }
 * 返回参数：{ snackId: number, isFavorited: boolean, favoriteCount: number }
 * url地址：/favorite/toggle
 * 请求方式：POST
 */
export function toggleFavorite(params, config = {}) {
  return request.post('/favorite/toggle', params, config)
}

/**
 * 检查商品收藏状态
 * 功能描述：检查指定商品是否已被当前用户收藏，同时返回该商品的总收藏数
 * 入参：snackId (路径参数)
 * 返回参数：{ snackId: number, isFavorited: boolean, favoriteCount: number }
 * url地址：/favorite/status/{snackId}
 * 请求方式：GET
 */
export function checkFavoriteStatus(snackId, config = {}) {
  return request.get(`/favorite/status/${snackId}`, null, config)
}

/**
 * 获取用户收藏列表
 * 功能描述：分页获取当前用户的收藏商品列表，包含商品详细信息
 * 入参：{ current: number, size: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/favorite/list
 * 请求方式：GET
 */
export function getFavoriteList(params, config = {}) {
  return request.get('/favorite/list', params, config)
}

/**
 * 获取用户收藏总数
 * 功能描述：获取当前用户的收藏商品总数量
 * 入参：无（从JWT Token获取用户信息）
 * 返回参数：number (收藏总数)
 * url地址：/favorite/count
 * 请求方式：GET
 */
export function getFavoriteCount(config = {}) {
  return request.get('/favorite/count', null, config)
}

/**
 * 批量检查商品收藏状态
 * 功能描述：批量检查多个商品的收藏状态，用于商品列表页面的收藏状态显示
 * 入参：[snackId1, snackId2, ...] (商品ID数组)
 * 返回参数：{ snackId1: boolean, snackId2: boolean, ... } (商品ID与收藏状态的映射)
 * url地址：/favorite/batch-status
 * 请求方式：POST
 */
export function batchCheckFavoriteStatus(snackIds, config = {}) {
  return request.post('/favorite/batch-status', snackIds, config)
}
