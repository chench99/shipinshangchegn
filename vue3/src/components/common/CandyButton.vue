<template>
  <button
    class="candy-btn"
    :class="[
      `candy-btn--${type}`,
      `candy-btn--${size}`,
      { 'is-block': block, 'is-loading': loading, 'is-disabled': isDisabled }
    ]"
    :disabled="isDisabled"
    @click="handleClick"
  >
    <span v-if="loading" class="candy-spinner" aria-hidden="true"></span>
    <span class="candy-btn__content"><slot /></span>
  </button>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({
  type: { type: String, default: 'primary' }, // primary | danger | success | info | warning
  size: { type: String, default: 'default' }, // small | default | large
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  block: { type: Boolean, default: false },
  requireAuth: { type: Boolean, default: false } // 是否需要登录验证
})

const emit = defineEmits(['click'])
const userStore = useUserStore()
const router = useRouter()

const isDisabled = computed(() => props.disabled || props.loading)

const handleClick = (e) => {
  if (isDisabled.value) return
  
  // 如果需要登录验证且用户未登录
  if (props.requireAuth && !userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/auth/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  
  emit('click', e)
}
</script>

<style scoped>
.candy-btn {
  -webkit-tap-highlight-color: transparent;
  appearance: none;
  border: none;
  outline: none;
  cursor: pointer;
  user-select: none;
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 18px;
  border-radius: 14px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(120deg, #FFB74D, #FF8F00);
  box-shadow: 0 12px 26px rgba(255, 143, 0, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 193, 7, 0.28);
  transition: transform 0.15s ease, box-shadow 0.15s ease, filter 0.15s ease;
}

.candy-btn:hover:not(.is-disabled) {
  transform: translateY(-1px);
  box-shadow: 0 14px 32px rgba(255, 143, 0, 0.42), inset 0 1px 0 rgba(255, 255, 255, 0.55);
}

.candy-btn:active:not(.is-disabled) {
  transform: translateY(0);
  filter: saturate(0.95);
}

.candy-btn.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.candy-btn.is-loading {
  cursor: progress;
}

.candy-btn.is-block {
  width: 100%;
}

/* Sizes */
.candy-btn--small { padding: 8px 14px; border-radius: 12px; font-size: 13px; }
.candy-btn--default { padding: 12px 18px; font-size: 14px; }
.candy-btn--large { padding: 14px 22px; font-size: 16px; }

/* Types */
.candy-btn--primary { background: linear-gradient(145deg, #FFB74D, #FF8F00); }
.candy-btn--danger { background: linear-gradient(145deg, #FF8A80, #E53935); box-shadow: 0 12px 26px rgba(229, 57, 53, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5); border-color: rgba(229, 57, 53, 0.28); }
.candy-btn--success { background: linear-gradient(145deg, #81C784, #43A047); box-shadow: 0 12px 26px rgba(67, 160, 71, 0.35); border-color: rgba(67, 160, 71, 0.28); }
.candy-btn--info { background: linear-gradient(145deg, #64B5F6, #1E88E5); box-shadow: 0 12px 26px rgba(30, 136, 229, 0.35); border-color: rgba(30, 136, 229, 0.28); }
.candy-btn--warning { background: linear-gradient(145deg, #FFD54F, #F9A825); box-shadow: 0 12px 26px rgba(249, 168, 37, 0.35); border-color: rgba(249, 168, 37, 0.28); }

.candy-spinner {
  width: 1em;
  height: 1em;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.candy-btn__content { display: inline-flex; align-items: center; gap: 8px; }

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>


