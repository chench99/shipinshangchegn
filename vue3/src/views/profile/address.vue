<template>
  <div class="address-management">
    <div class="page-header">
      <h2>收货地址管理</h2>
      <CandyButton @click="showAddForm">
        <i class="fas fa-plus" aria-hidden="true"></i>
        新增地址
      </CandyButton>
    </div>

    <!-- 地址列表 -->
    <div class="address-list">
      <el-empty v-if="addressList.length === 0" description="暂无收货地址">
        <CandyButton @click="showAddForm">添加地址</CandyButton>
      </el-empty>

      <div v-else class="address-grid">
        <div 
          v-for="address in addressList" 
          :key="address.id"
          class="address-card"
          :class="{ 'is-default': address.isDefault }"
        >
          <div class="address-content">
            <div class="address-header">
              <span class="consignee">{{ address.consigneeName }}</span>
              <span class="phone">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ address.fullAddress }}
            </div>
          </div>
          
          <div class="address-actions">
            <CandyOutlineButton 
              v-if="!address.isDefault" 
              size="small"
              @click="setDefault(address.id)"
            >
              设为默认
            </CandyOutlineButton>
            <CandyOutlineButton size="small" @click="editAddress(address)">编辑</CandyOutlineButton>
            <el-popconfirm
              title="确认删除这个地址吗？"
              @confirm="deleteAddressHandler(address.id)"
            >
              <template #reference>
                <CandyOutlineButton type="danger" size="small">删除</CandyOutlineButton>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑地址' : '新增地址'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="consigneeName">
          <el-input 
            v-model="addressForm.consigneeName" 
            placeholder="请输入收货人姓名"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="addressForm.phone" 
            placeholder="请输入手机号"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item label="省份" prop="province">
          <el-input 
            v-model="addressForm.province" 
            placeholder="请输入省份"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="城市" prop="city">
          <el-input 
            v-model="addressForm.city" 
            placeholder="请输入城市"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="区/县" prop="district">
          <el-input 
            v-model="addressForm.district" 
            placeholder="请输入区/县"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input
            v-model="addressForm.detailedAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
            maxlength="255"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <CandyOutlineButton @click="dialogVisible = false">取消</CandyOutlineButton>
          <CandyButton @click="submitForm" :loading="submitLoading">
            {{ isEditMode ? '更新' : '创建' }}
          </CandyButton>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'
import { 
  getUserAddressList, 
  createAddress, 
  updateAddress, 
  deleteAddress, 
  setDefaultAddress 
} from '@/api/address'

// 响应式数据
const addressList = ref([])
const dialogVisible = ref(false)
const isEditMode = ref(false)
const currentEditId = ref(null)
const submitLoading = ref(false)
const addressFormRef = ref()

// 表单数据
const addressForm = reactive({
  consigneeName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailedAddress: '',
  isDefault: false
})

// 表单验证规则
const formRules = {
  consigneeName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { max: 100, message: '收货人姓名不能超过100个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  detailedAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { max: 255, message: '详细地址不能超过255个字符', trigger: 'blur' }
  ]
}

// 获取地址列表
const fetchAddressList = () => {
  getUserAddressList({
    onSuccess: (res) => {
      addressList.value = res || []
    }
  })
}

// 显示新增表单
const showAddForm = () => {
  isEditMode.value = false
  dialogVisible.value = true
}

// 编辑地址
const editAddress = (address) => {
  isEditMode.value = true
  currentEditId.value = address.id
  
  // 填充表单数据
  Object.assign(addressForm, {
    consigneeName: address.consigneeName,
    phone: address.phone,
    province: address.province || '',
    city: address.city || '',
    district: address.district || '',
    detailedAddress: address.detailedAddress,
    isDefault: address.isDefault
  })
  
  dialogVisible.value = true
}

// 提交表单
const submitForm = () => {
  addressFormRef.value.validate((valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    const formData = { ...addressForm }
    
    if (isEditMode.value) {
      // 更新地址
      updateAddress(currentEditId.value, formData, {
        successMsg: '地址更新成功',
        onSuccess: () => {
          dialogVisible.value = false
          fetchAddressList()
          submitLoading.value = false
        },
        onError: () => {
          submitLoading.value = false
        }
      })
    } else {
      // 创建地址
      createAddress(formData, {
        successMsg: '地址创建成功',
        onSuccess: () => {
          dialogVisible.value = false
          fetchAddressList()
          submitLoading.value = false
        },
        onError: () => {
          submitLoading.value = false
        }
      })
    }
  })
}

// 删除地址
const deleteAddressHandler = (addressId) => {
  deleteAddress(addressId, {
    successMsg: '地址删除成功',
    onSuccess: () => {
      fetchAddressList()
    }
  })
}

// 设置默认地址
const setDefault = (addressId) => {
  setDefaultAddress(addressId, {
    successMsg: '默认地址设置成功',
    onSuccess: () => {
      fetchAddressList()
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(addressForm, {
    consigneeName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailedAddress: '',
    isDefault: false
  })
  currentEditId.value = null
  submitLoading.value = false
  
  if (addressFormRef.value) {
    addressFormRef.value.resetFields()
  }
}

// 组件挂载
onMounted(() => {
  fetchAddressList()
})
</script>

<style scoped>
.address-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.address-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: #fff;
  transition: all 0.3s ease;
  position: relative;
}

.address-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.address-card.is-default {
  border-color: #67c23a;
  background: #f0f9ff;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.consignee {
  font-size: 16px;
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
  line-height: 1.5;
  margin-bottom: 15px;
}

.address-actions {
  display: flex;
  gap: 10px;
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}

.address-actions .el-button {
  padding: 5px 10px;
  font-size: 13px;
}

.address-actions .el-button.danger {
  color: #f56c6c;
}

.address-actions .el-button.danger:hover {
  color: #f78989;
}

.dialog-footer {
  text-align: right;
}

.el-form-item {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .address-management {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .address-grid {
    grid-template-columns: 1fr;
  }
  
  .address-card {
    padding: 15px;
  }
  
  .address-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>
