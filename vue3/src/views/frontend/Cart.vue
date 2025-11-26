<template>
  <div class="cart-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">我的购物车</h1>
        <p class="page-subtitle" v-if="!cartData.isEmpty">
          共 {{ cartData.totalQuantity }} 件商品
        </p>
      </div>

      <!-- 购物车内容 -->
      <div class="cart-content" v-loading="loading">
        <!-- 空购物车状态 -->
        <div v-if="cartData.isEmpty && !loading" class="empty-cart">
          <el-empty description="购物车是空的">
            <CandyButton @click="goToSnackList">去逛逛</CandyButton>
          </el-empty>
        </div>

        <!-- 购物车商品列表 -->
        <div v-else class="cart-list">
          <!-- 购物车头部操作栏 -->
          <div class="cart-header">
            <el-checkbox 
              v-model="selectAll" 
              @change="handleSelectAll"
              :indeterminate="isIndeterminate"
            >
              全选
            </el-checkbox>
            <span class="selected-info">
              已选择 {{ selectedItems.length }} 件商品
            </span>
            <div class="header-actions">
              <el-button 
                type="danger" 
                :disabled="selectedItems.length === 0"
                @click="handleBatchDelete"
                text
              >
                删除选中
              </el-button>
              <el-button 
                type="info" 
                @click="handleClearCart"
                text
              >
                清空购物车
              </el-button>
            </div>
          </div>

          <!-- 购物车商品项 -->
          <div class="cart-items">
            <div 
              v-for="item in cartData.items" 
              :key="item.id"
              class="cart-item"
              :class="{ 'unavailable': !item.canPurchase }"
            >
              <!-- 选择框 -->
              <div class="item-select">
                <el-checkbox 
                  :model-value="selectedItems.includes(item.id)"
                  @change="handleSelectItem(item.id, $event)"
                  :disabled="!item.canPurchase"
                />
              </div>

              <!-- 商品图片 -->
              <div class="item-image">
                <el-image
                  :src="item.snackImage"
                  :alt="item.snackName"
                  fit="cover"
                  lazy
                >
                  <template #error>
                    <div class="image-error">
                      <i class="fas fa-image"></i>
                    </div>
                  </template>
                </el-image>
              </div>

              <!-- 商品信息 -->
              <div class="item-info">
                <h3 class="item-name">{{ item.snackName }}</h3>
                <p class="item-category">{{ item.categoryName }}</p>
                <p class="item-description" v-if="item.snackDescription">
                  {{ item.snackDescription }}
                </p>
                <div class="item-status" v-if="!item.canPurchase">
                  <el-tag type="warning" size="small">
                    {{ item.stock <= 0 ? '库存不足' : '已下架' }}
                  </el-tag>
                </div>
              </div>

              <!-- 单价 -->
              <div class="item-price">
                <span class="price">¥{{ item.price }}</span>
              </div>

              <!-- 数量控制 -->
              <div class="item-quantity">
                <CandyCounter
                  :model-value="item.quantity"
                  @change="(val) => handleQuantityChange(item, val)"
                  :min="1"
                  :max="item.stock"
                  :disabled="!item.canPurchase"
                  size="small"
                />
                <div class="stock-info">
                  库存: {{ item.stock }}
                </div>
              </div>

              <!-- 小计 -->
              <div class="item-subtotal">
                <span class="subtotal">¥{{ item.subtotal }}</span>
              </div>

              <!-- 操作按钮 -->
              <div class="item-actions">
                <el-button 
                  type="danger" 
                  @click="handleDeleteItem(item)"
                  text
                  size="small"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>

          <!-- 购物车底部结算栏 -->
          <div class="cart-footer">
            <div class="footer-left">
              <el-checkbox 
                v-model="selectAll" 
                @change="handleSelectAll"
                :indeterminate="isIndeterminate"
              >
                全选
              </el-checkbox>
              <span class="selected-summary">
                已选择 {{ selectedValidItems.length }} 件商品
              </span>
            </div>
            
            <div class="footer-right">
              <div class="price-summary">
                <div class="total-info">
                  <span class="label">合计:</span>
                  <span class="total-price">¥{{ selectedTotalAmount.toFixed(2) }}</span>
                </div>
                <div class="discount-info" v-if="selectedValidItems.length > 0">
                  <span class="original-price">原价: ¥{{ selectedTotalAmount.toFixed(2) }}</span>
                </div>
              </div>
              
              <CandyButton 
                size="large"
                :disabled="selectedValidItems.length === 0"
                @click="handleCheckout"
              >
                结算 ({{ selectedValidItems.length }})
              </CandyButton>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCartList, 
  updateCartQuantity, 
  deleteCartItem, 
  batchDeleteCartItems,
  clearCart 
} from '@/api/cart'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyCounter from '@/components/common/CandyCounter.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const cartData = reactive({
  items: [],
  totalQuantity: 0,
  totalAmount: 0,
  validQuantity: 0,
  validAmount: 0,
  isEmpty: true
})

// 选中的商品ID列表
const selectedItems = ref([])

// 计算属性
const selectAll = computed({
  get() {
    const availableItems = cartData.items.filter(item => item.canPurchase)
    return availableItems.length > 0 && 
           availableItems.every(item => selectedItems.value.includes(item.id))
  },
  set(value) {
    // 在handleSelectAll方法中处理
  }
})

const isIndeterminate = computed(() => {
  const availableItems = cartData.items.filter(item => item.canPurchase)
  const selectedCount = availableItems.filter(item => selectedItems.value.includes(item.id)).length
  return selectedCount > 0 && selectedCount < availableItems.length
})

// 选中的有效商品（可购买的商品）
const selectedValidItems = computed(() => {
  return cartData.items.filter(item => 
    selectedItems.value.includes(item.id) && item.canPurchase
  )
})

// 选中商品的总金额
const selectedTotalAmount = computed(() => {
  return selectedValidItems.value.reduce((total, item) => {
    return total + parseFloat(item.subtotal)
  }, 0)
})

// 获取购物车列表
const fetchCartList = () => {
  loading.value = true
  
  getCartList({
    onSuccess: (res) => {
      Object.assign(cartData, res)
      loading.value = false
      
      // 清理无效的选中项
      if (cartData.items.length > 0) {
        const validItemIds = cartData.items.map(item => item.id)
        selectedItems.value = selectedItems.value.filter(id => validItemIds.includes(id))
      } else {
        selectedItems.value = []
      }
    },
    onError: (error) => {
      console.error('获取购物车失败:', error)
      loading.value = false
    }
  })
}

// 处理全选/取消全选
const handleSelectAll = (checked) => {
  const availableItems = cartData.items.filter(item => item.canPurchase)
  
  if (checked) {
    // 全选：添加所有可购买商品的ID
    const availableIds = availableItems.map(item => item.id)
    selectedItems.value = [...new Set([...selectedItems.value, ...availableIds])]
  } else {
    // 取消全选：移除所有可购买商品的ID
    const availableIds = availableItems.map(item => item.id)
    selectedItems.value = selectedItems.value.filter(id => !availableIds.includes(id))
  }
}

// 处理单个商品选择
const handleSelectItem = (itemId, checked) => {
  if (checked) {
    if (!selectedItems.value.includes(itemId)) {
      selectedItems.value.push(itemId)
    }
  } else {
    selectedItems.value = selectedItems.value.filter(id => id !== itemId)
  }
}

// 处理数量变更
const handleQuantityChange = (item, newQuantity) => {
  if (newQuantity === item.quantity || newQuantity < 1) {
    return
  }
  
  updateCartQuantity(item.id, { quantity: newQuantity }, {
    successMsg: '更新数量成功',
    onSuccess: (res) => {
      // 更新本地数据
      const index = cartData.items.findIndex(cartItem => cartItem.id === item.id)
      if (index !== -1) {
        cartData.items[index] = res
      }
      
      // 重新计算统计信息
      recalculateCartStatistics()
    },
    onError: (error) => {
      console.error('更新数量失败:', error)
    }
  })
}

// 处理删除单个商品
const handleDeleteItem = (item) => {
  ElMessageBox.confirm(
    `确定要删除 "${item.snackName}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    deleteCartItem(item.id, {
      successMsg: '删除成功',
      onSuccess: () => {
        // 从列表中移除
        cartData.items = cartData.items.filter(cartItem => cartItem.id !== item.id)
        // 从选中项中移除
        selectedItems.value = selectedItems.value.filter(id => id !== item.id)
        
        // 重新计算统计信息
        recalculateCartStatistics()
      },
      onError: (error) => {
        console.error('删除商品失败:', error)
      }
    })
  }).catch(() => {
    // 用户取消删除
  })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  
  const selectedNames = cartData.items
    .filter(item => selectedItems.value.includes(item.id))
    .map(item => item.snackName)
    .slice(0, 3) // 最多显示3个商品名
  
  const displayNames = selectedNames.join('、')
  const moreText = selectedItems.value.length > 3 ? `等${selectedItems.value.length}个商品` : ''
  
  ElMessageBox.confirm(
    `确定要删除 "${displayNames}${moreText}" 吗？`,
    '确认批量删除',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    batchDeleteCartItems({ cartIds: selectedItems.value }, {
      successMsg: '批量删除成功',
      onSuccess: () => {
        // 从列表中移除选中的商品
        cartData.items = cartData.items.filter(item => !selectedItems.value.includes(item.id))
        // 清空选中项
        selectedItems.value = []
        
        // 重新计算统计信息
        recalculateCartStatistics()
      },
      onError: (error) => {
        console.error('批量删除失败:', error)
      }
    })
  }).catch(() => {
    // 用户取消删除
  })
}

// 处理清空购物车
const handleClearCart = () => {
  ElMessageBox.confirm(
    '确定要清空购物车吗？此操作不可恢复。',
    '确认清空购物车',
    {
      confirmButtonText: '清空',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    clearCart({
      successMsg: '购物车已清空',
      onSuccess: () => {
        // 清空本地数据
        cartData.items = []
        selectedItems.value = []
        recalculateCartStatistics()
      },
      onError: (error) => {
        console.error('清空购物车失败:', error)
      }
    })
  }).catch(() => {
    // 用户取消清空
  })
}

// 处理结算
const handleCheckout = () => {
  if (selectedValidItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 获取选中的购物车项ID
  const cartItemIds = selectedValidItems.value.map(item => item.id)
  
  // 跳转到订单确认页面
  router.push({
    path: '/order/confirm',
    query: {
      type: 'cart',
      cartItemIds: cartItemIds.join(',')
    }
  })
}

// 跳转到零食列表
const goToSnackList = () => {
  router.push('/snacks')
}

// 重新计算购物车统计信息
const recalculateCartStatistics = () => {
  if (cartData.items.length === 0) {
    cartData.isEmpty = true
    cartData.totalQuantity = 0
    cartData.totalAmount = 0
    cartData.validQuantity = 0
    cartData.validAmount = 0
    return
  }
  
  cartData.isEmpty = false
  cartData.totalQuantity = cartData.items.reduce((sum, item) => sum + item.quantity, 0)
  cartData.totalAmount = cartData.items.reduce((sum, item) => sum + parseFloat(item.subtotal), 0)
  
  const validItems = cartData.items.filter(item => item.canPurchase)
  cartData.validQuantity = validItems.reduce((sum, item) => sum + item.quantity, 0)
  cartData.validAmount = validItems.reduce((sum, item) => sum + parseFloat(item.subtotal), 0)
}

// 组件挂载时获取购物车数据
onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background:
    radial-gradient(1000px 700px at 110% -10%, rgba(255, 224, 178, .6) 0%, rgba(255, 224, 178, 0) 60%),
    radial-gradient(900px 600px at -10% 20%, rgba(255, 248, 225, .8) 0%, rgba(255, 248, 225, 0) 60%),
    linear-gradient(180deg, #FFF8E1 0%, #FFFFFF 30%, #FFF3E0 100%);
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #666;
  margin: 0;
}

/* 购物车内容 */
.cart-content {
  min-height: 400px;
}

.empty-cart {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 购物车列表 */
.cart-list {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 12px 12px 32px rgba(255, 171, 64, 0.16),
              -8px -8px 18px rgba(255, 255, 255, 0.85),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
}

/* 购物车头部 */
.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(255, 193, 7, 0.16);
  background: linear-gradient(145deg, #FFFDF6, #FFF3E0);
}

.selected-info {
  color: #666;
  font-size: 0.9rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

/* 购物车商品项 */
.cart-items {
  max-height: 60vh;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.cart-item:hover {
  background: rgba(255, 224, 178, .12);
}

.cart-item.unavailable {
  opacity: 0.6;
  background-color: #f5f5f5;
}

.item-select {
  width: 50px;
  display: flex;
  justify-content: center;
}

.item-image {
  width: 80px;
  height: 80px;
  margin-right: 1rem;
  border-radius: 8px;
  overflow: hidden;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 1.5rem;
}

.item-info {
  flex: 1;
  margin-right: 1rem;
}

.item-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.item-category {
  font-size: 0.9rem;
  color: #999;
  margin: 0 0 0.5rem 0;
}

.item-description {
  font-size: 0.85rem;
  color: #666;
  margin: 0 0 0.5rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-status {
  margin-top: 0.5rem;
}

.item-price {
  width: 100px;
  text-align: center;
  margin-right: 1rem;
}

.price {
  font-size: 1.2rem;
  font-weight: 600;
  color: #E65100;
}

.item-quantity {
  width: 150px;
  margin-right: 1rem;
}

.stock-info {
  font-size: 0.8rem;
  color: #999;
  text-align: center;
  margin-top: 0.5rem;
}

.item-subtotal {
  width: 100px;
  text-align: center;
  margin-right: 1rem;
}

.subtotal {
  font-size: 1.3rem;
  font-weight: 600;
  color: #E65100;
}

.item-actions {
  width: 80px;
  text-align: center;
}

/* 购物车底部 */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  background: linear-gradient(145deg, #FFFDF6, #FFF3E0);
  border-top: 1px solid rgba(255, 193, 7, 0.16);
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.selected-summary {
  color: #666;
  font-size: 0.9rem;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.price-summary {
  text-align: right;
}

.total-info {
  margin-bottom: 0.5rem;
}

.label {
  font-size: 1rem;
  color: #666;
  margin-right: 0.5rem;
}

.total-price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #E65100;
}

.discount-info {
  font-size: 0.9rem;
  color: #999;
}

.original-price {
  text-decoration: line-through;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-item {
    flex-direction: column;
    align-items: stretch;
    padding: 1rem;
  }
  
  .item-image {
    width: 60px;
    height: 60px;
    margin: 0 auto 1rem auto;
  }
  
  .item-info, .item-price, .item-quantity, .item-subtotal, .item-actions {
    width: 100%;
    text-align: center;
    margin: 0.5rem 0;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .footer-left, .footer-right {
    justify-content: center;
  }
}
</style>
