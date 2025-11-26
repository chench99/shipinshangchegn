import request from '@/utils/request'

/**
 * 用户登录
 * 功能描述：用户使用用户名和密码进行登录认证
 * 入参：{ username: string, password: string }
 * 返回参数：{ userInfo: object, token: string, roleCode: string, menuList?: array }
 * url地址：/user/login
 * 请求方式：POST
 */
export function login(params, config = {}) {
  return request.post('/user/login', params, config)
}

/**
 * 用户注册
 * 功能描述：新用户注册账号
 * 入参：{ username: string, password: string, confirmPassword: string, nickname?: string, phone?: string, userType?: string }
 * 返回参数：注册成功信息
 * url地址：/user/register
 * 请求方式：POST
 */
export function register(params, config = {}) {
  return request.post('/user/register', params, config)
}

/**
 * 获取当前登录用户信息
 * 功能描述：获取当前登录用户的详细信息
 * 入参：无
 * 返回参数：{ id: number, username: string, nickname: string, phone: string, avatar: string, userType: string, status: string }
 * url地址：/user/current
 * 请求方式：GET
 */
export function getCurrentUser(config = {}) {
  return request.get('/user/current', null, config)
}

/**
 * 根据ID获取用户信息
 * 功能描述：根据用户ID获取用户详细信息
 * 入参：{ id: number }
 * 返回参数：{ id: number, username: string, nickname: string, phone: string, avatar: string, userType: string, status: string }
 * url地址：/user/{id}
 * 请求方式：GET
 */
export function getUserById(id, config = {}) {
  return request.get(`/user/${id}`, null, config)
}

/**
 * 更新用户信息
 * 功能描述：更新用户的基本信息
 * 入参：{ id: number, nickname?: string, phone?: string, avatar?: string, userType?: string, status?: string }
 * 返回参数：更新成功信息
 * url地址：/user/{id}
 * 请求方式：PUT
 */
export function updateUser(id, params, config = {}) {
  return request.put(`/user/${id}`, params, config)
}

/**
 * 修改用户密码
 * 功能描述：用户修改登录密码
 * 入参：{ oldPassword: string, newPassword: string }
 * 返回参数：修改成功信息
 * url地址：/user/password/{id}
 * 请求方式：PUT
 */
export function updatePassword(id, params, config = {}) {
  return request.put(`/user/password/${id}`, params, config)
}

/**
 * 用户退出登录
 * 功能描述：用户退出登录，清除登录状态
 * 入参：无
 * 返回参数：退出成功信息
 * url地址：/user/logout
 * 请求方式：POST
 */
export function logout(config = {}) {
  return request.post('/user/logout', null, config)
}

/**
 * 分页查询用户列表（管理员功能）
 * 功能描述：管理员查询用户列表，支持条件筛选和分页
 * 入参：{ username?: string, nickname?: string, userType?: string, status?: string, currentPage?: number, size?: number }
 * 返回参数：{ records: array, total: number, size: number, current: number }
 * url地址：/user/page
 * 请求方式：GET
 */
export function getUserPage(params, config = {}) {
  return request.get('/user/page', params, config)
}