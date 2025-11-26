<template>
  <div class="mock-test">
    <el-card>
      <template #header>
        <h3>🎀 Mock数据测试页面</h3>
      </template>
      
      <el-space direction="vertical" size="large" style="width: 100%">
        <el-alert 
          :title="`Mock状态: ${mockEnabled ? '✅ 已启用' : '❌ 未启用'}`"
          :type="mockEnabled ? 'success' : 'warning'"
          show-icon
        />
        
        <el-divider>登录测试</el-divider>
        
        <el-form :model="loginForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="loginForm.username" placeholder="admin 或 user001" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="123456" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="testLogin" :loading="loading">
              测试登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <el-divider>测试结果</el-divider>
        
        <el-input
          v-model="result"
          type="textarea"
          :rows="10"
          placeholder="测试结果将显示在这里..."
          readonly
        />
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { login } from '@/api/user'

const loading = ref(false)
const result = ref('')

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

// 检查Mock是否启用
const mockEnabled = computed(() => {
  return import.meta.env.DEV && import.meta.env.VITE_USE_MOCK === 'true'
})

const testLogin = async () => {
  loading.value = true
  result.value = '🚀 开始测试登录...\n'
  
  try {
    const startTime = Date.now()
    
    await login(loginForm, {
      showDefaultMsg: false,
      onSuccess: (data) => {
        const endTime = Date.now()
        result.value += `✅ 登录成功！耗时: ${endTime - startTime}ms\n`
        result.value += `📋 返回数据:\n${JSON.stringify(data, null, 2)}\n`
      },
      onError: (error) => {
        const endTime = Date.now()
        result.value += `❌ 登录失败！耗时: ${endTime - startTime}ms\n`
        result.value += `📋 错误信息:\n${JSON.stringify(error, null, 2)}\n`
      }
    })
  } catch (error) {
    result.value += `💥 请求异常:\n${JSON.stringify(error, null, 2)}\n`
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  result.value = `🎀 Mock测试页面已加载\n`
  result.value += `📊 环境信息:\n`
  result.value += `- DEV模式: ${import.meta.env.DEV}\n`
  result.value += `- Mock开关: ${import.meta.env.VITE_USE_MOCK}\n`
  result.value += `- Mock状态: ${mockEnabled.value ? '启用' : '禁用'}\n\n`
  result.value += `🧪 测试说明:\n`
  result.value += `- 管理员账号: admin / 123456\n`
  result.value += `- 普通用户: user001 / 123456\n`
  result.value += `- 错误测试: 使用其他用户名或密码\n\n`
})
</script>

<style scoped>
.mock-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
</style>
