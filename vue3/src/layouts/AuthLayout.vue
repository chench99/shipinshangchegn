<template>
  <div class="auth-layout">
    <!-- 背景装饰元素（软糖/果冻气泡） -->
    <div class="bg-blob blob1" aria-hidden="true"></div>
    <div class="bg-blob blob2" aria-hidden="true"></div>
    <div class="bg-blob blob3" aria-hidden="true"></div>
    <div class="auth-container">
      <div class="auth-header">
        <div class="logo-badge">🍪</div>
        <h1 class="brand-title">{{ siteName }}</h1>
        <p>{{ siteDescription }}</p>
      </div>

      <div class="auth-content">
        <router-view />
      </div>

      <div class="auth-footer">
        <p>&copy; 2025 {{ siteName }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 站点配置，可以通过props传入或从配置文件读取
const siteName = ref('预制菜商城')
const siteDescription = ref('美味食品，一站购齐，享受生活每一刻')
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(1200px 800px at 20% 10%, #FFF8E1 0%, rgba(255, 248, 225, 0.6) 55%, rgba(255, 248, 225, 0) 60%),
              radial-gradient(1000px 700px at 90% 80%, #FFE0B2 0%, rgba(255, 224, 178, 0.5) 50%, rgba(255, 224, 178, 0) 60%),
              linear-gradient(135deg, #FFF8E1 0%, #FFE0B2 100%);
  position: relative;
  padding: 24px;
}

.auth-container {
  max-width: 400px;
  width: 100%;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border-radius: 20px;
  box-shadow: 12px 12px 40px rgba(255, 152, 0, 0.15),
              -8px -8px 24px rgba(255, 255, 255, 0.8),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
  backdrop-filter: saturate(120%) blur(2px);
  position: relative;
  overflow: hidden;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-badge {
  width: 56px;
  height: 56px;
  margin: 0 auto 10px auto;
  border-radius: 18px;
  display: grid;
  place-items: center;
  font-size: 28px;
  background: linear-gradient(145deg, #FFD180, #FFAB40);
  box-shadow: 0 8px 18px rgba(255, 171, 64, 0.35), inset 0 2px 0 rgba(255, 255, 255, 0.6);
  transform: translateZ(0);
  animation: jelly 2.2s ease-in-out infinite;
}

.auth-header h1,
.brand-title {
  margin: 0 0 0.5rem 0;
  color: #FF8F00;
  font-weight: 600;
  font-size: 1.9rem;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.6);
}

.auth-header p {
  margin: 0;
  color: #FF6F00;
  font-size: 0.9rem;
  opacity: 0.8;
}

.auth-content {
  margin-bottom: 2rem;
}

.auth-footer {
  text-align: center;
  color: #FFB74D;
  font-size: 0.8rem;
}

/* 背景软糖气泡 */
.bg-blob {
  position: absolute;
  filter: blur(10px);
  opacity: 0.6;
  border-radius: 40px;
  transform: translateZ(0);
}
.blob1 {
  width: 180px;
  height: 180px;
  left: 30px;
  top: 40px;
  background: radial-gradient(circle at 30% 30%, #FFD180, #FFB74D);
  animation: float 9s ease-in-out infinite;
}
.blob2 {
  width: 240px;
  height: 240px;
  right: 20px;
  bottom: 60px;
  background: radial-gradient(circle at 30% 30%, #FFE082, #FFCC80);
  animation: float 11s ease-in-out infinite reverse;
}
.blob3 {
  width: 120px;
  height: 120px;
  right: 120px;
  top: 100px;
  background: radial-gradient(circle at 30% 30%, #FFCC80, #FFAB40);
  animation: float 13s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) translateZ(0) scale(1); }
  50% { transform: translateY(-12px) translateZ(0) scale(1.02); }
}

@keyframes jelly {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.04, 0.96); }
  50% { transform: scale(0.98, 1.02); }
  75% { transform: scale(1.02, 0.98); }
}

/* 统一表单内部控件的柔和质感与圆角（通过深选择器作用于子组件） */
:deep(.auth-form .form-header h2) {
  color: #D2691E;
  font-weight: 700;
  letter-spacing: 0.3px;
}

:deep(.auth-form .form-header p) {
  color: #8D6E63;
  opacity: 0.9;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-input__wrapper) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFFFFF, #FFF3E0);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8),
              0 4px 14px rgba(255, 171, 64, 0.15);
  border: 1px solid rgba(255, 171, 64, 0.25);
  transition: box-shadow 0.2s ease, transform 0.2s ease, border-color 0.2s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9),
              0 6px 18px rgba(255, 171, 64, 0.28);
  border-color: rgba(255, 143, 0, 0.45);
  transform: translateY(-1px);
}

:deep(.el-input__inner::placeholder) {
  color: rgba(141, 110, 99, 0.7);
}

:deep(.el-button.el-button--primary) {
  border-radius: 14px;
  background: linear-gradient(145deg, #FFB74D, #FF8F00);
  border-color: #FF8F00;
  box-shadow: 0 8px 18px rgba(255, 143, 0, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.5);
  transition: transform 0.12s ease, box-shadow 0.12s ease, filter 0.12s ease;
}

:deep(.el-button.el-button--primary:hover) {
  filter: brightness(1.03);
  transform: translateY(-1px);
  box-shadow: 0 12px 22px rgba(255, 143, 0, 0.45), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

:deep(.el-button.el-button--primary:active) {
  transform: translateY(0);
  box-shadow: 0 6px 14px rgba(255, 143, 0, 0.35) inset;
}

:deep(.auth-form .form-links a) {
  color: #FF8F00;
}

:deep(.auth-form .form-links a:hover) {
  color: #FF6F00;
}
</style>
