import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import BackendLayout from '@/layouts/BackendLayout.vue'

// 后台路由
export const backendRoutes = [
  {
    path: '/back',
    component: BackendLayout,
    redirect: '/back/dashboard',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/backend/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/backend/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'category',
        name: 'CategoryManagement',
        component: () => import('@/views/backend/category/index.vue'),
        meta: { title: '分类管理', icon: 'Collection', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'snack',
        name: 'SnackManagement',
        component: () => import('@/views/backend/snack/index.vue'),
        meta: { title: '零食管理', icon: 'Food', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'order',
        name: 'OrderManagement',
        component: () => import('@/views/backend/order/index.vue'),
        meta: { title: '订单管理', icon: 'ShoppingBag', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'review',
        name: 'ReviewManagement',
        component: () => import('@/views/backend/review/index.vue'),
        meta: { title: '评价管理', icon: 'ChatLineSquare', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'carousel',
        name: 'CarouselManagement',
        component: () => import('@/views/backend/carousel/index.vue'),
        meta: { title: '轮播管理', icon: 'PictureFilled', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'profile',
        name: 'BackendProfile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息', icon: 'UserFilled', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

// 前台路由配置
const frontendRoutes = [
  {
    path: '/',
    component: () => import('@/layouts/FrontendLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/frontend/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'snacks',
        name: 'SnackList',
        component: () => import('@/views/frontend/SnackList.vue'),
        meta: { title: '零食列表' }
      },
      {
        path: 'snack/:id',
        name: 'SnackDetail',
        component: () => import('@/views/frontend/SnackDetail.vue'),
        meta: { title: '零食详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/frontend/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/frontend/OrderConfirm.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/frontend/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/frontend/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'profile/address',
        name: 'AddressManagement',
        component: () => import('@/views/profile/address.vue'),
        meta: { title: '收货地址', requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'FavoriteList',
        component: () => import('@/views/frontend/FavoriteList.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      }
    ] 
  },
  // 认证相关路由使用专门的认证布局
  {
    path: '/auth',
    component: () => import('@/layouts/AuthLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: () => import('@/views/auth/ForgotPassword.vue'),
        meta: { title: '找回密码' }
      }
    ]
  },
  // 兼容旧路由
  {
    path: '/login',
    redirect: '/auth/login'
  },
  {
    path: '/register',
    redirect: '/auth/register'
  },
  {
    path: '/mock-test',
    name: 'MockTest',
    component: () => import('@/views/MockTest.vue'),
    meta: { title: 'Mock测试' }
  }
]

// 错误页面路由
const errorRoutes = [
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 路由配置
const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...frontendRoutes,
    ...backendRoutes,
    ...errorRoutes
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 预制菜商城`
  }

  const userStore = useUserStore()
  
  // 开发环境下输出调试信息
  if (import.meta.env.DEV) {
    console.log("Current route:", to.path)
    console.log("User status:", {
      isLoggedIn: userStore.isLoggedIn,
      isUser: userStore.isUser,
      isAdmin: userStore.isAdmin
    })
  }

  // 1. 检查是否需要登录权限
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  if (requiresAuth && !userStore.isLoggedIn) {
    next({
      path: '/auth/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 2. 检查是否需要管理员权限
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  if (requiresAdmin && !userStore.isAdmin) {
    // 已登录但不是管理员，跳转到前台首页
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      // 未登录，跳转到登录页
      next({
        path: '/auth/login',
        query: { redirect: to.fullPath }
      })
    }
    return
  }

  // 3. 已登录用户访问登录/注册页面，重定向到对应首页
  if (userStore.isLoggedIn && (to.path === '/auth/login' || to.path === '/auth/register' || to.path === '/login' || to.path === '/register')) {
    next(userStore.isAdmin ? '/back/dashboard' : '/')
    return
  }

  // 4. 角色路由隔离（可选，根据需求启用）
  // 管理员访问前台页面：允许
  // 普通用户访问后台页面：已在上面的 requiresAdmin 检查中处理

  next()
})

export default router
