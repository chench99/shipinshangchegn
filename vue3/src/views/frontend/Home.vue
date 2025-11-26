<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="carousel-section" v-loading="carouselLoading">
      <el-carousel height="360px" indicator-position="outside" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in carousels" :key="item.id">
          <div class="carousel-item" @click="handleCarouselClick(item)">
            <img :src="item.imageUrl || ''" alt="banner" />
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 首页横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">欢迎来到预制菜商城</h1>
        <p class="hero-subtitle">精选优质预制菜，满足你的味蕾需求</p>
        <CandyButton size="large" @click="goToSnackList" class="cta-btn">立即购买</CandyButton>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="category-section">
      <div class="container">
        <h2 class="section-title">热门分类</h2>
        <div class="category-grid" v-loading="categoryLoading">
          <div 
            v-for="category in categoryList" 
            :key="category.id"
            class="category-item"
            @click="goToCategorySnacks(category)"
          >
            <div class="category-icon">
              <i class="fas fa-cookie-bite"></i>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐零食 -->
    <div class="recommended-section">
      <div class="container">
        <h2 class="section-title">热门推荐</h2>
        <div class="snack-grid" v-loading="snackLoading">
          <SnackCard
            v-for="snack in recommendedSnacks"
            :key="snack.id"
            :snack="snack"
            :max-desc-length="50"
            @click="goToSnackDetail"
            @add-to-cart="addToCart"
            @view-detail="goToSnackDetail"
          />
        </div>
        
        <!-- 查看更多 -->
        <div class="more-section">
          <el-button type="primary" @click="goToSnackList">
            查看更多零食
          </el-button>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRecommendedSnacks } from '@/api/snack'
import { getActiveCategoryList } from '@/api/category'
import { getEnabledCarousels } from '@/api/carousel'
import { addToCart as addToCartApi } from '@/api/cart'
import CandyButton from '@/components/common/CandyButton.vue'
import SnackCard from '@/components/frontend/SnackCard.vue'

const router = useRouter()

// 响应式数据
const categoryLoading = ref(false)
const snackLoading = ref(false)
const categoryList = ref([])
const recommendedSnacks = ref([])
const carouselLoading = ref(false)
const carousels = ref([])

// 获取分类列表
const fetchCategories = () => {
  categoryLoading.value = true
  getActiveCategoryList({
    onSuccess: (res) => {
      categoryList.value = res || []
      categoryLoading.value = false
    },
    onError: (error) => {
      console.error('获取分类列表失败:', error)
      categoryLoading.value = false
    }
  })
}

// 获取推荐零食
const fetchRecommendedSnacks = () => {
  snackLoading.value = true
  getRecommendedSnacks({ limit: 8 }, {
    onSuccess: (res) => {
      recommendedSnacks.value = res || []
      snackLoading.value = false
    },
    onError: (error) => {
      console.error('获取推荐零食失败:', error)
      snackLoading.value = false
    }
  })
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 处理图片加载错误
const handleImageError = (event) => {
  // 避免无限循环，只设置一次默认图片
  if (event.target.src.indexOf('default-snack.jpg') === -1) {
    event.target.src = '/default-snack.jpg'
  } else {
    // 如果默认图片也加载失败，使用base64编码的占位图片
    event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7pm7bpo5/lm77niYc8L3RleHQ+PC9zdmc+'
  }
}

// 导航方法
const goToSnackList = () => {
  router.push('/snacks')
}

const goToCategorySnacks = (category) => {
  router.push({
    path: '/snacks',
    query: { categoryId: category.id, categoryName: category.name }
  })
}

const goToSnackDetail = (snack) => {
  router.push(`/snack/${snack.id}`)
}

// 加入购物车
const addToCart = (snack) => {
  if (!snack || !snack.canPurchase) {
    ElMessage.warning('商品暂时缺货')
    return
  }
  
  // 调用加入购物车API
  addToCartApi({
    snackId: snack.id,
    quantity: 1
  }, {
    onSuccess: (res) => {
      ElMessage.success(`已将 ${snack.name} 加入购物车`)
      console.log('加入购物车成功:', res)
    },
    onError: (error) => {
      console.error('加入购物车失败:', error)
      ElMessage.error('加入购物车失败，请重试')
    }
  })
}

// 获取轮播
const fetchCarousels = () => {
  carouselLoading.value = true
  getEnabledCarousels({
    onSuccess: (res) => {
      carousels.value = res || []
      carouselLoading.value = false
    },
    onError: () => { carouselLoading.value = false }
  })
}

// 轮播点击
const handleCarouselClick = (item) => {

  if (!item) return
  if (item.jumpType === 'URL' && item.jumpTarget) {
    window.open(item.jumpTarget, '_blank')
    return
  }
  if (item.jumpType === 'PRODUCT' && item.jumpTarget) {
    router.push(`/snack/${item.jumpTarget}`)
    return
  }
}

// 组件挂载时初始化
onMounted(() => {
  fetchCategories()
  fetchRecommendedSnacks()
  fetchCarousels()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background:
    radial-gradient(1000px 700px at 110% -10%, rgba(255, 224, 178, .6) 0%, rgba(255, 224, 178, 0) 60%),
    radial-gradient(900px 600px at -10% 20%, rgba(255, 248, 225, .8) 0%, rgba(255, 248, 225, 0) 60%),
    linear-gradient(180deg, #FFF8E1 0%, #FFFFFF 30%, #FFF3E0 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 轮播图 */
.carousel-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 0 20px;
}

.carousel-item {
  width: 100%;
  height: 360px;
  cursor: pointer;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
}

/* 首页横幅 */
.hero-banner {
  color: #5D4037;
  padding: 90px 0 100px 0;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
  color: #D2691E;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.7);
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: #8D6E63;
  opacity: 0.95;
}

.cta-btn {
  border-radius: 14px;
  padding: 14px 22px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  box-shadow: 0 12px 26px rgba(255, 143, 0, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

/* 分类导航 */
.category-section {
  padding: 60px 0;
  background: linear-gradient(145deg, #FFFDF6, #FFF3E0);
}

.section-title {
  text-align: center;
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 3rem;
  color: #333;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.category-item {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 20px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 10px 10px 28px rgba(255, 171, 64, 0.16),
              -6px -6px 16px rgba(255, 255, 255, 0.85),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.category-item:hover {
  transform: translateY(-4px);
  box-shadow: 14px 14px 36px rgba(255, 171, 64, 0.22),
              -8px -8px 18px rgba(255, 255, 255, 0.9);
}

.category-icon {
  font-size: 3rem;
  color: #FF8F00;
  margin-bottom: 1rem;
}

.category-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin: 0;
}

/* 推荐零食 */
.recommended-section {
  padding: 60px 0;
}

.snack-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.snack-card {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 10px 10px 28px rgba(255, 171, 64, 0.16),
              -6px -6px 16px rgba(255, 255, 255, 0.85),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.snack-card:hover {
  transform: translateY(-4px);
  box-shadow: 14px 14px 36px rgba(255, 171, 64, 0.22),
              -8px -8px 18px rgba(255, 255, 255, 0.9);
}

.snack-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.snack-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.snack-card:hover .snack-image img {
  transform: scale(1.05);
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
  font-size: 1.2rem;
  font-weight: bold;
}

.snack-info {
  padding: 1.5rem;
}

.snack-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.snack-description {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 1rem 0;
  line-height: 1.4;
}

.snack-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.snack-price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #E65100;
}

.snack-sales {
  font-size: 0.9rem;
  color: #666;
}

.snack-actions {
  text-align: center;
}

/* 查看更多 */
.more-section {
  text-align: center;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .section-title {
    font-size: 1.5rem;
  }
  
  .category-grid,
  .snack-grid,
  .features-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .snack-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}
</style>
