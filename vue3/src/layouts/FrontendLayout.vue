<template>
    <div class="frontend-layout">
      <div class="bg-blob blob-a" aria-hidden="true"></div>
      <div class="bg-blob blob-b" aria-hidden="true"></div>
      <div class="bg-blob blob-c" aria-hidden="true"></div>
      <!-- 顶部导航栏 -->
      <header class="header">
        <nav>
          <router-link to="/" class="brand">🍩 预制菜商城</router-link>
          <router-link to="/snacks">食品列表</router-link>
          
          <!-- 购物车链接（仅登录用户可见） -->
          <router-link to="/cart" v-if="isLoggedIn" c ass="cart-link">
            <i class="fas fa-shopping-cart"></i>
            购物车
            <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
          </router-link>

          <!-- 我的订单链接（仅登录用户可见） -->
          <router-link to="/order/list" v-if="isLoggedIn" class="order-link">
            <i class="fas fa-list-alt"></i>
            我的订单
          </router-link>

          <!-- 我的收藏链接（仅登录用户可见） -->
          <router-link to="/favorites" v-if="isLoggedIn" class="favorite-link">
            <i class="fas fa-heart"></i>
            我的收藏
            <span v-if="favoriteCount > 0" class="favorite-badge">{{ favoriteCount }}</span>
          </router-link>

          <!-- 收货地址链接（仅登录用户可见） -->
          <router-link to="/profile/address" v-if="isLoggedIn" class="address-link">
            <i class="fas fa-map-marker-alt"></i>
            收货地址
          </router-link>
    
          <router-link to="/profile" v-if="isLoggedIn">个人中心</router-link>
          <button v-if="isLoggedIn" class="nav-button outline" @click="handleLogout">退出登录</button>
          <router-link v-else to="/auth/login" class="nav-button primary">登录</router-link>
        </nav>
      </header>
  
      <!-- 主要内容区域 -->
      <main class="main-content">
        <router-view />
      </main>
  
      <!-- 页脚 -->
      <footer class="footer">
        <p>&copy; 2025 预制菜商城</p>
      </footer>
    </div>
  </template>
  
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { getCartItemCount } from '@/api/cart'
import { getFavoriteCount } from '@/api/favorite'

const userStore = useUserStore()
const router = useRouter()

const isLoggedIn = computed(() => !!userStore.token)
const cartItemCount = ref(0)
const favoriteCount = ref(0)

const handleLogout = () => {
  userStore.clearUserInfo()
  router.push('/auth/login')
}

// 获取购物车商品数量
const fetchCartItemCount = () => {
  if (!isLoggedIn.value) {
    cartItemCount.value = 0
    return
  }
  
  getCartItemCount({
    onSuccess: (count) => {
      cartItemCount.value = count || 0
    },
    onError: (error) => {
      console.error('获取购物车数量失败:', error)
      cartItemCount.value = 0
    }
  })
}

// 获取收藏商品数量
const fetchFavoriteCount = () => {
  if (!isLoggedIn.value) {
    favoriteCount.value = 0
    return
  }
  
  getFavoriteCount({
    onSuccess: (count) => {
      favoriteCount.value = count || 0
    },
    onError: (error) => {
      console.error('获取收藏数量失败:', error)
      favoriteCount.value = 0
    }
  })
}

// 监听登录状态变化，更新购物车和收藏数量
const unwatchLogin = computed(() => {
  if (isLoggedIn.value) {
    fetchCartItemCount()
    fetchFavoriteCount()
  } else {
    cartItemCount.value = 0
    favoriteCount.value = 0
  }
})

onMounted(() => {
  fetchCartItemCount()
  fetchFavoriteCount()
})
</script>
  
  <style scoped>
  .frontend-layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    position: relative;
    background:
      radial-gradient(1000px 700px at 10% 0%, rgba(255, 248, 225, .9) 0%, rgba(255, 248, 225, .5) 55%, rgba(255, 248, 225, 0) 60%),
      linear-gradient(180deg, #FFF8E1 0%, #FFE0B2 100%);
  }
  
  .header {
    color: #5D4037;
    padding: 12px 20px;
    position: sticky;
    top: 0;
    z-index: 10;
    backdrop-filter: saturate(120%) blur(4px);
    background: rgba(255, 255, 255, 0.65);
    box-shadow: 0 8px 24px rgba(255, 171, 64, 0.15);
  }
  
  .header nav {
    display: flex;
    gap: 1rem;
    align-items: center;
  }
  
  .header a {
    color: #6D4C41;
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 8px 12px;
    border-radius: 12px;
    transition: transform .12s ease, background-color .12s ease, color .12s ease;
  }

  .header a.brand {
    font-weight: 700;
    color: #FF8F00;
    background: linear-gradient(145deg, #FFF3E0, #FFFFFF);
    box-shadow: 0 2px 0 rgba(255,255,255,.8) inset, 0 6px 16px rgba(255, 171, 64, .18);
  }

  .cart-link {
    position: relative;
  }

  .cart-badge, .favorite-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background-color: #FF6F00;
    color: white;
    border-radius: 50%;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.75rem;
    font-weight: bold;
    min-width: 20px;
    box-shadow: 0 6px 14px rgba(255, 111, 0, .35);
    animation: pulse 1.8s ease-in-out infinite;
  }

  .favorite-badge {
    background-color: #F44336;
    box-shadow: 0 6px 14px rgba(244, 67, 54, .35);
  }

  .favorite-link {
    position: relative;
  }
  
  .main-content {
    flex: 1;
    padding: 1rem;
  }
  
  .footer {
    background: linear-gradient(145deg, #FFF3E0, #FFE0B2);
    color: #6D4C41;
    text-align: center;
    padding: 1rem;
    border-top: 1px solid rgba(255, 171, 64, .25);
    box-shadow: 0 -6px 18px rgba(255, 171, 64, .12);
  }

  .header a:hover {
    background: linear-gradient(145deg, #FFECB3, #FFE0B2);
    color: #5D4037;
    transform: translateY(-1px);
  }

  /* 导航按钮样式（登录/退出） */
  .nav-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 8px 14px;
    border-radius: 14px;
    border: 1px solid rgba(255, 171, 64, .35);
    cursor: pointer;
    transition: transform .12s ease, box-shadow .12s ease, filter .12s ease, background-color .12s ease, color .12s ease;
  }

  .nav-button.primary {
    background: linear-gradient(145deg, #FFB74D, #FF8F00);
    color: #fff;
    border-color: #FF8F00;
    box-shadow: 0 10px 20px rgba(255, 143, 0, .28), inset 0 1px 0 rgba(255,255,255,.5);
  }

  .nav-button.primary:hover {
    filter: brightness(1.03);
    transform: translateY(-1px);
    box-shadow: 0 14px 24px rgba(255, 143, 0, .38), inset 0 1px 0 rgba(255,255,255,.6);
  }

  .nav-button.outline {
    background: linear-gradient(145deg, #FFFCF6, #FFF3E0);
    color: #6D4C41;
  }

  .nav-button.outline:hover {
    background: linear-gradient(145deg, #FFECB3, #FFE0B2);
    transform: translateY(-1px);
  }

  /* 背景装饰软糖气泡 */
  .bg-blob {
    position: absolute;
    filter: blur(12px);
    opacity: 0.5;
    border-radius: 40px;
    z-index: 0;
    pointer-events: none;
  }
  .blob-a { width: 240px; height: 240px; left: -40px; top: 80px; background: radial-gradient(circle at 30% 30%, #FFD180, #FFAB40); animation: float 12s ease-in-out infinite; }
  .blob-b { width: 180px; height: 180px; right: 40px; top: 20px; background: radial-gradient(circle at 30% 30%, #FFE082, #FFCC80); animation: float 14s ease-in-out infinite reverse; }
  .blob-c { width: 160px; height: 160px; right: 100px; bottom: 120px; background: radial-gradient(circle at 30% 30%, #FFCC80, #FFB74D); animation: float 16s ease-in-out infinite; }

  @keyframes float {
    0%, 100% { transform: translateY(0) scale(1); }
    50% { transform: translateY(-10px) scale(1.02); }
  }

  @keyframes pulse {
    0%, 100% { transform: translateZ(0) scale(1); }
    50% { transform: translateZ(0) scale(1.06); }
  }
  </style>