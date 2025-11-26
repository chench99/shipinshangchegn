<template>
  <button
    class="candy-icon-btn"
    :class="[
      `candy-icon-btn--${type}`,
      `candy-icon-btn--${size}`,
      { 'is-plain': plain, 'is-loading': loading, 'is-disabled': isDisabled }
    ]"
    :disabled="isDisabled"
    @click="handleClick"
    :aria-label="ariaLabel"
  >
    <span v-if="loading" class="candy-spinner" aria-hidden="true"></span>
    <span class="candy-icon"><slot name="icon" /></span>
    <span v-if="showText" class="candy-text"><slot /></span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, default: 'primary' }, // primary | danger | success | info | warning | default
  size: { type: String, default: 'default' }, // small | default | large
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  plain: { type: Boolean, default: false },
  showText: { type: Boolean, default: true },
  ariaLabel: { type: String, default: '' }
})

const emit = defineEmits(['click'])

const isDisabled = computed(() => props.disabled || props.loading)

const handleClick = (e) => {
  if (isDisabled.value) return
  emit('click', e)
}
</script>

<style scoped>
.candy-icon-btn {
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
  padding: 10px 12px;
  border-radius: 12px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  box-shadow: 0 8px 20px rgba(255, 143, 0, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.5);
  transition: transform 0.15s ease, box-shadow 0.15s ease, filter 0.15s ease;
}

.candy-icon-btn.is-plain {
  background: #fff;
  color: #FF8F00;
  border: 1px solid rgba(255, 193, 7, 0.5);
  box-shadow: none;
}

.candy-icon-btn:hover:not(.is-disabled) { transform: translateY(-1px); }
.candy-icon-btn:active:not(.is-disabled) { transform: translateY(0); filter: saturate(0.95); }
.candy-icon-btn.is-disabled { opacity: 0.6; cursor: not-allowed; }
.candy-icon-btn.is-loading { cursor: progress; }

/* Sizes */
.candy-icon-btn--small { padding: 6px 8px; font-size: 13px; border-radius: 10px; }
.candy-icon-btn--default { padding: 10px 12px; font-size: 14px; }
.candy-icon-btn--large { padding: 12px 16px; font-size: 16px; }

/* Types */
.candy-icon-btn--primary { background: linear-gradient(145deg, #FFB74D, #FF8F00); }
.candy-icon-btn--danger { background: linear-gradient(145deg, #FF8A80, #E53935); }
.candy-icon-btn--success { background: linear-gradient(145deg, #81C784, #43A047); }
.candy-icon-btn--info { background: linear-gradient(145deg, #64B5F6, #1E88E5); }
.candy-icon-btn--warning { background: linear-gradient(145deg, #FFD54F, #F9A825); }
.candy-icon-btn--default { background: linear-gradient(145deg, #ECEFF1, #CFD8DC); color: #455A64; }

.candy-icon { display: inline-flex; align-items: center; }
.candy-text { display: inline-flex; align-items: center; }

.candy-spinner { width: 1em; height: 1em; border: 2px solid rgba(255,255,255,0.6); border-top-color: #fff; border-radius: 50%; animation: spin 1s linear infinite; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>


