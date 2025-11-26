import request from '@/utils/request'

/**
 * 创建分类
 * 功能描述：创建新的零食分类
 * 入参：{ name: string, sortOrder?: number, status?: string }
 * 返回参数：{ id: number, name: string, sortOrder: number, status: string, statusDisplayName: string, createTime: string, updateTime: string }
 * url地址：/category
 * 请求方式：POST
 */
export function createCategory(params, config = {}) {
  return request.post('/category', params, config)
}

/**
 * 更新分类
 * 功能描述：更新指定ID的分类信息
 * 入参：{ id: number, name: string, sortOrder?: number, status?: string }
 * 返回参数：{ id: number, name: string, sortOrder: number, status: string, statusDisplayName: string, createTime: string, updateTime: string }
 * url地址：/category/{id}
 * 请求方式：PUT
 */
export function updateCategory(id, params, config = {}) {
  return request.put(`/category/${id}`, params, config)
}

/**
 * 删除分类
 * 功能描述：删除指定ID的分类
 * 入参：{ id: number }
 * 返回参数：无
 * url地址：/category/{id}
 * 请求方式：DELETE
 */
export function deleteCategory(id, config = {}) {
  return request.delete(`/category/${id}`, config)
}

/**
 * 根据ID获取分类详情
 * 功能描述：根据分类ID获取分类详细信息
 * 入参：{ id: number }
 * 返回参数：{ id: number, name: string, sortOrder: number, status: string, statusDisplayName: string, createTime: string, updateTime: string }
 * url地址：/category/{id}
 * 请求方式：GET
 */
export function getCategoryById(id, config = {}) {
  return request.get(`/category/${id}`, null, config)
}

/**
 * 分页查询分类列表
 * 功能描述：分页获取分类列表，支持按名称和状态筛选
 * 入参：{ current: number, size: number, name?: string, status?: string }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/category/page
 * 请求方式：GET
 */
export function getCategoryPage(params, config = {}) {
  return request.get('/category/page', params, config)
}

/**
 * 获取所有启用状态的分类列表
 * 功能描述：获取所有状态为启用的分类列表，按排序值升序排列
 * 入参：无
 * 返回参数：[{ id: number, name: string, sortOrder: number, status: string, statusDisplayName: string, createTime: string, updateTime: string }]
 * url地址：/category/active
 * 请求方式：GET
 */
export function getActiveCategoryList(config = {}) {
  return request.get('/category/active', null, config)
}

/**
 * 更新分类状态
 * 功能描述：更新指定分类的状态（启用/禁用）
 * 入参：{ id: number, status: string }
 * 返回参数：无
 * url地址：/category/{id}/status
 * 请求方式：PUT
 */
export function updateCategoryStatus(id, status, config = {}) {
  return request.put(`/category/${id}/status?status=${status}`, null, config)
}
