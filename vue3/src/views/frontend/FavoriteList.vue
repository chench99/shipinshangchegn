<template>
  <div class="favorite-list-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <font-awesome-icon icon="heart" />
          我的收藏
        </h1>
        <div class="header-stats">
          <span class="total-count">共 {{ totalCount }} 件商品</span>
        </div>
      </div>

      <!-- 收藏列表 -->
      <div class="favorite-content" v-loading="loading">
        <!-- 有收藏商品时 -->
        <div v-if="favoriteList.length > 0" class="favorite-grid">
          <SnackCard
            v-for="item in favoriteList"
            :key="item.favoriteId"
            :snack="{
              id: item.snackId,
              name: item.snackName,
              coverImage: item.coverImage,
              description: item.snackDescription,
              categoryName: item.categoryName,
              price: item.price,
              salesCount: item.salesCount,
              hasStock: item.stock > 0,
              canPurchase: item.canPurchase
            }"
            :max-desc-length="60"
            @click="goToSnackDetail"
            @add-to-cart="handleAddToCart"
            @view-detail="goToSnackDetail"
          >
            <template #header-right>
              <FavoriteButton
                :snack-id="item.snackId"
                :initial-favorited="true"
                :initial-count="item.favoriteCount"
                :show-text="false"
                :show-count="false"
                size="small"
                @favorite-changed="handleFavoriteChanged"
              />
            </template>
          </SnackCard>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <el-result
            icon="info"
            title="暂无收藏"
            sub-title="您还没有收藏任何商品，快去发现喜欢的零食吧！"
          >
            <template #extra>
              <CandyButton @click="goToSnackList">
                <font-awesome-icon icon="search" />
                去逛逛
              </CandyButton>
            </template>
          </el-result>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalCount > 0" class="pagination-section">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFavoriteList } from '@/api/favorite'
import { addToCart as apiAddToCart } from '@/api/cart'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'
import SnackCard from '@/components/frontend/SnackCard.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const favoriteList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const totalCount = ref(0)

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(totalCount.value / pageSize.value)
})

// 获取收藏列表
const fetchFavoriteList = () => {
  loading.value = true
  getFavoriteList({
    current: currentPage.value,
    size: pageSize.value
  }, {
    onSuccess: (res) => {
      favoriteList.value = res.records || []
      totalCount.value = res.total || 0
      loading.value = false
    },
    onError: (error) => {
      console.error('获取收藏列表失败:', error)
      loading.value = false
      ElMessage.error('获取收藏列表失败')
    }
  })
}

// 格式化收藏时间
const formatFavoriteTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 小于1小时显示分钟
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000))
    return minutes <= 0 ? '刚刚' : `${minutes}分钟前`
  }
  
  // 小于1天显示小时
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }
  
  // 小于7天显示天数
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  }
  
  // 超过7天显示具体日期
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 收藏状态变化处理
const handleFavoriteChanged = (data) => {
  if (!data.isFavorited) {
    // 如果取消收藏，从列表中移除
    favoriteList.value = favoriteList.value.filter(item => item.snackId !== data.snackId)
    totalCount.value = Math.max(0, totalCount.value - 1)
    
    // 如果当前页没有数据且不是第一页，跳转到上一页
    if (favoriteList.value.length === 0 && currentPage.value > 1) {
      currentPage.value--
      fetchFavoriteList()
    }
  } else {
    // 更新收藏数量
    const item = favoriteList.value.find(item => item.snackId === data.snackId)
    if (item) {
      item.favoriteCount = data.favoriteCount
    }
  }
}

// 加入购物车
const handleAddToCart = (item) => {
  if (!item.canPurchase) {
    ElMessage.warning('商品暂时缺货')
    return
  }
  
  apiAddToCart({
    snackId: item.snackId,
    quantity: 1
  }, {
    onSuccess: () => {
      ElMessage.success(`已将 ${item.snackName} 加入购物车`)
    },
    onError: (error) => {
      console.error('加入购物车失败:', error)
      ElMessage.error('加入购物车失败，请重试')
    }
  })
}

// 跳转到商品详情
const goToSnackDetail = (payload) => {
  const id = typeof payload === 'object' && payload !== null
    ? (payload.id || payload.snackId)
    : payload
  if (!id) return
  router.push(`/snack/${id}`)
}

// 跳转到商品列表
const goToSnackList = () => {
  router.push('/snacks')
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFavoriteList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFavoriteList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchFavoriteList()
})
</script>

<style scoped>
.favorite-list-page {
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

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1.5rem 0;
  border-bottom: 2px solid rgba(255, 193, 7, 0.2);
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #D2691E;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.7);
}

.page-title .svg-inline--fa {
  color: #F44336;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.total-count {
  font-size: 1rem;
  color: #666;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 193, 7, 0.2);
}

/* 收藏网格 */
.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.favorite-card {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 
    8px 8px 20px rgba(255, 171, 64, 0.12),
    -4px -4px 12px rgba(255, 255, 255, 0.9),
    inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
  transition: all 0.3s ease;
  position: relative;
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 
    12px 12px 32px rgba(255, 171, 64, 0.2),
    -6px -6px 18px rgba(255, 255, 255, 0.95),
    inset 0 0 0 1px rgba(255, 193, 7, 0.12);
}

/* 商品图片 */
.card-image {
  position: relative;
  height: 200px;
  cursor: pointer;
  overflow: hidden;
}

.snack-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.card-image:hover .snack-image {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f5f5;
  color: #ccc;
  font-size: 2rem;
}

.status-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-text {
  color: white;
  font-size: 1.2rem;
  font-weight: bold;
  background: rgba(244, 67, 54, 0.9);
  padding: 0.5rem 1rem;
  border-radius: 8px;
}

/* 卡片内容 */
.card-content {
  padding: 1.25rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.snack-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #D2691E;
  margin: 0;
  cursor: pointer;
  transition: color 0.3s ease;
  flex: 1;
  margin-right: 0.5rem;
  line-height: 1.4;
}

.snack-name:hover {
  color: #FF8F00;
}

.card-info {
  margin-bottom: 1rem;
}

.price-section {
  margin-bottom: 0.75rem;
}

.current-price {
  font-size: 1.5rem;
  font-weight: 800;
  color: #E65100;
}

.meta-info {
  margin-bottom: 0.75rem;
}

.category-tag {
  margin-bottom: 0.5rem;
}

.stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #666;
}

.favorite-time {
  font-size: 0.8rem;
  color: #999;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.time-text {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  gap: 0.5rem;
}

.card-actions .el-button {
  flex: 1;
  border-radius: 8px;
}

.card-actions :deep(.el-button--primary) {
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
}

.card-actions .svg-inline--fa {
  margin-right: 0.5rem;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-state :deep(.el-result__icon) {
  font-size: 4rem;
}

.empty-state .svg-inline--fa {
  margin-right: 0.5rem;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  padding: 2rem 0;
  border-top: 1px solid rgba(255, 193, 7, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorite-list-page {
    padding: 1rem 0;
  }
  
  .container {
    padding: 0 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
  
  .favorite-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .card-actions .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .card-content {
    padding: 1rem;
  }
  
  .card-image {
    height: 160px;
  }
  
  .page-title {
    font-size: 1.3rem;
  }
}
</style>
