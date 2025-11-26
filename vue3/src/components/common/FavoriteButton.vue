<template>
  <button
    class="favorite-btn"
    :class="[
      `favorite-btn--${size}`,
      { 'is-favorited': isFavorited, 'is-loading': loading, 'has-text': hasTextOrCount }
    ]"
    :disabled="loading"
    aria-pressed="isFavorited ? 'true' : 'false'"
    :aria-busy="loading ? 'true' : 'false'"
    @click="handleToggleFavorite"
  >
    <span v-if="loading" class="fav-spinner" aria-hidden="true"></span>
    <i 
      :class="[
        'heart-icon',
        isFavorited ? 'fas fa-heart' : 'far fa-heart',
        { favorited: isFavorited }
      ]"
      aria-hidden="true"
    ></i>
    <span v-if="showText">{{ isFavorited ? '已收藏' : '收藏' }}</span>
    <span v-if="showCount && favoriteCount > 0" class="count-text">
      ({{ favoriteCount }})
    </span>
  </button>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { toggleFavorite } from '@/api/favorite'

const props = defineProps({
  snackId: {
    type: Number,
    required: true
  },
  initialFavorited: {
    type: Boolean,
    default: false
  },
  initialCount: {
    type: Number,
    default: 0
  },
  size: {
    type: String,
    default: 'default'
  },
  showText: {
    type: Boolean,
    default: true
  },
  showCount: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['favoriteChanged'])
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const isFavorited = ref(props.initialFavorited)
const favoriteCount = ref(props.initialCount)

// 是否显示文字或数量，用于样式控制心形图标的边距
const hasTextOrCount = computed(() => props.showText || (props.showCount && favoriteCount.value > 0))

// 监听初始值变化
watch(() => props.initialFavorited, (newVal) => {
  isFavorited.value = newVal
})

watch(() => props.initialCount, (newVal) => {
  favoriteCount.value = newVal
})

// 切换收藏状态
const handleToggleFavorite = () => {
  if (loading.value) return
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/auth/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  
  loading.value = true
  toggleFavorite({ snackId: props.snackId }, {
    onSuccess: (res) => {
      isFavorited.value = res.isFavorited
      favoriteCount.value = res.favoriteCount
      
      const message = res.isFavorited ? '收藏成功' : '取消收藏成功'
      ElMessage.success(message)
      
      // 通知父组件收藏状态变化
      emit('favoriteChanged', {
        snackId: props.snackId,
        isFavorited: res.isFavorited,
        favoriteCount: res.favoriteCount
      })
      
      loading.value = false
    },
    onError: (error) => {
      console.error('切换收藏状态失败:', error)
      ElMessage.error('操作失败，请重试')
      loading.value = false
    }
  })
}
</script>

<style scoped>
.favorite-btn {
  -webkit-tap-highlight-color: transparent;
  appearance: none;
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  color: #666;
  padding: 12px 18px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.favorite-btn:not(.is-favorited):hover {
  background: linear-gradient(145deg, #FFF7E6, #FFE0B2);
  border-color: rgba(255, 193, 7, 0.5);
  color: #E65100;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 143, 0, 0.2);
}

.favorite-btn.is-favorited {
  background: linear-gradient(145deg, #FFE0E1, #FFCDD2);
  border-color: #F44336;
  color: #D32F2F;
}

.favorite-btn.is-favorited:hover {
  background: linear-gradient(145deg, #FFCDD2, #FFBCBC);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

.heart-icon {
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1em;
  height: 1em;
}

.heart-icon.favorited {
  color: #F44336;
}

.count-text {
  font-size: 0.85em;
  margin-left: 0.25rem;
  opacity: 0.8;
}

/* 尺寸 */
.favorite-btn--small { padding: 8px 14px; font-size: 13px; }
.favorite-btn--default { padding: 12px 18px; font-size: 14px; }
.favorite-btn--large { padding: 14px 22px; font-size: 16px; }

/* 加载状态 */
.favorite-btn.is-loading { cursor: progress; opacity: 0.85; }
.fav-spinner { width: 1em; height: 1em; border: 2px solid rgba(0,0,0,0.15); border-top-color: currentColor; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* 移除心跳动画（保留占位，便于未来扩展） */

/* 加载状态优化 */
.favorite-btn.is-loading {
  pointer-events: none;
}

/* 小尺寸适配 */
.favorite-btn--small .heart-icon { font-size: 14px; }

.favorite-btn--small .count-text {
  font-size: 0.8em;
}

/* 大尺寸适配 */
.favorite-btn--large .heart-icon { font-size: 18px; }

.favorite-btn--large .count-text {
  font-size: 0.9em;
}

/* 仅在有文字/数量时才给心形右侧留间距，确保内容水平居中 */
.favorite-btn.has-text .heart-icon { margin-right: 0.5rem; }
.favorite-btn.favorite-btn--small.has-text .heart-icon { margin-right: 0.25rem; }
.favorite-btn.favorite-btn--large.has-text .heart-icon { margin-right: 0.75rem; }
</style>
