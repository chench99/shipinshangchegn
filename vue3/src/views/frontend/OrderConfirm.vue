<template>
  <div class="order-confirm">
    <div class="confirm-container">
      <div class="confirm-header">
        <h2>确认订单</h2>
      </div>

      <!-- 收货地址选择 -->
      <div class="section address-section">
        <AddressSelector 
          ref="addressSelectorRef"
          v-model="selectedAddressId"
          @address-selected="handleAddressSelected"
          @add-address="showAddressDialog"
        />
      </div>

      <!-- 商品信息 -->
      <div class="section goods-section">
        <div class="section-title">
          <i class="fas fa-shopping-bag"></i>
          <span>商品信息</span>
        </div>
        
        <div class="goods-list">
          <div 
            v-for="item in orderItems" 
            :key="item.id || item.snackId"
            class="goods-item"
          >
            <div class="goods-image">
              <img :src="item.coverImage || item.snackImage" :alt="item.name || item.snackName" />
            </div>
            <div class="goods-info">
              <div class="goods-name">{{ item.name || item.snackName }}</div>
              <div class="goods-price">
                ¥{{ (item.price || 0) }}
              </div>
            </div>
            <div class="goods-quantity">
              x{{ item.quantity }}
            </div>
            <div class="goods-subtotal">
              ¥{{ ((item.price || 0) * item.quantity).toFixed(2) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="section remark-section">
        <div class="section-title">
          <i class="fas fa-edit"></i>
          <span>订单备注</span>
        </div>
        <el-input
          v-model="orderForm.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入订单备注（选填）"
          maxlength="500"
          show-word-limit
        />
      </div>

      <!-- 订单汇总 -->
      <div class="section summary-section">
        <div class="summary-content">
          <div class="summary-row">
            <span>商品总计</span>
            <span>¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费</span>
            <span>免费</span>
          </div>
          <div class="summary-row total-row">
            <span>总计</span>
            <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="section actions-section">
        <div class="actions-content">
          <CandyOutlineButton size="large" @click="goBack">返回</CandyOutlineButton>
          <CandyButton 
            size="large" 
            @click="submitOrder"
            :loading="submitLoading"
            :disabled="!selectedAddressId || orderItems.length === 0"
          >
            提交订单
          </CandyButton>
        </div>
      </div>
    </div>

    <!-- 地址表单对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      title="新增收货地址"
      width="500px"
      @close="resetAddressForm"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressFormRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="consigneeName">
          <el-input 
            v-model="addressForm.consigneeName" 
            placeholder="请输入收货人姓名"
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="addressForm.phone" 
            placeholder="请输入手机号"
          />
        </el-form-item>
        
        <el-form-item label="省份">
          <el-input 
            v-model="addressForm.province" 
            placeholder="请输入省份"
          />
        </el-form-item>
        
        <el-form-item label="城市">
          <el-input 
            v-model="addressForm.city" 
            placeholder="请输入城市"
          />
        </el-form-item>
        
        <el-form-item label="区/县">
          <el-input 
            v-model="addressForm.district" 
            placeholder="请输入区/县"
          />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input
            v-model="addressForm.detailedAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddressForm" :loading="addressSubmitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// Element Plus 图标已替换为 Font Awesome 图标
import AddressSelector from '@/components/common/AddressSelector.vue'
import { createOrder } from '@/api/order'
import { createAddress } from '@/api/address'
import { getCartList } from '@/api/cart'
import { getSnackById } from '@/api/snack'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const orderItems = ref([])
const selectedAddressId = ref(null)
const selectedAddress = ref(null)
const submitLoading = ref(false)
const addressDialogVisible = ref(false)
const addressSubmitLoading = ref(false)
const addressFormRef = ref()
const addressSelectorRef = ref()

// 表单数据
const orderForm = reactive({
  orderType: '',
  addressId: null,
  remark: '',
  cartItemIds: [],
  directOrderItem: null
})

const addressForm = reactive({
  consigneeName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailedAddress: ''
})

// 地址表单验证规则
const addressFormRules = {
  consigneeName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  detailedAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 计算总金额
const totalAmount = computed(() => {
  return orderItems.value.reduce((total, item) => {
    const price = parseFloat(item.price || 0)
    const quantity = item.quantity || 0
    return total + (price * quantity)
  }, 0)
})

// 处理地址选择
const handleAddressSelected = (address) => {
  selectedAddress.value = address
  orderForm.addressId = address.id
}

// 显示地址表单
const showAddressDialog = () => {
  addressDialogVisible.value = true
}

// 提交地址表单
const submitAddressForm = () => {
  addressFormRef.value.validate((valid) => {
    if (!valid) return
    
    addressSubmitLoading.value = true
    
    createAddress({ ...addressForm }, {
      successMsg: '地址添加成功',
      onSuccess: () => {
        addressDialogVisible.value = false
        // 刷新地址选择器
        if (addressSelectorRef.value) {
          addressSelectorRef.value.refresh()
        }
        addressSubmitLoading.value = false
      },
      onError: () => {
        addressSubmitLoading.value = false
      }
    })
  })
}

// 重置地址表单
const resetAddressForm = () => {
  Object.assign(addressForm, {
    consigneeName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailedAddress: ''
  })
  if (addressFormRef.value) {
    addressFormRef.value.resetFields()
  }
}

// 提交订单
const submitOrder = () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  if (orderItems.value.length === 0) {
    ElMessage.warning('订单商品不能为空')
    return
  }
  
  submitLoading.value = true
  
  const orderData = {
    orderType: orderForm.orderType,
    addressId: selectedAddressId.value,
    remark: orderForm.remark || null
  }
  
  // 根据订单类型添加对应字段
  if (orderForm.orderType === 'CART_ORDER') {
    orderData.cartItemIds = orderForm.cartItemIds
  } else if (orderForm.orderType === 'DIRECT_ORDER') {
    orderData.directOrderItem = orderForm.directOrderItem
  }
  
  createOrder(orderData, {
    successMsg: '订单创建成功',
    onSuccess: (res) => {
      // 跳转到订单详情页面
      router.push(`/order/detail/${res.id}`)
      submitLoading.value = false
    },
    onError: () => {
      submitLoading.value = false
    }
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 初始化订单数据
const initOrderData = async () => {
  const orderType = route.query.type
  
  if (orderType === 'cart') {
    // 购物车下单
    orderForm.orderType = 'CART_ORDER'
    const cartItemIds = route.query.cartItemIds
    
    if (cartItemIds) {
      orderForm.cartItemIds = cartItemIds.split(',').map(id => parseInt(id))
      
      // 获取购物车数据
      getCartList({
        onSuccess: (res) => {
          if (res && res.items) {
            // 过滤出选中的购物车项
            orderItems.value = res.items.filter(item => 
              orderForm.cartItemIds.includes(item.id)
            )
          }
        }
      })
    }
  } else if (orderType === 'direct') {
    // 直接下单
    orderForm.orderType = 'DIRECT_ORDER'
    const snackId = parseInt(route.query.snackId)
    const quantity = parseInt(route.query.quantity || 1)
    
    if (snackId) {
      orderForm.directOrderItem = { snackId, quantity }
      
      // 获取商品信息
      getSnackById(snackId, {
        onSuccess: (res) => {
          if (res) {
            orderItems.value = [{
              snackId: res.id,
              name: res.name,
              price: res.price,
              quantity: quantity,
              coverImage: res.coverImage
            }]
          }
        }
      })
    }
  }
}

onMounted(() => {
  initOrderData()
})
</script>

<style scoped>
.order-confirm {
  min-height: 100vh;
  background:
    radial-gradient(1000px 700px at 110% -10%, rgba(255, 224, 178, .6) 0%, rgba(255, 224, 178, 0) 60%),
    radial-gradient(900px 600px at -10% 20%, rgba(255, 248, 225, .8) 0%, rgba(255, 248, 225, 0) 60%),
    linear-gradient(180deg, #FFF8E1 0%, #FFFFFF 30%, #FFF3E0 100%);
  padding: 20px 0;
}

.confirm-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.confirm-header {
  text-align: center;
  margin-bottom: 30px;
}

.confirm-header h2 {
  color: #303133;
  font-size: 24px;
  margin: 0;
}

.section {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 16px;
  margin-bottom: 20px;
  overflow: hidden;
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  font-weight: 600;
  color: #303133;
}

.section-title i {
  color: #FF8F00;
  width: 16px;
  text-align: center;
}

.goods-section .goods-list {
  padding: 0;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-image {
  width: 60px;
  height: 60px;
  margin-right: 16px;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-price {
  font-size: 14px;
  color: #E65100;
  font-weight: 700;
}

.goods-quantity {
  margin: 0 20px;
  color: #606266;
  font-size: 14px;
}

.goods-subtotal {
  font-size: 16px;
  color: #E65100;
  font-weight: 700;
  min-width: 80px;
  text-align: right;
}

.remark-section {
  padding: 20px;
}

.summary-section {
  padding: 20px;
}

.summary-content {
  max-width: 300px;
  margin-left: auto;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.total-row {
  padding-top: 12px;
  border-top: 1px solid rgba(255, 193, 7, 0.16);
  font-size: 16px;
  font-weight: 600;
}

.total-price {
  color: #E65100;
  font-size: 18px;
}

.actions-section {
  padding: 20px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  position: sticky;
  bottom: 0;
  border-top: 1px solid rgba(255, 193, 7, 0.16);
}

.actions-content {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

.actions-content .el-button {
  min-width: 120px;
}

.actions-content :deep(.el-button.el-button--primary) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  box-shadow: 0 12px 26px rgba(255, 143, 0, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

@media (max-width: 768px) {
  .confirm-container {
    padding: 0 15px;
  }
  
  .goods-item {
    padding: 12px 15px;
  }
  
  .goods-image {
    width: 50px;
    height: 50px;
    margin-right: 12px;
  }
  
  .goods-quantity {
    margin: 0 10px;
  }
  
  .summary-content {
    max-width: none;
  }
  
  .actions-content {
    flex-direction: column;
  }
  
  .actions-content .el-button {
    width: 100%;
  }
}
</style>
