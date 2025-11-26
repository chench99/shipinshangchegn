import request from '@/utils/request'

/**
 * @description 创建评价
 * @param {object} params - 参数对象 { snackId: number, rating: number, content: string }
 * @param {object} [callbacks={}] - 回调对象 { onSuccess, onError, successMsg }
 * @returns {Promise}
 */
export function createReview(params, callbacks = {}) {
  return request.post('/review/create', params, callbacks)
}

/**
 * @description 分页查询评价
 * @param {object} params - 查询参数 { current, size, snackId? }
 * @param {object} [callbacks={}] - 回调对象 { onSuccess, onError }
 * @returns {Promise}
 */
export function getReviewPage(params, callbacks = {}) {
  return request.get('/review/page', params, callbacks)
}

/**
 * @description 删除评价
 * @param {number} id - 评价ID
 * @param {object} [callbacks={}] - 回调对象 { onSuccess, onError, successMsg }
 * @returns {Promise}
 */
export function deleteReview(id, callbacks = {}) {
  return request.delete(`/review/${id}`, {
    successMsg: '删除成功',
    ...callbacks
  })
}

/**
 * @description 判断当前用户是否可评价该零食
 * @param {number} snackId - 零食ID
 * @param {object} [callbacks={}]
 */
export function canReview(snackId, callbacks = {}) {
  return request.get('/review/can-review', { snackId }, callbacks)
}

/**
 * @description 确认评价图片（策略A第二阶段）
 * @param {number} reviewId - 评价ID
 * @param {number} tempFileId - 临时文件ID
 * @param {object} [callbacks={}] - 回调对象
 * @returns {Promise}
 */
export function confirmReviewImage(reviewId, tempFileId, callbacks = {}) {
  return request.put(`/review/${reviewId}/confirm-image/${tempFileId}`, {}, callbacks)
}


