/**
 * Mock数据管理器
 * 功能描述：统一管理所有API接口的Mock数据实现
 */

// Mock数据存储
const mockData = new Map()

/**
 * 创建成功响应
 * @param {*} data 响应数据
 * @param {string} msg 响应消息
 * @returns {Object} 标准响应格式
 */
export function createSuccessResponse(data = null, msg = '操作成功') {
  return {
    code: '200',
    data,
    msg
  }
}

/**
 * 创建错误响应
 * @param {string} msg 错误消息
 * @param {string} code 错误码
 * @returns {Object} 标准错误响应格式
 */
export function createErrorResponse(msg = '操作失败', code = '500') {
  return {
    code,
    data: null,
    msg
  }
}

/**
 * 注册Mock API
 * @param {string} method HTTP方法
 * @param {string} url API路径
 * @param {Function} handler 处理函数
 */
export function registerMockApi(method, url, handler) {
  const key = `${method.toUpperCase()}_${url}`
  mockData.set(key, handler)
  console.log('🎀 注册Mock API:', key)
}

/**
 * 获取Mock处理函数
 * @param {string} method HTTP方法
 * @param {string} url API路径
 * @returns {Function|null} 处理函数
 */
export function getMockHandler(method, url) {
  const key = `${method.toUpperCase()}_${url}`
  const handler = mockData.get(key)
  console.log('🔍 查找Mock处理器:', key, handler ? '✅找到' : '❌未找到')
  console.log('📋 已注册的Mock API:', Array.from(mockData.keys()))
  return handler || null
}

// ==================== 用户认证相关Mock数据 ====================

// Mock用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    name: '管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    sex: '男',
    avatar: '/img/avatar/admin.jpg',
    roleCode: 'ADMIN',
    token: 'mock_admin_token_123456',
    menuList: [
      { id: 1, name: '用户管理', path: '/back/user', icon: 'user' },
      { id: 2, name: '系统设置', path: '/back/system', icon: 'setting' }
    ]
  },
  {
    id: 2,
    username: 'user001',
    name: '普通用户',
    email: 'user001@example.com',
    phone: '13800138001',
    sex: '女',
    avatar: '/img/avatar/user001.jpg',
    roleCode: 'USER',
    token: 'mock_user_token_123456'
  }
]

// Mock测试账号说明
console.log('🎭 Mock测试账号:')
console.log('管理员: admin / 123456')
console.log('普通用户: user / 123456')

// 注册用户登录Mock
registerMockApi('POST', '/user/login', (params) => {
  const { username, password } = params
  
  if (!username || !password) {
    return createErrorResponse('用户名和密码不能为空', '400')
  }
  
  const user = mockUsers.find(u => u.username === username)
  if (!user) {
    return createErrorResponse('用户名不存在。测试账号: admin/123456 或 user/123456', '400')
  }

  if (password !== '123456') { // Mock密码验证
    return createErrorResponse('密码错误。测试密码: 123456', '400')
  }
  
  return createSuccessResponse(user, '登录成功')
})

// 注册用户注册Mock
registerMockApi('POST', '/user/add', (params) => {
  const { username, password, email, phone, name, roleCode = 'USER' } = params
  
  if (!username || !password || !email) {
    return createErrorResponse('用户名、密码和邮箱不能为空', '400')
  }
  
  // 检查用户名是否已存在
  const existingUser = mockUsers.find(u => u.username === username)
  if (existingUser) {
    return createErrorResponse('用户名已存在', '400')
  }
  
  // 检查邮箱是否已存在
  const existingEmail = mockUsers.find(u => u.email === email)
  if (existingEmail) {
    return createErrorResponse('邮箱已被注册', '400')
  }
  
  // 创建新用户
  const newUser = {
    id: mockUsers.length + 1,
    username,
    name: name || username,
    email,
    phone: phone || '',
    sex: '男',
    avatar: '/img/avatar/default.jpg',
    roleCode,
    token: `mock_token_${Date.now()}`
  }
  
  mockUsers.push(newUser)
  
  return createSuccessResponse(newUser, '注册成功')
})

// 注册获取当前用户信息Mock
registerMockApi('GET', '/user/current', () => {
  // 模拟从token获取用户信息
  const currentUser = mockUsers[0] // 默认返回第一个用户
  return createSuccessResponse(currentUser, '获取用户信息成功')
})

// 注册根据ID获取用户信息Mock
registerMockApi('GET', '/user/:id', (params, pathParams) => {
  const userId = parseInt(pathParams.id)
  const user = mockUsers.find(u => u.id === userId)
  
  if (!user) {
    return createErrorResponse('用户不存在', '404')
  }
  
  return createSuccessResponse(user, '获取用户信息成功')
})

// 注册更新用户信息Mock
registerMockApi('PUT', '/user/:id', (params, pathParams) => {
  const userId = parseInt(pathParams.id)
  const userIndex = mockUsers.findIndex(u => u.id === userId)
  
  if (userIndex === -1) {
    return createErrorResponse('用户不存在', '404')
  }
  
  // 更新用户信息
  mockUsers[userIndex] = { ...mockUsers[userIndex], ...params }
  
  return createSuccessResponse(mockUsers[userIndex], '用户信息更新成功')
})

// 注册修改密码Mock
registerMockApi('PUT', '/user/password/:id', (params, pathParams) => {
  const { oldPassword, newPassword } = params
  const userId = parseInt(pathParams.id)
  
  if (!oldPassword || !newPassword) {
    return createErrorResponse('旧密码和新密码不能为空', '400')
  }
  
  const user = mockUsers.find(u => u.id === userId)
  if (!user) {
    return createErrorResponse('用户不存在', '404')
  }
  
  // Mock密码验证（实际项目中应该验证旧密码）
  if (oldPassword !== '123456') {
    return createErrorResponse('旧密码错误', '400')
  }
  
  return createSuccessResponse(null, '密码修改成功')
})

// 注册忘记密码Mock
registerMockApi('GET', '/user/forget', (params) => {
  const { email, newPassword } = params
  if (!email || !newPassword) {
    return createErrorResponse('邮箱和新密码不能为空', '400')
  }
  
  const user = mockUsers.find(u => u.email === email)
  if (!user) {
    return createErrorResponse('邮箱不存在', '404')
  }
  
  return createSuccessResponse(null, '密码重置成功')
})

// 注册退出登录Mock
registerMockApi('POST', '/user/logout', () => {
  return createSuccessResponse(null, '退出登录成功')
})

// 导出Mock管理器
export default {
  registerMockApi,
  getMockHandler,
  createSuccessResponse,
  createErrorResponse
}
