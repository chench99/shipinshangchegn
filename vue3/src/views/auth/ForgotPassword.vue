<template>
  <div v-if="!emailSent">
    <AuthForm
      title="找回密码"
      subtitle="输入您的邮箱，我们将发送重置链接"
      :fields="forgotFields"
      :form-data="forgotForm"
      :rules="rules"
      :loading="loading"
      submit-text="发送重置链接"
      :links="authLinks"
      @submit="handleSendReset"
    />
  </div>

  <!-- 成功提示 -->
  <div v-else class="success-message">
    <h2>邮件已发送</h2>
    <p>我们已向 <strong>{{ forgotForm.email }}</strong> 发送了密码重置链接</p>
    <p>请检查您的邮箱（包括垃圾邮件文件夹）</p>

    <div class="resend-section">
      <p>没有收到邮件？</p>
      <el-button
        type="text"
        :disabled="countdown > 0"
        @click="handleSendReset"
      >
        {{ countdown > 0 ? `${countdown}秒后可重新发送` : '重新发送' }}
      </el-button>
    </div>

    <div class="back-links">
      <router-link to="/auth/login">返回登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AuthForm from '@/components/AuthForm.vue'

const router = useRouter()
const loading = ref(false)
const emailSent = ref(false)
const countdown = ref(0)

const forgotForm = reactive({
  email: ''
})

// 表单字段配置
const forgotFields = [
  {
    prop: 'email',
    placeholder: '请输入您的邮箱地址',
    icon: Message
  }
]

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 底部链接配置
const authLinks = [
  { text: '返回登录', to: '/auth/login' },
  { text: '还没有账号？立即注册', to: '/auth/register' }
]

// 发送重置邮件
const handleSendReset = async () => {
  loading.value = true
  try {
    // 这里调用发送重置邮件的API
    // await sendResetEmail(forgotForm.email)

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    emailSent.value = true
    startCountdown()
    ElMessage.success('重置链接已发送到您的邮箱')
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 倒计时
const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}
</script>

<style scoped>
.success-message {
  text-align: center;
  padding: 2rem;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 16px;
  border: 1px solid rgba(255, 193, 7, 0.16);
  box-shadow: 10px 10px 28px rgba(255, 171, 64, 0.16), -6px -6px 16px rgba(255, 255, 255, 0.85);
}

.success-message h2 {
  color: #333;
  margin: 0 0 1rem 0;
}

.success-message p {
  color: #666;
  margin: 0.5rem 0;
  line-height: 1.5;
}

.success-message strong {
  color: #E65100;
}

.resend-section {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 193, 7, 0.16);
}

.resend-section p {
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.back-links {
  margin-top: 1rem;
}

.back-links a {
  color: #FF8F00;
  text-decoration: none;
  font-size: 0.9rem;
}

.back-links a:hover {
  text-decoration: underline;
}
</style>
