<template>
  <div class="order-list">
    <div class="list-container">
      <div class="list-header">
        <h2>我的订单</h2>
      </div>

      <!-- 订单状态筛选 -->
      <div class="status-filter">
        <CandyTabs v-model="activeStatus" :items="statusTabs" @change="handleStatusChange" />
      </div>

      <!-- 订单列表 -->
      <div class="orders-content">
        <div v-if="loading" class="loading-state">
          <el-skeleton v-for="i in 3" :key="i" :rows="4" animated class="order-skeleton" />
        </div>

        <el-empty v-else-if="orderList.length === 0" description="暂无订单">
          <CandyButton @click="$router.push('/frontend/home')">去购物</CandyButton>
        </el-empty>

        <div v-else class="order-items">
          <div 
            v-for="order in orderList" 
            :key="order.id"
            class="order-card"
          >
            <!-- 订单头部 -->
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusTagType(order.status)">
                  {{ order.statusDesc }}
                </el-tag>
              </div>
            </div>

            <!-- 订单商品 -->
            <div class="order-goods">
              <div 
                v-for="item in order.orderItems" 
                :key="item.id"
                class="goods-item"
              >
                <div class="goods-image">
                  <img :src="item.snackImage" :alt="item.snackName" />
                </div>
                <div class="goods-info">
                  <div class="goods-name">{{ item.snackName }}</div>
                  <div class="goods-spec">
                    <span class="price">¥{{ item.priceYuan }}</span>
                    <span class="quantity">x{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单金额 -->
            <div class="order-amount">
              <span class="amount-label">订单金额：</span>
              <span class="amount-value">¥{{ order.totalAmountYuan }}</span>
            </div>

            <!-- 订单操作 -->
            <div class="order-actions">
              <CandyOutlineButton @click="viewOrderDetail(order.id)">查看详情</CandyOutlineButton>
              
              <CandyButton 
                v-if="order.status === 'UNPAID'" 
                @click="payOrder(order.id)"
                :loading="payingOrderId === order.id"
              >立即支付</CandyButton>
              
              <CandyOutlineButton 
                v-if="order.status === 'UNPAID'" 
                @click="cancelOrderHandler(order.id)"
                :loading="cancellingOrderId === order.id"
              >取消订单</CandyOutlineButton>
              
              <CandyButton 
                v-if="order.status === 'SHIPPED'" 
                @click="confirmReceive(order.id)"
                :loading="confirmingOrderId === order.id"
              >确认收货</CandyButton>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-wrapper">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrderPage, payOrder as apiPayOrder, cancelOrder, completeOrder } from '@/api/order'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'
import CandyTabs from '@/components/common/CandyTabs.vue'

const router = useRouter()

// 响应式数据
const orderList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeStatus = ref('')
const statusTabs = [
  { label: '全部', name: '' },
  { label: '待付款', name: 'UNPAID' },
  { label: '待发货', name: 'PAID' },
  { label: '已发货', name: 'SHIPPED' },
  { label: '已完成', name: 'COMPLETED' },
  { label: '已取消', name: 'CANCELLED' }
]
const payingOrderId = ref(null)
const cancellingOrderId = ref(null)
const confirmingOrderId = ref(null)

// 获取订单列表
const fetchOrderList = () => {
  loading.value = true
  
  const params = {
    current: currentPage.value,
    size: pageSize.value
  }
  
  if (activeStatus.value) {
    params.status = activeStatus.value
  }
  
  getUserOrderPage(params, {
    onSuccess: (res) => {
      orderList.value = res.records || []
      total.value = res.total || 0
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

// 状态筛选变化
const handleStatusChange = (status) => {
  activeStatus.value = status
  currentPage.value = 1
  fetchOrderList()
}

// 分页大小变化
const handleSizeChange = () => {
  currentPage.value = 1
  fetchOrderList()
}

// 当前页变化
const handleCurrentChange = () => {
  fetchOrderList()
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  router.push(`/order/detail/${orderId}`)
}

// 支付订单
const payOrder = (orderId) => {
  ElMessageBox.confirm('确认支付此订单吗？', '提示', {
    confirmButtonText: '确认支付',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    payingOrderId.value = orderId
    
    apiPayOrder(orderId, {
      successMsg: '支付成功',
      onSuccess: () => {
        fetchOrderList()
        payingOrderId.value = null
      },
      onError: () => {
        payingOrderId.value = null
      }
    })
  })
}

// 取消订单
const cancelOrderHandler = (orderId) => {
  ElMessageBox.confirm('确认取消此订单吗？', '提示', {
    confirmButtonText: '确认取消',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    cancellingOrderId.value = orderId
    
    cancelOrder(orderId, {
      successMsg: '订单已取消',
      onSuccess: () => {
        fetchOrderList()
        cancellingOrderId.value = null
      },
      onError: () => {
        cancellingOrderId.value = null
      }
    })
  })
}

// 确认收货
const confirmReceive = (orderId) => {
  ElMessageBox.confirm('确认已收到商品吗？', '提示', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    confirmingOrderId.value = orderId
    
    completeOrder(orderId, {
      successMsg: '确认收货成功',
      onSuccess: () => {
        fetchOrderList()
        confirmingOrderId.value = null
      },
      onError: () => {
        confirmingOrderId.value = null
      }
    })
  })
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    'UNPAID': 'warning',
    'PAID': 'info',
    'SHIPPED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
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

onMounted(() => {
  fetchOrderList()
})
</script>

<style scoped>
.order-list {
  min-height: 100vh;
  background:
    radial-gradient(1000px 700px at 110% -10%, rgba(255, 224, 178, .6) 0%, rgba(255, 224, 178, 0) 60%),
    radial-gradient(900px 600px at -10% 20%, rgba(255, 248, 225, .8) 0%, rgba(255, 248, 225, 0) 60%),
    linear-gradient(180deg, #FFF8E1 0%, #FFFFFF 30%, #FFF3E0 100%);
  padding: 20px 0;
}

.list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.list-header {
  text-align: center;
  margin-bottom: 30px;
}

.list-header h2 {
  color: #303133;
  font-size: 24px;
  margin: 0;
}

.status-filter {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 14px;
  padding: 0 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 193, 7, 0.16);
  display: flex;
  align-items: center;
  min-height: 56px;
}

/* 让 Tabs 与容器垂直居中，并去掉底部分割线，统一高度 */
:deep(.status-filter .el-tabs__header) { margin: 0; padding: 8px 0; }
:deep(.status-filter .el-tabs__nav-wrap::after) { display: none; }
:deep(.status-filter .el-tabs__item) {
  height: 40px;
  line-height: 40px;
  display: inline-flex;
  align-items: center;
}

.orders-content {
  min-height: 400px;
}

.loading-state .order-skeleton {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 16px;
}

/* .order-items 使用 .order-card 的 margin-bottom 形成分隔 */

.order-card {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 193, 7, 0.16);
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 12px 12px 32px rgba(255, 171, 64, 0.16), -8px -8px 18px rgba(255, 255, 255, 0.85);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

.order-goods {
  margin-bottom: 16px;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.goods-item:not(:last-child) {
  border-bottom: 1px solid #f5f5f5;
  margin-bottom: 8px;
  padding-bottom: 16px;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
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
  font-size: 14px;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-spec {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 14px;
  font-weight: 600;
}

.quantity {
  color: #909399;
  font-size: 13px;
}

.order-amount {
  text-align: right;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 193, 7, 0.16);
}

.amount-label {
  font-size: 14px;
  color: #606266;
}

.amount-value {
  font-size: 16px;
  color: #E65100;
  font-weight: 700;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.order-actions .el-button {
  padding: 8px 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 14px;
  border: 1px solid rgba(255, 193, 7, 0.16);
}

@media (max-width: 768px) {
  .list-container {
    padding: 0 15px;
  }
  
  .order-card {
    padding: 15px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .goods-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .goods-image {
    width: 50px;
    height: 50px;
    margin-right: 0;
    margin-bottom: 8px;
  }
  
  .goods-spec {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .order-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .order-actions .el-button {
    width: 100%;
  }
}
</style>
