<template>
  <div class="auth-page register-page">
    <AuthForm
      title="加入我们"
      subtitle="创建账号，开启您的零食购物之旅"
      :fields="registerFields"
      :form-data="registerForm"
      :rules="rules"
      :loading="loading"
      submit-text="注册"
      :links="authLinks"
      @submit="handleRegister"
    />
    <p class="policy-note">注册即表示同意《用户协议》与《隐私政策》</p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Phone, UserFilled } from '@element-plus/icons-vue'
import { register } from '@/api/user'
import AuthForm from '@/components/AuthForm.vue'

const router = useRouter()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
  userType: 'USER', // 默认注册为普通用户
})

// 表单字段配置
const registerFields = [
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
  },
  {
    prop: 'confirmPassword',
    type: 'password',
    placeholder: '请确认密码',
    icon: Lock
  },
  {
    prop: 'nickname',
    placeholder: '请输入昵称（可选）',
    icon: UserFilled
  },
  {
    prop: 'phone',
    placeholder: '请输入手机号（可选）',
    icon: Phone
  }
]

const validatePass2 = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}


const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度必须在3到50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度必须在6到100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  nickname: [
    { required: false }
  ]
}

// 底部链接配置
const authLinks = [
  { text: '已有账号？立即登录', to: '/auth/login' }
]

const handleRegister = async () => {
  loading.value = true
  try {
    // 保留 confirmPassword 字段，因为后端需要验证
    const registerData = { ...registerForm }
    
    // 清理空字符串字段，将其设为undefined（但保留必需字段）
    const cleanedData = Object.fromEntries(
      Object.entries(registerData).map(([key, value]) => {
        // 对于必需字段（username, password, confirmPassword, userType），不进行清理
        if (['username', 'password', 'confirmPassword', 'userType'].includes(key)) {
          return [key, value]
        }
        // 对于可选字段，清理空字符串
        return [key, typeof value === 'string' && value.trim() === '' ? undefined : value]
      })
    )
    
    await register(cleanedData, {
      successMsg: "注册成功！欢迎加入预制菜商城",
      showDefaultMsg: true,
      onSuccess: () => {
        router.push('/auth/login')
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
  background: linear-gradient(180deg, #8D6E63 0%, #6D4C41 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
</style>