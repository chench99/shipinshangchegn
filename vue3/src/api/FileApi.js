import request from '@/utils/request'

/**
 * @description 临时业务文件上传（策略A第一阶段）
 * @param {File} file - 上传的文件对象
 * @param {string} businessType - 业务类型
 * @param {string} [businessField] - 业务字段名（可选）
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回临时文件信息
 */
export function uploadTempBusinessFile(file, businessType, businessField, callbacks = {}) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessType)
  if (businessField) {
    formData.append('businessField', businessField)
  }
  
  return request.post('/file/upload/temp-business', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * @description 确认临时文件并关联业务（策略A第二阶段）
 * @param {number} tempFileId - 临时文件ID
 * @param {object} businessInfo - 业务关联信息
 * @param {string} businessInfo.businessType - 业务类型
 * @param {string|number} businessInfo.businessId - 业务实体ID
 * @param {string} businessInfo.businessField - 业务字段
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回确认结果
 */
export function confirmTempFile(tempFileId, businessInfo, callbacks = {}) {
  return request.put(`/file/confirm/${tempFileId}`, businessInfo, callbacks)
}

/**
 * @description 直接上传业务文件（策略B和策略C）
 * @param {File} file - 上传的文件对象
 * @param {object} businessInfo - 业务信息
 * @param {string} businessInfo.businessType - 业务类型
 * @param {string|number} businessInfo.businessId - 业务实体ID
 * @param {string} businessInfo.businessField - 业务字段
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回文件信息
 */
export function uploadBusinessFile(file, businessInfo, callbacks = {}) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessInfo.businessType)
  formData.append('businessId', businessInfo.businessId)
  formData.append('businessField', businessInfo.businessField)
  
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * @description 根据业务实体查询文件列表
 * @param {string} businessType - 业务类型
 * @param {string|number} businessId - 业务实体ID
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回文件列表
 */
export function getFilesByBusiness(businessType, businessId, callbacks = {}) {
  return request.get(`/file/business/${businessType}/${businessId}`, null, callbacks)
}

/**
 * @description 根据业务字段查询文件
 * @param {string} businessType - 业务类型
 * @param {string|number} businessId - 业务实体ID
 * @param {string} businessField - 业务字段
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回文件信息
 */
export function getFileByBusinessField(businessType, businessId, businessField, callbacks = {}) {
  return request.get(`/file/business/${businessType}/${businessId}/${businessField}`, null, callbacks)
}

/**
 * @description 删除文件
 * @param {number} fileId - 文件ID
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回删除结果
 */
export function deleteFile(fileId, callbacks = {}) {
  return request.delete(`/file/delete/${fileId}`, callbacks)
}

/**
 * @description 批量删除业务关联文件
 * @param {string} businessType - 业务类型
 * @param {string|number} businessId - 业务实体ID
 * @param {object} [callbacks={}] - 回调函数对象
 * @returns {Promise} 返回删除结果
 */
export function deleteFilesByBusiness(businessType, businessId, callbacks = {}) {
  return request.delete(`/file/business/${businessType}/${businessId}`, callbacks)
}
