import request from '@/utils/request'

/**
 * 获取仪表板统计数据
 * @param {object} callbacks - 回调函数对象
 * @returns {Promise} 请求Promise
 */
export function getDashboardStats(callbacks = {}) {
  return request.get('/dashboard/stats', null, callbacks)
}

