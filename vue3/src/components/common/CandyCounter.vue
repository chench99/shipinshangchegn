<template>
  <div
    class="candy-counter"
    :class="[`candy-counter--${size}`, { 'is-disabled': disabled }]"
    role="group"
    aria-label="数量选择器"
  >
    <button
      class="counter-btn counter-btn--decrease"
      :disabled="disabled || !canDecrement"
      aria-label="减少"
      @click="decrement"
    >
      −
    </button>
    <input
      class="counter-input"
      :value="displayValue"
      :disabled="disabled"
      inputmode="numeric"
      aria-live="polite"
      @input="onInput"
      @blur="onBlur"
      @keydown.up.prevent="increment"
      @keydown.down.prevent="decrement"
    />
    <button
      class="counter-btn counter-btn--increase"
      :disabled="disabled || !canIncrement"
      aria-label="增加"
      @click="increment"
    >
      +
    </button>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({
  modelValue: { type: Number, required: true },
  min: { type: Number, default: 1 },
  max: { type: Number, default: Infinity },
  step: { type: Number, default: 1 },
  disabled: { type: Boolean, default: false },
  size: { type: String, default: 'default' }, // small | default | large
  requireAuth: { type: Boolean, default: false } // 是否需要登录验证
})

const emit = defineEmits(['update:modelValue', 'change'])
const userStore = useUserStore()
const router = useRouter()

const internal = ref(String(props.modelValue ?? ''))

watch(() => props.modelValue, (v) => {
  internal.value = String(v ?? '')
})

const normalized = computed(() => {
  const n = Number(props.modelValue)
  return Number.isFinite(n) ? n : props.min
})

const displayValue = computed(() => internal.value)

const canDecrement = computed(() => normalized.value - props.step >= props.min)
const canIncrement = computed(() => normalized.value + props.step <= props.max)

function clamp(val) {
  if (!Number.isFinite(val)) return props.min
  if (val < props.min) return props.min
  if (val > props.max) return props.max
  return val
}

function update(val) {
  const next = clamp(val)
  if (next !== props.modelValue) {
    emit('update:modelValue', next)
    emit('change', next)
  }
  internal.value = String(next)
}

function checkAuth() {
  if (props.requireAuth && !userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/auth/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return false
  }
  return true
}

function decrement() {
  if (props.disabled) return
  if (!checkAuth()) return
  update(normalized.value - props.step)
}

function increment() {
  if (props.disabled) return
  if (!checkAuth()) return
  update(normalized.value + props.step)
}

function onInput(e) {
  internal.value = e.target.value.replace(/[^\d]/g, '')
}

function onBlur() {
  const n = Number(internal.value)
  update(Number.isFinite(n) ? n : props.modelValue)
}
</script>

<style scoped>
.candy-counter {
  display: inline-flex;
  align-items: center;
  gap: 0;
  border: 1px solid rgba(255, 193, 7, 0.28);
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  border-radius: 14px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.candy-counter.is-disabled { opacity: 0.6; cursor: not-allowed; }

.counter-btn {
  appearance: none;
  border: none;
  background: transparent;
  color: #8D6E63;
  width: 38px;
  height: 38px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  cursor: pointer;
  transition: background 0.15s ease;
}

.counter-btn:hover:not(:disabled) { background: rgba(255, 193, 7, 0.12); }
.counter-btn:disabled { cursor: not-allowed; opacity: 0.6; }

.counter-input {
  width: 56px;
  text-align: center;
  border: none;
  outline: none;
  background: transparent;
  color: #5D4037;
  font-weight: 700;
}

/* Sizes */
.candy-counter--small .counter-btn { width: 30px; height: 30px; font-size: 16px; }
.candy-counter--small .counter-input { width: 44px; font-size: 13px; }

.candy-counter--default .counter-btn { width: 38px; height: 38px; font-size: 18px; }
.candy-counter--default .counter-input { width: 56px; font-size: 14px; }

.candy-counter--large .counter-btn { width: 44px; height: 44px; font-size: 20px; }
.candy-counter--large .counter-input { width: 64px; font-size: 16px; }
</style>


