<template>
  <div class="order-management">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <el-card>
        <el-form :model="searchForm" inline>
          <el-form-item label="订单号">
            <el-input
              v-model="searchForm.orderNo"
              placeholder="请输入订单号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          
          <el-form-item label="订单状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 150px"
            >
              <el-option label="全部" value="" />
              <el-option label="待付款" value="UNPAID" />
              <el-option label="待发货" value="PAID" />
              <el-option label="已发货" value="SHIPPED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 订单表格 -->
    <div class="table-section">
      <el-card>
        <el-table
          v-loading="loading"
          :data="orderList"
          stripe
          style="width: 100%"
        >
          <el-table-column prop="orderNo" label="订单号" width="180" />
          
          <el-table-column label="商品信息" min-width="200">
            <template #default="{ row }">
              <div class="goods-info">
                <div 
                  v-for="item in row.orderItems?.slice(0, 2)" 
                  :key="item.id"
                  class="goods-item"
                >
                  <img :src="item.snackImage" :alt="item.snackName" class="goods-image" />
                  <span class="goods-name">{{ item.snackName }}</span>
                  <span class="goods-quantity">x{{ item.quantity }}</span>
                </div>
                <div v-if="row.orderItems?.length > 2" class="more-goods">
                  还有{{ row.orderItems.length - 2 }}件商品...
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="totalAmountYuan" label="订单金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.totalAmountYuan }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="订单状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">
                {{ row.statusDesc }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="下单时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="text" @click="viewOrderDetail(row.id)">
                查看详情
              </el-button>
              
              <el-button 
                v-if="row.status === 'PAID'"
                type="primary"
                text
                @click="handleShipOrder(row.id)"
                :loading="shippingOrderId === row.id"
              >
                发货
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="80%"
      max-width="1000px"
    >
      <div v-if="currentOrderDetail" class="order-detail-dialog">
        <!-- 订单基本信息 -->
        <div class="detail-section">
          <h4>订单信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">
              {{ currentOrderDetail.orderNo }}
            </el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusTagType(currentOrderDetail.status)">
                {{ currentOrderDetail.statusDesc }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              ¥{{ currentOrderDetail.totalAmountYuan }}
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">
              {{ formatDateTime(currentOrderDetail.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="支付时间">
              {{ formatDateTime(currentOrderDetail.paymentTime) || '未支付' }}
            </el-descriptions-item>
            <el-descriptions-item label="发货时间">
              {{ formatDateTime(currentOrderDetail.shipTime) || '未发货' }}
            </el-descriptions-item>
            <el-descriptions-item label="完成时间">
              {{ formatDateTime(currentOrderDetail.completeTime) || '未完成' }}
            </el-descriptions-item>
            <el-descriptions-item label="订单备注">
              {{ currentOrderDetail.remark || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 收货地址信息 -->
        <div v-if="currentOrderDetail.address" class="detail-section">
          <h4>收货地址</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="收货人">
              {{ currentOrderDetail.address.consigneeName }} {{ currentOrderDetail.address.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="收货地址">
              {{ currentOrderDetail.address.fullAddress }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 商品信息 -->
        <div class="detail-section">
          <h4>商品信息</h4>
          <el-table :data="currentOrderDetail.orderItems" border>
            <el-table-column label="商品" min-width="200">
              <template #default="{ row }">
                <div class="detail-goods-item">
                  <img :src="row.snackImage" :alt="row.snackName" class="detail-goods-image" />
                  <span class="detail-goods-name">{{ row.snackName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="priceYuan" label="单价" width="100" align="right">
              <template #default="{ row }">
                ¥{{ row.priceYuan }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="subtotalYuan" label="小计" width="100" align="right">
              <template #default="{ row }">
                ¥{{ row.subtotalYuan }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentOrderDetail?.status === 'PAID'"
          type="primary"
          @click="handleShipOrderFromDialog"
          :loading="shippingOrderId === currentOrderDetail?.id"
        >
          发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getAdminOrderPage, getAdminOrderDetail, shipOrder } from '@/api/order'

// 响应式数据
const orderList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const shippingOrderId = ref(null)
const detailDialogVisible = ref(false)
const currentOrderDetail = ref(null)

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  status: ''
})

// 获取订单列表
const fetchOrderList = () => {
  loading.value = true
  
  const params = {
    current: currentPage.value,
    size: pageSize.value
  }
  
  if (searchForm.orderNo) {
    params.orderNo = searchForm.orderNo.trim()
  }
  
  if (searchForm.status) {
    params.status = searchForm.status
  }
  
  getAdminOrderPage(params, {
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

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchOrderList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    status: ''
  })
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
  getAdminOrderDetail(orderId, {
    onSuccess: (res) => {
      currentOrderDetail.value = res
      detailDialogVisible.value = true
    }
  })
}

// 发货操作
const handleShipOrder = (orderId) => {
  shippingOrderId.value = orderId
  
  shipOrder(orderId, {
    successMsg: '发货成功',
    onSuccess: () => {
      fetchOrderList()
      // 如果详情对话框打开，更新详情
      if (detailDialogVisible.value && currentOrderDetail.value?.id === orderId) {
        viewOrderDetail(orderId)
      }
      shippingOrderId.value = null
    },
    onError: () => {
      shippingOrderId.value = null
    }
  })
}

// 从详情对话框发货
const handleShipOrderFromDialog = () => {
  if (currentOrderDetail.value) {
    handleShipOrder(currentOrderDetail.value.id)
  }
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
.order-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.search-section {
  margin-bottom: 20px;
}

.table-section {
  margin-bottom: 20px;
}

.goods-info {
  max-width: 200px;
}

.goods-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  gap: 8px;
}

.goods-item:last-child {
  margin-bottom: 0;
}

.goods-image {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
}

.goods-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-quantity {
  font-size: 12px;
  color: #909399;
  min-width: 30px;
}

.more-goods {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.order-detail-dialog {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.detail-goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-goods-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
}

.detail-goods-name {
  font-size: 14px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-management {
    padding: 15px;
  }
  
  .goods-info {
    max-width: 150px;
  }
  
  .goods-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .goods-name {
    font-size: 12px;
  }
}
</style>
