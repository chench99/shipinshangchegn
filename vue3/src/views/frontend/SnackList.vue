<template>
  <div class="snack-list-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          {{ currentCategoryName ? `${currentCategoryName} - 零食列表` : '全部食品' }}
        </h1>
        <p class="page-subtitle">
          共找到 {{ total }} 款美味食品
        </p>
      </div>

      <!-- 搜索筛选栏 -->
      <SnackSearchBar
        @search="handleSearch"
        @filter="handleFilter"
        @sort="handleSort"
        @reset="handleReset"
      />

      <!-- 零食列表 -->
      <div class="snack-list-content" v-loading="loading">
        <div v-if="snackList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无符合条件的零食">
            <CandyButton @click="handleReset">重置筛选条件</CandyButton>
          </el-empty>
        </div>
        
        <div v-else class="snack-grid">
          <SnackCard
            v-for="snack in snackList"
            :key="snack.id"
            :snack="snack"
            @click="goToSnackDetail"
            @add-to-cart="handleAddToCart"
            @view-detail="goToSnackDetail"
          />
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[12, 24, 36, 48]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 返回顶部 -->
    <el-backtop :right="40" :bottom="40" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CandyButton from '@/components/common/CandyButton.vue'
import SnackCard from '@/components/frontend/SnackCard.vue'
import SnackSearchBar from '@/components/frontend/SnackSearchBar.vue'
import { getFrontendSnackPage, searchSnacks } from '@/api/snack'
import { addToCart } from '@/api/cart'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const snackList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 当前筛选参数
const filterParams = reactive({
  name: '',
  categoryId: null,
  sortBy: '',
  sortOrder: '',
  minPrice: null,
  maxPrice: null
})

// 计算属性
const currentCategoryName = computed(() => {
  return route.query.categoryName || ''
})

// 获取零食列表
const fetchSnackList = (params = {}) => {
  loading.value = true
  
  const requestParams = {
    current: currentPage.value,
    size: pageSize.value,
    ...filterParams,
    ...params
  }
  
  // 根据是否有搜索关键词选择不同的API
  const apiCall = requestParams.name ? searchSnacks : getFrontendSnackPage
  
  // 如果是搜索API，需要将name参数改为keyword
  if (requestParams.name && apiCall === searchSnacks) {
    requestParams.keyword = requestParams.name
    delete requestParams.name
  }
  
  apiCall(requestParams, {
    onSuccess: (res) => {
      snackList.value = res.records || []
      total.value = res.total || 0
      loading.value = false
      
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    onError: (error) => {
      console.error('获取零食列表失败:', error)
      loading.value = false
      ElMessage.error('获取零食列表失败')
    }
  })
}

// 从URL初始化筛选参数
const initFromQuery = () => {
  const query = route.query
  
  // 重置当前页
  currentPage.value = parseInt(query.page) || 1
  pageSize.value = parseInt(query.size) || 12
  
  // 设置筛选参数
  Object.assign(filterParams, {
    name: query.keyword || '',
    categoryId: query.categoryId ? parseInt(query.categoryId) : null,
    sortBy: query.sortBy || '',
    sortOrder: query.sortOrder || '',
    minPrice: query.minPrice ? parseFloat(query.minPrice) : null,
    maxPrice: query.maxPrice ? parseFloat(query.maxPrice) : null
  })
}

// 更新URL查询参数
const updateQuery = (params = {}) => {
  const query = {
    ...route.query,
    page: currentPage.value.toString(),
    size: pageSize.value.toString(),
    ...params
  }
  
  // 清理空值和undefined值
  Object.keys(query).forEach(key => {
    if (!query[key] || query[key] === 'undefined' || query[key] === '' || query[key] === 'null') {
      delete query[key]
    }
  })
  
  router.replace({ query })
}

// 事件处理方法
const handleSearch = (params) => {
  currentPage.value = 1
  // 重置筛选参数，然后应用新参数
  Object.assign(filterParams, {
    name: '',
    categoryId: null,
    sortBy: '',
    sortOrder: '',
    minPrice: null,
    maxPrice: null
  }, params)
  updateQuery(params)
  fetchSnackList()
}

const handleFilter = (params) => {
  currentPage.value = 1
  // 重置筛选参数，然后应用新参数
  Object.assign(filterParams, {
    name: '',
    categoryId: null,
    sortBy: '',
    sortOrder: '',
    minPrice: null,
    maxPrice: null
  }, params)
  updateQuery(params)
  fetchSnackList()
}

const handleSort = (params) => {
  currentPage.value = 1
  // 重置筛选参数，然后应用新参数
  Object.assign(filterParams, {
    name: '',
    categoryId: null,
    sortBy: '',
    sortOrder: '',
    minPrice: null,
    maxPrice: null
  }, params)
  updateQuery(params)
  fetchSnackList()
}

const handleReset = () => {
  currentPage.value = 1
  Object.assign(filterParams, {
    name: '',
    categoryId: null,
    sortBy: '',
    sortOrder: '',
    minPrice: null,
    maxPrice: null
  })
  
  // 保留分类筛选（如果是从分类页面进入的）
  const query = {}
  if (route.query.categoryId && route.query.categoryName) {
    query.categoryId = route.query.categoryId
    query.categoryName = route.query.categoryName
    filterParams.categoryId = parseInt(route.query.categoryId)
  }
  
  updateQuery(query)
  fetchSnackList()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  updateQuery()
  fetchSnackList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  updateQuery()
  fetchSnackList()
}

// 零食相关操作
const goToSnackDetail = (snack) => {
  router.push(`/snack/${snack.id}`)
}

const handleAddToCart = (snack) => {
  if (!snack.canPurchase) {
    ElMessage.warning('商品暂时缺货')
    return
  }
  
  // 调用加入购物车API，默认数量为1
  addToCart({
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

// 监听路由变化
watch(() => route.query, () => {
  initFromQuery()
  fetchSnackList()
}, { immediate: false })

// 组件挂载时初始化
onMounted(() => {
  initFromQuery()
  fetchSnackList()
})
</script>

<style scoped>
.snack-list-page {
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
  font-weight: 800;
  color: #D2691E;
  margin: 0 0 0.5rem 0;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.7);
}

.page-subtitle {
  font-size: 1.1rem;
  color: #8D6E63;
  margin: 0;
}

/* 零食列表内容 */
.snack-list-content {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.snack-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 2rem 0;
}

/* Element 分页轻量美化 */
.pagination-container :deep(.el-pagination .el-pager li.is-active) {
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  color: #fff;
}

.pagination-container :deep(.el-pagination .btn-next:hover),
.pagination-container :deep(.el-pagination .btn-prev:hover),
.pagination-container :deep(.el-pagination .el-pager li:hover) {
  color: #FF8F00;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .snack-grid {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .snack-list-page {
    padding: 1rem 0;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .snack-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 1rem;
  }
  
  .pagination-container {
    padding: 1rem 0;
  }
  
  /* 分页在移动端的样式调整 */
  .pagination-container :deep(.el-pagination) {
    justify-content: center;
  }
  
  .pagination-container :deep(.el-pagination .el-pager li) {
    min-width: 32px;
    height: 32px;
    line-height: 32px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 10px;
  }
  
  .snack-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .page-header {
    margin-bottom: 1rem;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
}
</style>
