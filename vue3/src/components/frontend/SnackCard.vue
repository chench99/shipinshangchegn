<template>
  <div class="snack-card" @click="$emit('click', snack)">
    <div class="snack-image">
      <img 
        :src="snack.coverImage || '/default-snack.jpg'" 
        :alt="snack.name"
        @error="handleImageError"
      />
      <div v-if="!snack.hasStock" class="stock-overlay">
        <span>缺货</span>
      </div>
      <div class="category-tag">
        {{ snack.categoryName }}
      </div>
    </div>
    <div class="snack-info">
      <div class="snack-header">
        <h3 class="snack-name">{{ snack.name }}</h3>
        <div class="snack-header-extra" @click.stop>
          <slot name="header-right"></slot>
        </div>
      </div>
      <p class="snack-description">{{ truncateText(snack.description, maxDescLength) }}</p>
      <div class="snack-meta">
        <span class="snack-price">¥{{ snack.price }}</span>
        <span class="snack-sales">已售{{ snack.salesCount }}件</span>
      </div>
      <div class="snack-actions">
        <CandyButton 
          size="small"
          :disabled="!snack.canPurchase"
          :require-auth="true"
          @click.stop="handleAddToCart"
        >
          加入购物车
        </CandyButton>
        <CandyOutlineButton 
          size="small"
          @click.stop="handleViewDetail"
        >
          查看详情
        </CandyOutlineButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'

// 定义props
const props = defineProps({
  snack: {
    type: Object,
    required: true
  },
  maxDescLength: {
    type: Number,
    default: 50
  }
})

// 定义emits
const emit = defineEmits(['click', 'add-to-cart', 'view-detail'])

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

// 加入购物车
const handleAddToCart = () => {
  if (props.snack.canPurchase) {
    emit('add-to-cart', props.snack)
  }
}

// 查看详情
const handleViewDetail = () => {
  emit('view-detail', props.snack)
}
</script>

<style scoped>

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
  height: 100%;
  display: flex;
  flex-direction: column;
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

.category-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  color: white;
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.snack-info {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.snack-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
}

.snack-name {
  font-size: 1.2rem;
  font-weight: 800;
  color: #D2691E;
  margin: 0 0 0.5rem 0;
}

.snack-description {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 1rem 0;
  line-height: 1.4;
  flex: 1;
}

.snack-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.snack-price {
  font-size: 1.3rem;
  font-weight: 800;
  color: #E65100;
}

.snack-sales {
  font-size: 0.9rem;
  color: #666;
}

.snack-actions {
  display: flex;
  gap: 0.5rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .snack-image {
    height: 150px;
  }
  
  .snack-info {
    padding: 1rem;
  }
  
  .snack-name {
    font-size: 1rem;
  }
  
  .snack-description {
    font-size: 0.8rem;
  }
  
  .snack-price {
    font-size: 1.1rem;
  }
  
  .snack-actions {
    flex-direction: column;
  }
}
</style>
