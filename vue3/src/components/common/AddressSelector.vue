<template>
  <div class="address-selector">
    <div class="selector-header">
      <span class="title">选择收货地址</span>
      <el-button type="text" @click="$emit('add-address')">
        <el-icon><Plus /></el-icon>
        新增地址
      </el-button>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="addressList.length === 0" class="empty-state">
      <el-empty description="暂无收货地址">
        <el-button type="primary" @click="$emit('add-address')">
          添加地址
        </el-button>
      </el-empty>
    </div>

    <div v-else class="address-list">
      <div 
        v-for="address in addressList"
        :key="address.id"
        class="address-item"
        :class="{ 
          'is-selected': selectedAddressId === address.id,
          'is-default': address.isDefault 
        }"
        @click="selectAddress(address)"
      >
        <div class="address-radio">
          <el-radio 
            :model-value="selectedAddressId" 
            :value="address.id"
            @change="selectAddress(address)"
          >
            <span></span>
          </el-radio>
        </div>
        
        <div class="address-content">
          <div class="address-header">
            <span class="consignee">{{ address.consigneeName }}</span>
            <span class="phone">{{ address.phone }}</span>
            <el-tag v-if="address.isDefault" type="success" size="small">
              默认
            </el-tag>
          </div>
          <div class="address-detail">
            {{ address.fullAddress }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getUserAddressList } from '@/api/address'

// 属性定义
const props = defineProps({
  modelValue: {
    type: [Number, String],
    default: null
  },
  autoSelectDefault: {
    type: Boolean,
    default: true
  }
})

// 事件定义
const emit = defineEmits(['update:modelValue', 'address-selected', 'add-address'])

// 响应式数据
const addressList = ref([])
const loading = ref(false)
const selectedAddressId = ref(props.modelValue)

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  selectedAddressId.value = newValue
})

// 获取地址列表
const fetchAddressList = () => {
  loading.value = true
  getUserAddressList({
    onSuccess: (res) => {
      addressList.value = res || []
      
      // 自动选择默认地址
      if (props.autoSelectDefault && !selectedAddressId.value && addressList.value.length > 0) {
        const defaultAddress = addressList.value.find(addr => addr.isDefault)
        if (defaultAddress) {
          selectAddress(defaultAddress)
        } else {
          // 如果没有默认地址，选择第一个
          selectAddress(addressList.value[0])
        }
      }
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

// 选择地址
const selectAddress = (address) => {
  selectedAddressId.value = address.id
  emit('update:modelValue', address.id)
  emit('address-selected', address)
}

// 刷新地址列表（供父组件调用）
const refresh = () => {
  fetchAddressList()
}

// 组件挂载
onMounted(() => {
  fetchAddressList()
})

// 暴露方法给父组件
defineExpose({
  refresh
})
</script>

<style scoped>
.address-selector {
  border: 1px solid rgba(255, 193, 7, 0.16);
  border-radius: 16px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  overflow: hidden;
  box-shadow: 10px 10px 28px rgba(255, 171, 64, 0.12), -6px -6px 16px rgba(255,255,255,.85);
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 193, 7, 0.16);
  background: linear-gradient(145deg, #FFFDF6, #FFF3E0);
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.loading-container {
  padding: 20px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.address-item:last-child {
  border-bottom: none;
}

.address-item:hover {
  background: rgba(255, 224, 178, .12);
}

.address-item.is-selected {
  background: rgba(255, 224, 178, .2);
  border-left: 4px solid #FF8F00;
}

.address-item.is-default {
  background: rgba(255, 248, 225, .7);
}

.address-radio {
  margin-right: 12px;
  margin-top: 2px;
}

.address-content {
  flex: 1;
  min-width: 0;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.consignee {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.phone {
  color: #606266;
  font-size: 14px;
}

.address-detail {
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
  word-break: break-all;
}

/* 自定义滚动条 */
.address-list::-webkit-scrollbar {
  width: 6px;
}

.address-list::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.address-list::-webkit-scrollbar-thumb:hover {
  background: #a8abb2;
}

@media (max-width: 768px) {
  .selector-header {
    padding: 12px 16px;
  }
  
  .address-item {
    padding: 12px 16px;
  }
  
  .address-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
