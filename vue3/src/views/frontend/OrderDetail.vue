<template>
  <div class="order-detail">
    <div class="detail-container">
      <div class="detail-header">
        <el-button @click="goBack" class="back-button">
          <i class="fas fa-arrow-left"></i>
          返回
        </el-button>
        <h2>订单详情</h2>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else-if="orderDetail" class="detail-content">
        <!-- 订单状态 -->
        <div class="section status-section">
          <div class="status-content">
            <div class="status-icon">
              <i :class="getStatusIcon(orderDetail.status)" style="font-size: 48px;"></i>
            </div>
            <div class="status-info">
              <div class="status-text">{{ orderDetail.statusDesc }}</div>
              <div class="status-time">{{ getStatusTime(orderDetail) }}</div>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="section address-section">
        <div class="section-title">
          <i class="fas fa-map-marker-alt"></i>
          <span>收货地址</span>
        </div>
          <div v-if="orderDetail.address" class="address-content">
            <div class="address-header">
              <span class="consignee">{{ orderDetail.address.consigneeName }}</span>
              <span class="phone">{{ orderDetail.address.phone }}</span>
            </div>
            <div class="address-detail">
              {{ orderDetail.address.fullAddress }}
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="section goods-section">
        <div class="section-title">
          <i class="fas fa-shopping-bag"></i>
          <span>商品信息</span>
        </div>
          <div class="goods-list">
            <div 
              v-for="item in orderDetail.orderItems" 
              :key="item.id"
              class="goods-item"
            >
              <div class="goods-image">
                <img :src="item.snackImage" :alt="item.snackName" />
              </div>
              <div class="goods-info">
                <div class="goods-name">{{ item.snackName }}</div>
                <div class="goods-price">¥{{ item.priceYuan }}</div>
              </div>
              <div class="goods-quantity">x{{ item.quantity }}</div>
              <div class="goods-subtotal">¥{{ item.subtotalYuan }}</div>
            </div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="section info-section">
        <div class="section-title">
          <i class="fas fa-file-alt"></i>
          <span>订单信息</span>
        </div>
          <div class="info-content">
            <div class="info-row">
              <span class="label">订单号：</span>
              <span class="value">{{ orderDetail.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatDateTime(orderDetail.createTime) }}</span>
            </div>
            <div v-if="orderDetail.paymentTime" class="info-row">
              <span class="label">支付时间：</span>
              <span class="value">{{ formatDateTime(orderDetail.paymentTime) }}</span>
            </div>
            <div v-if="orderDetail.shipTime" class="info-row">
              <span class="label">发货时间：</span>
              <span class="value">{{ formatDateTime(orderDetail.shipTime) }}</span>
            </div>
            <div v-if="orderDetail.completeTime" class="info-row">
              <span class="label">完成时间：</span>
              <span class="value">{{ formatDateTime(orderDetail.completeTime) }}</span>
            </div>
            <div v-if="orderDetail.cancelTime" class="info-row">
              <span class="label">取消时间：</span>
              <span class="value">{{ formatDateTime(orderDetail.cancelTime) }}</span>
            </div>
            <div v-if="orderDetail.remark" class="info-row">
              <span class="label">订单备注：</span>
              <span class="value">{{ orderDetail.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 费用明细 -->
        <div class="section summary-section">
        <div class="section-title">
          <i class="fas fa-dollar-sign"></i>
          <span>费用明细</span>
        </div>
          <div class="summary-content">
            <div class="summary-row">
              <span>商品总计</span>
              <span>¥{{ orderDetail.totalAmountYuan }}</span>
            </div>
            <div class="summary-row">
              <span>运费</span>
              <span>免费</span>
            </div>
            <div class="summary-row total-row">
              <span>总计</span>
              <span class="total-price">¥{{ orderDetail.totalAmountYuan }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="section actions-section">
          <div class="actions-content">
            <CandyButton 
              v-if="orderDetail.status === 'UNPAID'" 
              size="large"
              @click="payOrder"
              :loading="actionLoading"
            >
              立即支付
            </CandyButton>
            
            <CandyOutlineButton 
              v-if="orderDetail.status === 'UNPAID'" 
              size="large"
              @click="cancelOrder"
              :loading="actionLoading"
            >
              取消订单
            </CandyOutlineButton>
            
            <CandyButton 
              v-if="orderDetail.status === 'SHIPPED'" 
              size="large"
              @click="confirmReceive"
              :loading="actionLoading"
            >
              确认收货
            </CandyButton>
          </div>
        </div>
      </div>

      <el-empty v-else description="订单不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
// Element Plus 图标已替换为 Font Awesome 图标
import { 
  getOrderDetail, 
  payOrder as apiPayOrder, 
  cancelOrder as apiCancelOrder, 
  completeOrder 
} from '@/api/order'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const orderDetail = ref(null)
const loading = ref(false)
const actionLoading = ref(false)

// 获取订单详情
const fetchOrderDetail = () => {
  loading.value = true
  const orderId = route.params.id
  
  getOrderDetail(orderId, {
    onSuccess: (res) => {
      orderDetail.value = res
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

// 支付订单
const payOrder = () => {
  ElMessageBox.confirm('确认支付此订单吗？', '提示', {
    confirmButtonText: '确认支付',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    actionLoading.value = true
    
    apiPayOrder(orderDetail.value.id, {
      successMsg: '支付成功',
      onSuccess: () => {
        fetchOrderDetail()
        actionLoading.value = false
      },
      onError: () => {
        actionLoading.value = false
      }
    })
  })
}

// 取消订单
const cancelOrder = () => {
  ElMessageBox.confirm('确认取消此订单吗？', '提示', {
    confirmButtonText: '确认取消',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    actionLoading.value = true
    
    apiCancelOrder(orderDetail.value.id, {
      successMsg: '订单已取消',
      onSuccess: () => {
        fetchOrderDetail()
        actionLoading.value = false
      },
      onError: () => {
        actionLoading.value = false
      }
    })
  })
}

// 确认收货
const confirmReceive = () => {
  ElMessageBox.confirm('确认已收到商品吗？', '提示', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    actionLoading.value = true
    
    completeOrder(orderDetail.value.id, {
      successMsg: '确认收货成功',
      onSuccess: () => {
        fetchOrderDetail()
        actionLoading.value = false
      },
      onError: () => {
        actionLoading.value = false
      }
    })
  })
}

// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    'UNPAID': 'fas fa-exclamation-triangle',
    'PAID': 'fas fa-clock',
    'SHIPPED': 'fas fa-truck',
    'COMPLETED': 'fas fa-check-circle',
    'CANCELLED': 'fas fa-times-circle'
  }
  return iconMap[status] || 'fas fa-clock'
}

// 获取状态时间
const getStatusTime = (order) => {
  const timeMap = {
    'UNPAID': '等待付款',
    'PAID': order.paymentTime ? `已于 ${formatDateTime(order.paymentTime)} 付款` : '等待发货',
    'SHIPPED': order.shipTime ? `已于 ${formatDateTime(order.shipTime)} 发货` : '已发货',
    'COMPLETED': order.completeTime ? `已于 ${formatDateTime(order.completeTime)} 完成` : '已完成',
    'CANCELLED': order.cancelTime ? `已于 ${formatDateTime(order.cancelTime)} 取消` : '已取消'
  }
  return timeMap[order.status] || ''
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background:
    radial-gradient(1000px 700px at 110% -10%, rgba(255, 224, 178, .6) 0%, rgba(255, 224, 178, 0) 60%),
    radial-gradient(900px 600px at -10% 20%, rgba(255, 248, 225, .8) 0%, rgba(255, 248, 225, 0) 60%),
    linear-gradient(180deg, #FFF8E1 0%, #FFFFFF 30%, #FFF3E0 100%);
  padding: 20px 0;
}

.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}

.back-button {
  padding: 8px 12px;
}

.detail-header h2 {
  color: #303133;
  font-size: 24px;
  margin: 0;
}

.loading-state {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 14px;
  padding: 30px;
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

.status-section {
  padding: 30px 20px;
  text-align: center;
}

.status-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.status-icon {
  color: #FF8F00;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.status-time {
  font-size: 14px;
  color: #606266;
}

.address-content {
  padding: 20px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
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
}

.goods-list {
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

.info-content {
  padding: 20px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  width: 80px;
  color: #606266;
  flex-shrink: 0;
}

.value {
  color: #303133;
  word-break: break-all;
}

.summary-content {
  padding: 20px;
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
  border-top: 1px solid #e4e7ed;
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
  justify-content: center;
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
  .detail-container {
    padding: 0 15px;
  }
  
  .detail-header {
    margin-bottom: 20px;
  }
  
  .detail-header h2 {
    font-size: 20px;
  }
  
  .status-section {
    padding: 20px 15px;
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
  
  .info-content {
    padding: 15px;
  }
  
  .summary-content {
    padding: 15px;
  }
  
  .actions-content {
    flex-direction: column;
  }
  
  .actions-content .el-button {
    width: 100%;
  }
}
</style>
