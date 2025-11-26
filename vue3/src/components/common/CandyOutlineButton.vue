<template>
  <button
    class="candy-outline-btn"
    :class="[
      `candy-outline-btn--${type}`,
      `candy-outline-btn--${size}`,
      { 'is-block': block, 'is-loading': loading, 'is-disabled': isDisabled }
    ]"
    :disabled="isDisabled"
    @click="handleClick"
  >
    <span v-if="loading" class="candy-spinner" aria-hidden="true"></span>
    <span class="candy-outline-btn__content"><slot /></span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, default: 'default' }, // default | primary | danger | success | info | warning
  size: { type: String, default: 'default' }, // small | default | large
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  block: { type: Boolean, default: false }
})

const emit = defineEmits(['click'])

const isDisabled = computed(() => props.disabled || props.loading)

const handleClick = (e) => {
  if (isDisabled.value) return
  emit('click', e)
}
</script>

<style scoped>
.candy-outline-btn {
  -webkit-tap-highlight-color: transparent;
  appearance: none;
  border-radius: 14px;
  border: 1px solid rgba(255, 193, 7, 0.5);
  background: #fff7e6;
  color: #8D6E63;
  padding: 12px 18px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.15s ease;
}

.candy-outline-btn:hover:not(.is-disabled) {
  box-shadow: 0 8px 20px rgba(255, 143, 0, 0.15);
  transform: translateY(-1px);
}

.candy-outline-btn.is-disabled { opacity: 0.6; cursor: not-allowed; }
.candy-outline-btn.is-loading { cursor: progress; }
.candy-outline-btn.is-block { width: 100%; }

/* Sizes */
.candy-outline-btn--small { padding: 8px 14px; font-size: 13px; }
.candy-outline-btn--default { padding: 12px 18px; font-size: 14px; }
.candy-outline-btn--large { padding: 14px 22px; font-size: 16px; }

/* Types: only text/icon colors and border emphasis vary */
.candy-outline-btn--default { border-color: rgba(255, 193, 7, 0.45); color: #6D4C41; background: #fff; }
.candy-outline-btn--primary { border-color: #FF8F00; color: #FF8F00; background: #fff; }
.candy-outline-btn--danger { border-color: #E53935; color: #E53935; background: #fff; }
.candy-outline-btn--success { border-color: #43A047; color: #43A047; background: #fff; }
.candy-outline-btn--info { border-color: #1E88E5; color: #1E88E5; background: #fff; }
.candy-outline-btn--warning { border-color: #F9A825; color: #F9A825; background: #fff; }

.candy-spinner {
  width: 1em; height: 1em; border: 2px solid rgba(0,0,0,0.15); border-top-color: currentColor; border-radius: 50%; animation: spin 1s linear infinite;
}

.candy-outline-btn__content { display: inline-flex; align-items: center; gap: 8px; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>


