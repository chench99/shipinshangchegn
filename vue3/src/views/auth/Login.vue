<template>
  <div class="auth-page login-page">
    <AuthForm
      title="欢迎回来"
      subtitle="登录您的账号，继续美味的食品购物之旅"
      :fields="loginFields"
      :form-data="loginForm"
      :rules="rules"
      :loading="loading"
      submit-text="登录"
      :links="authLinks"
      @submit="handleLogin"
    />
    <p class="policy-note">登录即表示您同意《用户协议》与《隐私政策》</p>
  </div>
  
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { login } from '@/api/user'
import { User, Lock } from '@element-plus/icons-vue'
import AuthForm from '@/components/AuthForm.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// 表单字段配置
const loginFields = [
  {
    prop: 'username',
    placeholder: '请输入用户名',
    icon: User
  },
  {
    prop: 'password',
    type: 'password',
    placeholder: '请输入密码',
    icon: Lock
  }
]

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 底部链接配置
const authLinks = [
  { text: '还没有账号？立即注册', to: '/auth/register' }
]

const handleLogin = async () => {
  loading.value = true
  try {
    // 使用API接口进行登录
    await login(loginForm, {
      successMsg: "登录成功",
      showDefaultMsg: true,
      onSuccess: async (data) => {
        userStore.setUserInfo(data)

        // 根据返回的用户类型决定跳转路径
        if (data.userInfo && data.userInfo.userType === 'ADMIN') {
          // 管理员用户跳转到后台仪表盘
          await router.isReady()
          router.push(route.query.redirect || '/back/dashboard')
        } else {
          // 普通用户登录，直接跳转到前台
          const redirect = route.query.redirect || '/'
          router.push(redirect)
        }
      },
      onError: (error) => {
        console.error('登录失败:', error)
      }
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  animation: pop-in 360ms cubic-bezier(.2,.75,.3,1) 40ms both;
}

.policy-note {
  margin-top: 10px;
  text-align: center;
  font-size: 12px;
  color: #8D6E63;
  opacity: 0.9;
}

@keyframes pop-in {
  0% { opacity: 0; transform: translateY(8px) scale(.98); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* 页面定制的小幅色彩强调（不改变全局样式） */
:deep(.auth-form .form-header h2) {
  background: linear-gradient(180deg, #D2691E 0%, #8D6E63 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
</style>