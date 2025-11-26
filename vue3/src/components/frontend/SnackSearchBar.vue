<template>
  <div class="snack-search-bar">
    <el-card class="search-card">
      <div class="search-content">
        <!-- 搜索输入框 -->
        <div class="search-input-section">
          <div class="custom-input">
            <i class="fas fa-search input-prefix" aria-hidden="true"></i>
            <input
              v-model="searchKeyword"
              class="custom-input__inner"
              type="text"
              placeholder="搜索食品名称..."
              @keyup.enter="handleSearch"
            />
            <CandyButton class="custom-input__append" @click="handleSearch">搜索</CandyButton>
          </div>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-section">
          <el-row :gutter="16">
            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label class="filter-label">分类</label>
                <el-select 
                  v-model="selectedCategoryId" 
                  placeholder="全部分类" 
                  clearable
                  style="width: 100%"
                  @change="handleFilter"
                >
                  <el-option 
                    v-for="category in categoryList" 
                    :key="category.id" 
                    :label="category.name" 
                    :value="category.id" 
                  />
                </el-select>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label class="filter-label">排序</label>
                <el-select 
                  v-model="sortOption" 
                  placeholder="默认排序"
                  style="width: 100%"
                  @change="handleSort"
                >
                  <el-option label="默认排序" value="default" />
                  <el-option label="价格升序" value="price_asc" />
                  <el-option label="价格降序" value="price_desc" />
                  <el-option label="销量降序" value="sales_desc" />
                  <el-option label="最新上架" value="time_desc" />
                </el-select>
              </div>
            </el-col>

            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label class="filter-label">价格区间</label>
                <div class="price-range">
                  <el-input
                    v-model.number="priceRange.min"
                    placeholder="最低价"
                    size="small"
                    style="width: 45%"
                    @blur="handlePriceFilter"
                  />
                  <span class="price-separator">-</span>
                  <el-input
                    v-model.number="priceRange.max"
                    placeholder="最高价"
                    size="small"
                    style="width: 45%"
                    @blur="handlePriceFilter"
                  />
                </div>
              </div>
            </el-col>

            <el-col :xs="24" :sm="24" :md="6">
              <div class="filter-actions">
                <CandyOutlineButton @click="handleReset">
                  <i class="fas fa-undo" aria-hidden="true"></i>
                  重置
                </CandyOutlineButton>
                <CandyButton @click="handleFilter">
                  <i class="fas fa-filter" aria-hidden="true"></i>
                  筛选
                </CandyButton>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 当前筛选标签 -->
        <div class="active-filters" v-if="hasActiveFilters">
          <span class="filter-title">当前筛选：</span>
          <el-tag 
            v-if="searchKeyword"
            closable
            @close="clearSearchKeyword"
          >
            关键词: {{ searchKeyword }}
          </el-tag>
          <el-tag 
            v-if="selectedCategoryName"
            closable
            @close="clearCategory"
          >
            分类: {{ selectedCategoryName }}
          </el-tag>
          <el-tag 
            v-if="sortOption !== 'default'"
            closable
            @close="clearSort"
          >
            排序: {{ getSortLabel(sortOption) }}
          </el-tag>
          <el-tag 
            v-if="hasPriceRange"
            closable
            @close="clearPriceRange"
          >
            价格: ¥{{ priceRange.min || 0 }} - ¥{{ priceRange.max || '∞' }}
          </el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getActiveCategoryList } from '@/api/category'
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'

const route = useRoute()
const router = useRouter()

// 定义emits
const emit = defineEmits(['search', 'filter', 'sort', 'reset'])

// 响应式数据
const searchKeyword = ref('')
const selectedCategoryId = ref(null)
const sortOption = ref('default')
const priceRange = ref({
  min: null,
  max: null
})
const categoryList = ref([])

// 计算属性
const selectedCategoryName = computed(() => {
  if (!selectedCategoryId.value) return ''
  const category = categoryList.value.find(cat => cat.id === selectedCategoryId.value)
  return category ? category.name : ''
})

const hasActiveFilters = computed(() => {
  return searchKeyword.value || 
         selectedCategoryId.value || 
         sortOption.value !== 'default' || 
         hasPriceRange.value
})

const hasPriceRange = computed(() => {
  return priceRange.value.min !== null || priceRange.value.max !== null
})

// 获取排序标签
const getSortLabel = (value) => {
  const sortLabels = {
    'default': '默认排序',
    'price_asc': '价格升序',
    'price_desc': '价格降序',
    'sales_desc': '销量降序',
    'time_desc': '最新上架'
  }
  return sortLabels[value] || '默认排序'
}

// 获取分类列表
const fetchCategories = () => {
  getActiveCategoryList({
    onSuccess: (res) => {
      categoryList.value = res || []
    },
    onError: (error) => {
      console.error('获取分类列表失败:', error)
    }
  })
}

// 从URL查询参数初始化筛选条件
const initFromQuery = () => {
  const query = route.query
  
  if (query.keyword) {
    searchKeyword.value = query.keyword
  }
  
  if (query.categoryId) {
    selectedCategoryId.value = parseInt(query.categoryId)
  }
  
  if (query.sortBy && query.sortOrder) {
    const sortKey = `${query.sortBy}_${query.sortOrder}`
    if (['price_asc', 'price_desc', 'sales_desc', 'time_desc'].includes(sortKey)) {
      sortOption.value = sortKey
    }
  }
  
  if (query.minPrice) {
    priceRange.value.min = parseFloat(query.minPrice)
  }
  
  if (query.maxPrice) {
    priceRange.value.max = parseFloat(query.maxPrice)
  }
}

// 构建筛选参数
const buildFilterParams = () => {
  const params = {}
  
  if (searchKeyword.value) {
    params.name = searchKeyword.value
  }
  
  if (selectedCategoryId.value) {
    params.categoryId = selectedCategoryId.value
  }
  
  // 处理排序
  if (sortOption.value !== 'default') {
    const [sortBy, sortOrder] = sortOption.value.split('_')
    params.sortBy = sortBy === 'time' ? 'create_time' : sortBy
    params.sortOrder = sortOrder
  }
  
  if (priceRange.value.min !== null) {
    params.minPrice = priceRange.value.min
  }
  
  if (priceRange.value.max !== null) {
    params.maxPrice = priceRange.value.max
  }
  
  return params
}

// 更新URL查询参数
const updateQuery = (params) => {
  const query = { ...route.query }
  
  // 清除旧的筛选参数
  delete query.keyword
  delete query.categoryId
  delete query.sortBy
  delete query.sortOrder
  delete query.minPrice
  delete query.maxPrice
  delete query.page  // 重置页码
  
  // 添加新的筛选参数
  if (params.name) query.keyword = params.name
  if (params.categoryId) query.categoryId = params.categoryId.toString()
  if (params.sortBy) query.sortBy = params.sortBy
  if (params.sortOrder) query.sortOrder = params.sortOrder
  if (params.minPrice) query.minPrice = params.minPrice.toString()
  if (params.maxPrice) query.maxPrice = params.maxPrice.toString()
  
  // 强制路由更新
  router.replace({ query })
}

// 事件处理方法
const handleSearch = () => {
  const params = buildFilterParams()
  updateQuery(params)
  emit('search', params)
}

const handleFilter = () => {
  const params = buildFilterParams()
  updateQuery(params)
  emit('filter', params)
}

const handleSort = () => {
  const params = buildFilterParams()
  updateQuery(params)
  emit('sort', params)
}

const handlePriceFilter = () => {
  // 延迟执行，避免频繁触发
  setTimeout(() => {
    handleFilter()
  }, 500)
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedCategoryId.value = null
  sortOption.value = 'default'
  priceRange.value = { min: null, max: null }
  
  // 清除URL查询参数
  const query = { ...route.query }
  delete query.keyword
  delete query.categoryId
  delete query.sortBy
  delete query.sortOrder
  delete query.minPrice
  delete query.maxPrice
  delete query.page  // 重置页码
  
  router.replace({ query })
  emit('reset')
}

// 清除单个筛选条件
const clearSearchKeyword = () => {
  searchKeyword.value = ''
  handleFilter()
}

const clearCategory = () => {
  selectedCategoryId.value = null
  handleFilter()
}

const clearSort = () => {
  sortOption.value = 'default'
  handleSort()
}

const clearPriceRange = () => {
  priceRange.value = { min: null, max: null }
  handleFilter()
}

// 监听路由变化
watch(() => route.query, () => {
  initFromQuery()
}, { immediate: true })

// 组件挂载时初始化
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.snack-search-bar {
  margin-bottom: 2rem;
}

.search-card {
  border-radius: 18px;

  border: 1px solid rgba(255, 193, 7, 0.16);
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
}

.search-content {
  padding: 0.5rem;
}

.search-input-section {
  margin-bottom: 1.5rem;
}

.custom-input {
  display: flex;
  align-items: stretch;
  gap: 0;
  border: 1px solid rgba(255, 193, 7, 0.4);
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  border-radius: 14px;
  overflow: hidden;
}

.custom-input__inner {
  flex: 1;
  border: none;
  outline: none;
  padding: 12px 14px 12px 38px;
  font-size: 14px;
  background: transparent;
  color: #5D4037;
}

.custom-input__inner::placeholder { color: #9E9E9E; }

.input-prefix {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9E9E9E;
}

.custom-input { position: relative; }

.custom-input__append {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.filter-section {
  margin-bottom: 1rem;
}

.filter-item {
  margin-bottom: 1rem;
}

.filter-label {
  display: none;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-separator {
  color: #666;
  font-weight: bold;
}

.filter-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

/* 让筛选列与右侧操作按钮垂直居中对齐 */
:deep(.filter-section .el-row) {
  align-items: center;
}

@media (min-width: 769px) {
  .filter-item { margin-bottom: 0; }
}

/* 统一 Element Plus 下拉选择为糖果风样式 */
:deep(.el-select .el-input__wrapper) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 193, 7, 0.28);
}

:deep(.el-select .el-input__wrapper:hover),
:deep(.el-select.is-focus .el-input__wrapper),
:deep(.el-select .is-focus .el-input__wrapper) {
  border-color: #FF8F00;
  box-shadow: 0 0 0 2px rgba(255, 143, 0, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

:deep(.el-select .el-input__inner::placeholder) { color: #9E9E9E; }

:deep(.el-select .el-input__suffix .el-icon) { color: #FF8F00; }

:deep(.el-select-dropdown) {
  border-radius: 12px;
  border: 1px solid rgba(255, 193, 7, 0.18);
  box-shadow: 0 12px 24px rgba(255, 171, 64, 0.16);
}

:deep(.el-select-dropdown__item) {
  border-radius: 8px;
}

:deep(.el-select-dropdown__item:hover) {
  background: rgba(255, 193, 7, 0.12);
  color: #E65100;
}

/* 价格区间小输入框糖果风样式 */
:deep(.price-range .el-input__wrapper) {
  border-radius: 12px;
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  border: 1px solid rgba(255, 193, 7, 0.28);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  min-height: 36px;
}

:deep(.price-range .el-input__wrapper:hover),
:deep(.price-range .is-focus .el-input__wrapper) {
  border-color: #FF8F00;
  box-shadow: 0 0 0 2px rgba(255, 143, 0, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

:deep(.price-range .el-input__inner::placeholder) {
  color: #9E9E9E;
}

.active-filters {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 193, 7, 0.16);
}

.filter-title {
  font-size: 0.9rem;
  color: #6D4C41;
  font-weight: 500;
}

/* 输入框与主按钮轻度主题增强 */
:deep(.el-input__wrapper) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);

}

:deep(.el-button.el-button--primary) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  color: #FFF7E6;

}
:deep(.el-button.el-button--primary:hover) {
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  color: #FFF7E6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-content {
    padding: 0.5rem;
  }
  
  .filter-actions {
    padding-top: 1rem;
    justify-content: center;
  }
  
  .filter-actions .el-button {
    flex: 1;
  }
  
  .price-range {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .price-range .el-input {
    width: 100% !important;
  }
  
  .price-separator {
    display: none;
  }
  
  .active-filters {
    justify-content: center;
  }
}
</style>
