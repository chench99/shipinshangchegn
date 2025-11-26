import request from '@/utils/request'

/**
 * 创建零食
 * 功能描述：创建新的零食商品
 * 入参：{ categoryId: number, name: string, description?: string, price: number, stock: number, coverImage?: string, detailImages?: array, status?: string }
 * 返回参数：{ id: number, categoryId: number, categoryName: string, name: string, description: string, price: number, stock: number, coverImage: string, detailImages: array, status: string, statusDisplayName: string, salesCount: number, createTime: string, updateTime: string, onSale: boolean, hasStock: boolean, canPurchase: boolean }
 * url地址：/snack
 * 请求方式：POST
 */
export function createSnack(params, config = {}) {
  return request.post('/snack', params, config)
}

/**
 * 更新零食
 * 功能描述：更新指定ID的零食信息
 * 入参：{ id: number, categoryId: number, name: string, description?: string, price: number, stock: number, coverImage?: string, detailImages?: array, status: string }
 * 返回参数：{ id: number, categoryId: number, categoryName: string, name: string, description: string, price: number, stock: number, coverImage: string, detailImages: array, status: string, statusDisplayName: string, salesCount: number, createTime: string, updateTime: string, onSale: boolean, hasStock: boolean, canPurchase: boolean }
 * url地址：/snack/{id}
 * 请求方式：PUT
 */
export function updateSnack(id, params, config = {}) {
  return request.put(`/snack/${id}`, params, config)
}

/**
 * 删除零食
 * 功能描述：删除指定ID的零食
 * 入参：{ id: number }
 * 返回参数：无
 * url地址：/snack/{id}
 * 请求方式：DELETE
 */
export function deleteSnack(id, config = {}) {
  return request.delete(`/snack/${id}`, config)
}

/**
 * 根据ID获取零食详情
 * 功能描述：根据零食ID获取零食详细信息
 * 入参：{ id: number }
 * 返回参数：{ id: number, categoryId: number, categoryName: string, name: string, description: string, price: number, stock: number, coverImage: string, detailImages: array, status: string, statusDisplayName: string, salesCount: number, createTime: string, updateTime: string, onSale: boolean, hasStock: boolean, canPurchase: boolean }
 * url地址：/snack/{id}
 * 请求方式：GET
 */
export function getSnackById(id, config = {}) {
  return request.get(`/snack/${id}`, null, config)
}

/**
 * 后台分页查询零食列表
 * 功能描述：后台管理分页获取零食列表，支持按名称、分类、状态、价格范围筛选
 * 入参：{ current: number, size: number, name?: string, categoryId?: number, status?: string, minPrice?: number, maxPrice?: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/snack/page
 * 请求方式：GET
 */
export function getSnackPage(params, config = {}) {
  return request.get('/snack/page', params, config)
}

/**
 * 前台分页查询零食列表
 * 功能描述：前台用户分页获取零食列表，只显示上架商品，支持按名称、分类筛选和排序
 * 入参：{ current: number, size: number, name?: string, categoryId?: number, sortBy?: string, sortOrder?: string }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/snack/frontend/page
 * 请求方式：GET
 */
export function getFrontendSnackPage(params, config = {}) {
  return request.get('/snack/frontend/page', params, config)
}

/**
 * 获取推荐零食列表
 * 功能描述：获取推荐零食列表，用于首页展示，按销量和创建时间排序
 * 入参：{ limit?: number }
 * 返回参数：[{ id: number, categoryId: number, categoryName: string, name: string, description: string, price: number, stock: number, coverImage: string, status: string, statusDisplayName: string, salesCount: number, createTime: string, updateTime: string, onSale: boolean, hasStock: boolean, canPurchase: boolean }]
 * url地址：/snack/recommended
 * 请求方式：GET
 */
export function getRecommendedSnacks(params, config = {}) {
  return request.get('/snack/recommended', params, config)
}

/**
 * 更新零食状态
 * 功能描述：更新指定零食的状态（上架/下架）
 * 入参：{ id: number, status: string }
 * 返回参数：无
 * url地址：/snack/{id}/status
 * 请求方式：PUT
 */
export function updateSnackStatus(id, status, config = {}) {
  return request.put(`/snack/${id}/status?status=${status}`, null, config)
}

/**
 * 根据分类ID获取零食列表
 * 功能描述：根据分类ID获取该分类下的零食列表
 * 入参：{ categoryId: number, limit?: number }
 * 返回参数：[{ id: number, categoryId: number, categoryName: string, name: string, description: string, price: number, stock: number, coverImage: string, status: string, statusDisplayName: string, salesCount: number, createTime: string, updateTime: string, onSale: boolean, hasStock: boolean, canPurchase: boolean }]
 * url地址：/snack/category/{categoryId}
 * 请求方式：GET
 */
export function getSnacksByCategory(categoryId, params, config = {}) {
  return request.get(`/snack/category/${categoryId}`, params, config)
}

/**
 * 搜索零食
 * 功能描述：前台搜索零食功能，支持关键词搜索、分类筛选和排序
 * 入参：{ current: number, size: number, keyword: string, categoryId?: number, sortBy?: string, sortOrder?: string }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/snack/search
 * 请求方式：GET
 */
export function searchSnacks(params, config = {}) {
  return request.get('/snack/search', params, config)
}

/**
 * 批量更新零食状态
 * 功能描述：批量更新多个零食的状态
 * 入参：{ ids: array, status: string }
 * 返回参数：无
 * url地址：/snack/batch/status
 * 请求方式：PUT
 */
export function batchUpdateSnackStatus(params, config = {}) {
  return request.put('/snack/batch/status', params, config)
}

/**
 * 获取零食统计信息
 * 功能描述：获取零食的统计信息，如总数、上架数、下架数等
 * 入参：无
 * 返回参数：{ total: number, onSale: number, offShelf: number, lowStock: number }
 * url地址：/snack/statistics
 * 请求方式：GET
 */
export function getSnackStatistics(config = {}) {
  return request.get('/snack/statistics', null, config)
}
