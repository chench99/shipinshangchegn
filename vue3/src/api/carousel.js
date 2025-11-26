import request from '@/utils/request'

/**
 * @description 创建轮播图
 * @param {object} params - { title, jumpType, jumpTarget, sortOrder, status, startTime, endTime }
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function createCarousel(params, callbacks = {}) {
  return request.post('/carousel', params, callbacks)
}

/**
 * @description 更新轮播图
 * @param {number} id - 轮播ID
 * @param {object} params - 同创建参数
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function updateCarousel(id, params, callbacks = {}) {
  return request.put(`/carousel/${id}`, params, callbacks)
}

/**
 * @description 删除轮播图
 * @param {number} id - 轮播ID
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function deleteCarousel(id, callbacks = {}) {
  return request.delete(`/carousel/${id}`, callbacks)
}

/**
 * @description 获取轮播图详情
 * @param {number} id - 轮播ID
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function getCarouselDetail(id, callbacks = {}) {
  return request.get(`/carousel/${id}`, null, callbacks)
}

/**
 * @description 分页查询轮播图
 * @param {object} params - { current, size, status, title }
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function getCarouselPage(params, callbacks = {}) {
  return request.get('/carousel/page', params, callbacks)
}

/**
 * @description 获取启用中的轮播图列表（前台首页展示）
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function getEnabledCarousels(callbacks = {}) {
  return request.get('/carousel/enabled', null, callbacks)
}


