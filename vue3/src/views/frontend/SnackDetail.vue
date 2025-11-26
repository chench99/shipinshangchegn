<template>
  <div class="snack-detail-page">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-section">
        <CandyOutlineButton @click="goBack" size="small">
          <font-awesome-icon icon="arrow-left" />
          返回列表
        </CandyOutlineButton>
      </div>

      <!-- 零食详情 -->
      <div class="snack-detail-content" v-loading="loading">
        <div v-if="snack" class="detail-container">
          <el-row :gutter="40">
            <!-- 左侧：图片展示 -->
            <el-col :xs="24" :md="12">
              <div class="image-section">
                <!-- 主图 -->
                <div class="main-image">
                  <el-image
                    :src="currentImage"
                    :alt="snack.name"
                    fit="contain"
                    :preview-src-list="allImages"
                    style="width: 100%; height: 400px;"
                    @error="handleMainImageError"
                  >
                    <template #error>
                      <div class="image-error-placeholder">
                        <i class="el-icon-picture-outline"></i>
                        <span>图片加载失败</span>
                      </div>
                    </template>
                  </el-image>
                  <div v-if="!snack.hasStock" class="stock-overlay">
                    <span>暂时缺货</span>
                  </div>
                </div>
                
                <!-- 缩略图 -->
                <div v-if="allImages.length > 1" class="thumbnail-list">
                  <div 
                    v-for="(image, index) in allImages" 
                    :key="index"
                    class="thumbnail-item"
                    :class="{ active: currentImage === image }"
                    @click="currentImage = image"
                  >
                    <img :src="image" :alt="`${snack.name} 图片${index + 1}`" @error="handleThumbnailError" />
                  </div>
                </div>
              </div>
            </el-col>

            <!-- 右侧：商品信息 -->
            <el-col :xs="24" :md="12">
              <div class="info-section">
                <!-- 分类标签 -->
                <div class="category-tag">
                  <el-tag type="info">{{ snack.categoryName }}</el-tag>
                </div>

                <!-- 商品名称 -->
                <h1 class="snack-name">{{ snack.name }}</h1>

                <!-- 价格和销量 -->
                <div class="price-sales">
                  <div class="price-section">
                    <span class="price-label">价格：</span>
                    <span class="current-price">¥{{ snack.price }}</span>
                  </div>
                  <div class="sales-section">
                    <span class="sales-text">已售 {{ snack.salesCount }} 件</span>
                  </div>
                </div>

                <!-- 库存信息 -->
                <div class="stock-section">
                  <span class="stock-label">库存：</span>
                  <span class="stock-value" :class="stockClass">
                    {{ snack.stock }} 件
                  </span>
                  <span v-if="snack.stock <= 10 && snack.stock > 0" class="stock-warning">
                    (库存紧张)
                  </span>
                </div>

                <!-- 购买数量 -->
                <div class="quantity-section">
                  <span class="quantity-label">数量：</span>
                  <CandyCounter
                    v-model="quantity"
                    :min="1"
                    :max="snack.stock"
                    :disabled="!snack.canPurchase"
                    :require-auth="true"
                    size="default"
                  />
                </div>

                <!-- 购买按钮 -->
                <div class="action-section">
                <CandyButton 
                  size="large"
                  :disabled="!snack.canPurchase"
                  :require-auth="true"
                  @click="handleAddToCart"
                >
                  <font-awesome-icon icon="shopping-cart" />
                  {{ snack.canPurchase ? '加入购物车' : '暂时缺货' }}
                </CandyButton>
                  <CandyButton 
                    type="danger" 
                    size="large"
                    :disabled="!snack.canPurchase"
                    :require-auth="true"
                    @click="handleBuyNow"
                  >
                    立即购买
                  </CandyButton>
                  
                  <!-- 收藏按钮 -->
                  <FavoriteButton
                    v-if="snack.id"
                    :snack-id="snack.id"
                    :initial-favorited="favoriteStatus.isFavorited"
                    :initial-count="favoriteStatus.favoriteCount"
                    size="large"
                    :show-count="true"
                    @favorite-changed="handleFavoriteChanged"
                  />
                </div>

                <!-- 商品特性 -->
                <div class="features-section">
                  <div class="feature-item">
                    <font-awesome-icon icon="shipping-fast" />
                    <span>24小时内发货</span>
                  </div>
                  <div class="feature-item">
                    <font-awesome-icon icon="shield-alt" />
                    <span>品质保证</span>
                  </div>
                  <div class="feature-item">
                    <font-awesome-icon icon="undo" />
                    <span>7天无理由退换</span>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 商品描述 -->
          <div class="description-section">
            <el-card>
              <template #header>
                <h3>商品描述</h3>
              </template>
              <div class="description-content">
                <p v-if="snack.description">{{ snack.description }}</p>
                <p v-else class="no-description">暂无商品描述</p>
              </div>
            </el-card>
          </div>

          <!-- 用户评价 -->
          <div class="reviews-section" style="margin-top: 2rem;">
            <el-card>
              <template #header>
                <h3>用户评价</h3>
              </template>

              <div v-if="snack?.id && showReviewEditor" style="margin-bottom: 12px;">
                <ReviewEditor :snackId="Number(snack.id)" @submitted="onReviewSubmitted" />
              </div>

              <div v-loading="reviewLoading">
                <div v-if="reviewList.length === 0" class="no-description">还没有评价，快来抢沙发～</div>
                <div v-else class="review-list">
                  <div v-for="item in reviewList" :key="item.id" class="review-item">
                    <div class="review-meta">
                      <el-rate v-model="item.rating" disabled :max="5" />
                      <span class="review-time">{{ formatReviewTime(item.createTime) }}</span>
                    </div>
                    <div class="review-content">{{ item.content }}</div>
                    <div v-if="item.images && item.images.length" class="review-images">
                      <el-image
                        v-for="(img, idx) in item.images"
                        :key="idx"
                        :src="img"
                        fit="cover"
                        style="width: 80px; height: 80px; margin-right: 8px; border-radius: 6px;"
                      />
                    </div>
                  </div>

                  <div class="review-pagination">
                    <el-pagination
                      background
                      layout="prev, pager, next"
                      :total="reviewTotal"
                      :current-page="reviewPage"
                      :page-size="reviewSize"
                      @current-change="handleReviewPageChange"
                    />
                  </div>
                </div>
              </div>
            </el-card>
          </div>

        </div>

        <!-- 加载失败状态 -->
        <div v-else-if="!loading" class="error-state">
          <el-result
            icon="warning"
            title="商品不存在"
            sub-title="抱歉，您访问的商品不存在或已下架"
          >
            <template #extra>
              <CandyButton @click="goToSnackList">返回商品列表</CandyButton>
            </template>
          </el-result>
        </div>
      </div>
    </div>

    <!-- 返回顶部 -->
    <el-backtop :right="40" :bottom="40" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElImageViewer } from 'element-plus'
// 移除Element Plus图标导入，改用FontAwesome
import { getSnackById } from '@/api/snack'
import { addToCart } from '@/api/cart'
import { checkFavoriteStatus } from '@/api/favorite'
import { getReviewPage, canReview as apiCanReview } from '@/api/review'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'
import CandyCounter from '@/components/common/CandyCounter.vue'
import ReviewEditor from '@/components/frontend/ReviewEditor.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const snack = ref(null)
const quantity = ref(1)
const currentImage = ref('')
const favoriteStatus = ref({
  isFavorited: false,
  favoriteCount: 0
})

// 评价相关
const reviewLoading = ref(false)
const reviewList = ref([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const reviewSize = ref(5)
const showReviewEditor = ref(false)

// 计算属性
const allImages = computed(() => {
  if (!snack.value) return []
  const images = []
  
  // 添加封面图
  if (snack.value.coverImage) {
    images.push(snack.value.coverImage)
  }
  
  return images
})


const stockClass = computed(() => {
  if (!snack.value) return ''
  if (snack.value.stock === 0) return 'stock-empty'
  if (snack.value.stock <= 10) return 'stock-low'
  return 'stock-normal'
})

// 获取零食详情
const fetchSnackDetail = () => {
  const snackId = route.params.id
  if (!snackId) {
    router.push('/snacks')
    return
  }
  
  loading.value = true
  getSnackById(snackId, {
    onSuccess: (res) => {
      snack.value = res
      // 设置默认显示图片
      if (allImages.value.length > 0) {
        currentImage.value = allImages.value[0]
      }
      loading.value = false
      
      // 获取收藏状态
      fetchFavoriteStatus(snackId)

      // 获取评价列表
      fetchReviews()

      // 判断是否可评价，用于控制显示组件
      apiCanReview(Number(snackId), {
        showDefaultMsg: false,
        onSuccess: (res) => { showReviewEditor.value = !!res },
        onError: () => { showReviewEditor.value = false }
      })
    },
    onError: (error) => {
      console.error('获取零食详情失败:', error)
      loading.value = false
      ElMessage.error('获取商品详情失败')
    }
  })
}

// 获取收藏状态
const fetchFavoriteStatus = (snackId) => {
  checkFavoriteStatus(snackId, {
    onSuccess: (res) => {
      favoriteStatus.value = {
        isFavorited: res.isFavorited,
        favoriteCount: res.favoriteCount
      }
    },
    onError: (error) => {
      console.error('获取收藏状态失败:', error)
      // 收藏状态获取失败不影响主要功能，只记录错误
    }
  })
}

// 预览图片
const previewImage = (imageSrc) => {
  ElImageViewer({
    urlList: [imageSrc],
    initialIndex: 0
  })
}

// 处理主图加载错误
const handleMainImageError = () => {
  console.warn('主图加载失败:', currentImage.value)
  // 尝试使用封面图
  if (snack.value?.coverImage && currentImage.value !== snack.value.coverImage) {
    currentImage.value = snack.value.coverImage
  }
}

// 处理缩略图加载错误
const handleThumbnailError = (event) => {
  // 避免无限循环，只设置一次默认图片
  if (event.target.src.indexOf('default-snack.jpg') === -1) {
    event.target.src = '/default-snack.jpg'
  } else {
    // 如果默认图片也加载失败，使用base64编码的占位图片
    event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0iI2RkZCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjEyIiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5pS+5Zu+54mHPC90ZXh0Pjwvc3ZnPg=='
  }
}


// 事件处理方法
const goBack = () => {
  router.go(-1)
}

const goToSnackList = () => {
  router.push('/snacks')
}

const handleAddToCart = () => {
  if (!snack.value || !snack.value.canPurchase) {
    ElMessage.warning('商品暂时缺货')
    return
  }
  
  // 调用加入购物车API
  addToCart({
    snackId: snack.value.id,
    quantity: quantity.value
  }, {
    onSuccess: (res) => {
      ElMessage.success(`已将 ${quantity.value} 件 ${snack.value.name} 加入购物车`)
      console.log('加入购物车成功:', res)
    },
    onError: (error) => {
      console.error('加入购物车失败:', error)
      ElMessage.error('加入购物车失败，请重试')
    }
  })
}

const handleBuyNow = () => {
  if (!snack.value || !snack.value.canPurchase) {
    ElMessage.warning('商品暂时缺货')
    return
  }
  
  // 跳转到订单确认页面
  router.push({
    path: '/order/confirm',
    query: {
      type: 'direct',
      snackId: snack.value.id,
      quantity: quantity.value
    }
  })
}

// 收藏状态变化处理
const handleFavoriteChanged = (data) => {
  favoriteStatus.value = {
    isFavorited: data.isFavorited,
    favoriteCount: data.favoriteCount
  }
}

// 获取评价列表
const fetchReviews = () => {
  if (!route.params.id) return
  reviewLoading.value = true
  getReviewPage({
    current: reviewPage.value,
    size: reviewSize.value,
    snackId: Number(route.params.id)
  }, {
    onSuccess: (res) => {
      reviewList.value = res.records || []
      reviewTotal.value = res.total || 0
      reviewLoading.value = false
    },
    onError: (err) => {
      console.error('获取评价失败', err)
      reviewLoading.value = false
    }
  })
}

const handleReviewPageChange = (page) => {
  reviewPage.value = page
  fetchReviews()
}

const formatReviewTime = (time) => {
  if (!time) return ''
  try {
    const d = new Date(time)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const da = String(d.getDate()).padStart(2, '0')
    const hh = String(d.getHours()).padStart(2, '0')
    const mm = String(d.getMinutes()).padStart(2, '0')
    return `${y}-${m}-${da} ${hh}:${mm}`
  } catch {
    return ''
  }
}

// 评价提交后刷新列表并跳到第一页
const onReviewSubmitted = () => {
  reviewPage.value = 1
  fetchReviews()
}

// 组件挂载时初始化
onMounted(() => {
  fetchSnackDetail()
})
</script>

<style scoped>
.snack-detail-page {
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

/* 返回按钮 */
.back-section {
  margin-bottom: 2rem;
}

/* 详情容器 */
.detail-container {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 12px 12px 32px rgba(255, 171, 64, 0.16),
              -8px -8px 18px rgba(255, 255, 255, 0.85),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
}

/* 图片展示区域 */
.image-section {
  position: sticky;
  top: 2rem;
}

.main-image {
  position: relative;
  margin-bottom: 1rem;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
}

.stock-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.thumbnail-list {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  padding: 0.5rem 0;
}

.thumbnail-item {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.thumbnail-item:hover,
.thumbnail-item.active {
  border-color: #409eff;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区域 */
.info-section {
  padding-left: 2rem;
}

.category-tag {
  margin-bottom: 1rem;
}

.snack-name {
  font-size: 2rem;
  font-weight: 800;
  color: #D2691E;
  margin: 0 0 1.5rem 0;
  line-height: 1.3;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.7);
}

.price-sales {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 1rem;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 12px;
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.price-label {
  font-size: 1rem;
  color: #666;
}

.current-price {
  font-size: 2rem;
  font-weight: 800;
  color: #E65100;
}

.sales-section {
  font-size: 0.9rem;
  color: #666;
}

.stock-section {
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stock-label {
  font-size: 1rem;
  color: #666;
}

.stock-value {
  font-weight: bold;
}

.stock-value.stock-normal {
  color: #67c23a;
}

.stock-value.stock-low {
  color: #e6a23c;
}

.stock-value.stock-empty {
  color: #f56c6c;
}

.stock-warning {
  font-size: 0.8rem;
  color: #e6a23c;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.quantity-label {
  font-size: 1rem;
  color: #666;
}

.action-section {
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.action-section :deep(.el-button.el-button--primary) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  box-shadow: 0 12px 26px rgba(255, 143, 0, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.features-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 12px;
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #666;
}

.feature-item .el-icon,
.feature-item .fa-icon {
  color: #409eff;
}

.feature-item .svg-inline--fa {
  color: #409eff;
  margin-right: 0.5rem;
  width: 1em;
}

/* FontAwesome图标通用样式 */
.el-button .svg-inline--fa {
  margin-right: 0.5rem;
}

.back-section .svg-inline--fa {
  margin-right: 0.5rem;
}

/* 商品描述 */
.description-section {
  margin-top: 2rem;
}

.description-content {
  line-height: 1.6;
  color: #6D4C41;
}

.no-description {
  color: #999;
  font-style: italic;
}


/* 图片错误占位符 */
.image-error-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #c0c4cc;
  font-size: 14px;
}

.image-error-placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

/* 错误状态 */
.error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .snack-detail-page {
    padding: 1rem 0;
  }
  
  .detail-container {
    padding: 1rem;
  }
  
  .info-section {
    padding-left: 0;
    margin-top: 2rem;
  }
  
  .snack-name {
    font-size: 1.5rem;
  }
  
  .current-price {
    font-size: 1.5rem;
  }
  
  .price-sales {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .action-section .el-button {
    width: 100%;
    margin-bottom: 0.5rem;
    margin-right: 0 !important;
  }
  
  .quantity-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .features-section {
    margin-top: 1rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 10px;
  }
  
  .main-image {
    height: 250px;
  }
  
  .thumbnail-item {
    width: 60px;
    height: 60px;
  }
}
</style>
