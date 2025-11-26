/**
 * 图片处理工具函数
 */

/**
 * 创建安全的图片错误处理函数
 * @param {string} defaultImage - 默认图片路径
 * @param {string} fallbackImage - 最终降级图片（base64编码）
 * @returns {Function} 错误处理函数
 */
export function createImageErrorHandler(defaultImage = '/default-snack.jpg', fallbackImage = null) {
  return function(event) {
    const target = event.target
    
    // 避免无限循环，只设置一次默认图片
    if (target.src.indexOf(defaultImage) === -1) {
      target.src = defaultImage
    } else if (fallbackImage) {
      // 如果默认图片也加载失败，使用降级图片
      target.src = fallbackImage
    } else {
      // 使用通用的base64占位图片
      target.src = createPlaceholderImage(200, 200, '图片')
    }
  }
}

/**
 * 创建base64编码的占位图片
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @param {string} text - 显示文本
 * @param {string} bgColor - 背景颜色
 * @param {string} textColor - 文字颜色
 * @returns {string} base64编码的SVG图片
 */
export function createPlaceholderImage(width = 200, height = 200, text = '图片', bgColor = '#ddd', textColor = '#999') {
  const svg = `
    <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="${bgColor}"/>
      <text x="50%" y="50%" font-size="18" fill="${textColor}" text-anchor="middle" dy=".3em">${text}</text>
    </svg>
  `
  return `data:image/svg+xml;base64,${btoa(unescape(encodeURIComponent(svg)))}`
}

/**
 * 预定义的占位图片
 */
export const PLACEHOLDER_IMAGES = {
  // 商品图片占位符
  snack: createPlaceholderImage(200, 200, '零食图片'),
  // 缩略图占位符
  thumbnail: createPlaceholderImage(80, 80, '缩略图'),
  // 详情图片占位符
  detail: createPlaceholderImage(400, 300, '详情图片'),
  // 头像占位符
  avatar: createPlaceholderImage(100, 100, '头像')
}

/**
 * 预定义的错误处理函数
 */
export const IMAGE_ERROR_HANDLERS = {
  // 商品图片错误处理
  snack: createImageErrorHandler('/default-snack.jpg', PLACEHOLDER_IMAGES.snack),
  // 缩略图错误处理
  thumbnail: createImageErrorHandler('/default-snack.jpg', PLACEHOLDER_IMAGES.thumbnail),
  // 详情图片错误处理
  detail: createImageErrorHandler('/default-snack.jpg', PLACEHOLDER_IMAGES.detail),
  // 头像错误处理
  avatar: createImageErrorHandler('/default-avatar.jpg', PLACEHOLDER_IMAGES.avatar)
}

/**
 * 检查图片URL是否有效
 * @param {string} url - 图片URL
 * @returns {Promise<boolean>} 是否有效
 */
export function checkImageUrl(url) {
  return new Promise((resolve) => {
    if (!url) {
      resolve(false)
      return
    }
    
    const img = new Image()
    img.onload = () => resolve(true)
    img.onerror = () => resolve(false)
    img.src = url
  })
}

/**
 * 获取安全的图片URL，如果原图片无效则返回占位符
 * @param {string} url - 原图片URL
 * @param {string} placeholder - 占位符图片
 * @returns {Promise<string>} 安全的图片URL
 */
export async function getSafeImageUrl(url, placeholder = PLACEHOLDER_IMAGES.snack) {
  const isValid = await checkImageUrl(url)
  return isValid ? url : placeholder
}
