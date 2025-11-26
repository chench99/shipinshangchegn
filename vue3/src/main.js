import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
// 导入 Element Plus
import ElementPlus from 'element-plus'
// 导入自定义主题色配置
import './styles/element-variables.scss'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 导入初始化样式
import './styles/global.css'
// 导入FontAwesome样式
import '@fortawesome/fontawesome-free/css/all.css'
// 中文
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
// 导入Mock管理器（确保Mock API被注册）
import '@/api/mockManager'
// 导入认证工具
import { initAuth } from '@/utils/auth'

// 添加全局错误处理器来抑制ResizeObserver警告
const originalConsoleError = console.error
console.error = (...args) => {
  if (args[0] && args[0].includes && args[0].includes('ResizeObserver loop')) {
    return
  }
  originalConsoleError(...args)
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})
// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 初始化认证状态
initAuth()

app.mount('#app')
