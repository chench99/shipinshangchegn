<template>
  <div class="auth-form">
    <!-- 表单标题 -->
    <div class="form-header">
      <h2>{{ title }}</h2>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>

    <!-- 表单内容 -->
    <el-form 
      :model="formData" 
      :rules="rules" 
      ref="formRef" 
      @submit.prevent="handleSubmit"
    >
      <!-- 动态渲染表单项 -->
      <el-form-item 
        v-for="field in fields" 
        :key="field.prop"
        :prop="field.prop"
        :label="field.label"
      >
        <el-input 
          v-model="formData[field.prop]"
          :type="field.type || 'text'"
          :placeholder="field.placeholder"
          :prefix-icon="field.icon"
          :show-password="field.type === 'password'"
          clearable
          @keyup.enter="handleSubmit"
        />
      </el-form-item>
      
      <!-- 提交按钮 -->
      <el-form-item>
        <el-button 
          type="primary" 
          :loading="loading" 
          @click="handleSubmit"
          style="width: 100%"
        >
          {{ submitText }}
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 底部链接 -->
    <div class="form-links" v-if="links && links.length">
      <div v-for="link in links" :key="link.text" class="link-item">
        <router-link :to="link.to">{{ link.text }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  fields: {
    type: Array,
    required: true
  },
  formData: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  submitText: {
    type: String,
    default: '提交'
  },
  loading: {
    type: Boolean,
    default: false
  },
  links: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['submit'])

const formRef = ref(null)

const handleSubmit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      emit('submit')
    }
  })
}

defineExpose({
  formRef
})
</script>

<style scoped>
.auth-form {
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.form-header h2 {
  margin: 0 0 0.5rem 0;
  color: #D2691E;
  font-weight: 800;
  letter-spacing: .3px;
  text-shadow: 0 2px 0 rgba(255,255,255,.7);
}

.form-header p {
  margin: 0;
  color: #8D6E63;
  font-size: 0.9rem;
}

.form-links {
  text-align: center;
  margin-top: 1rem;
}

.link-item {
  margin: 0.5rem 0;
}

.link-item a {
  color: #FF8F00;
  text-decoration: none;
  font-size: 0.9rem;
}

.link-item a:hover {
  text-decoration: underline;
}

/* 轻量动效与输入/按钮统一（与 AuthLayout 深选择器一致） */
:deep(.el-input__wrapper) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  border: 1px solid rgba(255, 171, 64, 0.25);
}

:deep(.el-button.el-button--primary) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  box-shadow: 0 8px 18px rgba(255, 143, 0, 0.28), inset 0 1px 0 rgba(255,255,255,.5);
}
</style>
