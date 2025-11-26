<template>
  <div class="candy-tabs" role="tablist" :class="[`candy-tabs--${size}`]">
    <button
      v-for="item in items"
      :key="item.name"
      class="candy-tab"
      :class="{ 'is-active': item.name === modelValue, 'is-disabled': item.disabled }"
      role="tab"
      :aria-selected="item.name === modelValue"
      :disabled="item.disabled"
      @click="onSelect(item.name)"
    >
      <span class="tab-label">{{ item.label }}</span>
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  items: { type: Array, default: () => [] },
  size: { type: String, default: 'default' } // small | default | large
})

const emit = defineEmits(['update:modelValue', 'change'])

function onSelect(name) {
  if (name === props.modelValue) return
  emit('update:modelValue', name)
  emit('change', name)
}
</script>

<style scoped>
.candy-tabs {
  display: inline-flex;
  gap: 10px;
  padding: 8px;
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border: 1px solid rgba(255, 193, 7, 0.16);
  box-shadow: 0 6px 16px rgba(255, 171, 64, 0.10), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.candy-tab {
  appearance: none;
  border: 1px solid rgba(255, 193, 7, 0.28);
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  color: #6D4C41;
  font-weight: 700;
  border-radius: 12px;
  padding: 8px 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(255, 171, 64, 0.10), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.candy-tab:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(255, 171, 64, 0.14), inset 0 1px 0 rgba(255, 255, 255, 0.75);
}

.candy-tab.is-active {
  color: #4E342E;
  border-color: #FF8F00;
  background: linear-gradient(145deg, #FFEBCC, #FFD59A);
  box-shadow: 0 10px 22px rgba(255, 143, 0, 0.18), inset 0 1px 0 rgba(255, 255, 255, 0.75);
}

.candy-tab.is-disabled {
  cursor: not-allowed;
  opacity: 0.6;
  background: linear-gradient(145deg, #F7F7F7, #F0F0F0);
  border-color: #E0E0E0;
  box-shadow: none;
}

.candy-tabs--small { padding: 6px; }
.candy-tabs--small .candy-tab { padding: 6px 10px; border-radius: 10px; font-size: 13px; }

.candy-tabs--large { padding: 10px; }
.candy-tabs--large .candy-tab { padding: 10px 18px; border-radius: 14px; font-size: 15px; }

.tab-label { white-space: nowrap; }
</style>


