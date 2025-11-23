<template>
  <div class="user-layout">
    <!-- 背景装饰元素 -->
    <div class="background-elements">
      <div class="decoration-shape shape-1"></div>
      <div class="decoration-shape shape-2"></div>
      <div class="decoration-shape shape-3"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="user-header">
      <div class="header-container">
        <div class="header-content">
          <el-button 
            type="primary" 
            link 
            @click="$router.push('/')" 
            class="back-home-btn"
          >
            <div class="btn-content">
              <el-icon class="btn-icon"><ArrowLeft /></el-icon>
              <span class="btn-text">返回首页</span>
            </div>
          </el-button>
          
          <div class="header-main">
            <div class="page-info">
              <el-icon class="title-icon"><User /></el-icon>
              <h1 class="page-title">个人中心</h1>
              <p class="page-subtitle">管理您的账户信息和订单</p>
            </div>
            
            <div class="header-actions">
              <el-button 
                type="primary" 
                link 
                @click="$router.push('/cart')" 
                class="action-btn"
              >
                <el-icon><ShoppingCart /></el-icon>
                购物车
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="user-container">
      <!-- 侧边栏导航 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <div class="user-quick-info">
            <el-avatar :size="50" class="user-avatar">
              {{ userStore.userInfo?.username?.charAt(0) || '用户' }}
            </el-avatar>
            <div class="user-details">
              <h3 class="username">{{ userStore.userInfo?.username || '用户' }}</h3>
              <p class="user-level">普通会员</p>
            </div>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          router
          class="user-menu"
        >
          <el-menu-item index="/user/profile" class="menu-item">
            <div class="menu-item-content">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><User /></el-icon>
              </div>
              <span class="menu-text">个人中心</span>
            </div>
            <div class="menu-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </el-menu-item>

          <el-menu-item index="/user/addresses" class="menu-item">
            <div class="menu-item-content">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Location /></el-icon>
              </div>
              <span class="menu-text">收货地址</span>
            </div>
            <div class="menu-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </el-menu-item>

          <el-menu-item index="/user/orders" class="menu-item">
            <div class="menu-item-content">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Document /></el-icon>
              </div>
              <span class="menu-text">我的订单</span>
            </div>
            <div class="menu-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </el-menu-item>

          <!-- 新增菜单项 -->
          <el-menu-item index="/user/favorites" class="menu-item">
            <div class="menu-item-content">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Star /></el-icon>
              </div>
              <span class="menu-text">我的收藏</span>
            </div>
            <div class="menu-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </el-menu-item>

          <el-menu-item index="/user/security" class="menu-item">
            <div class="menu-item-content">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Lock /></el-icon>
              </div>
              <span class="menu-text">安全设置</span>
            </div>
            <div class="menu-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
          <div class="quick-stats">
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">待付款</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">待收货</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">待评价</div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="main-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  User, 
  Location, 
  Document, 
  ArrowLeft, 
  ArrowRight,
  ShoppingCart,
  Star,
  Lock
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

.background-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.decoration-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  top: 70%;
  right: 8%;
  animation-delay: 2.5s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 15%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-15px) scale(1.05);
  }
}

/* 头部导航栏样式 */
.user-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px 0;
}

.back-home-btn {
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.back-home-btn:hover {
  background: rgba(64, 158, 255, 0.1);
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-icon {
  font-size: 16px;
}

.btn-text {
  font-weight: 500;
}

.header-main {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 28px;
  color: #409eff;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  margin-left: 12px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
}

/* 主容器布局 */
.user-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 120px);
}

/* 侧边栏样式 */
.sidebar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.user-quick-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: white;
  font-weight: 600;
  border: 3px solid #e8f4ff;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.user-level {
  margin: 0;
  font-size: 12px;
  color: #409eff;
  background: #e8f4ff;
  padding: 2px 8px;
  border-radius: 8px;
  display: inline-block;
}

/* 菜单样式 */
.user-menu {
  flex: 1;
  border: none;
  padding: 16px 0;
}

:deep(.user-menu .el-menu-item) {
  height: 56px;
  margin: 4px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
}

:deep(.user-menu .el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.08);
  transform: translateX(4px);
}

:deep(.user-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: white;
}

:deep(.user-menu .el-menu-item.is-active .menu-icon) {
  color: white;
}

:deep(.user-menu .el-menu-item.is-active .menu-arrow) {
  color: white;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.menu-icon-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(64, 158, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon {
  font-size: 16px;
  color: #409eff;
}

.menu-text {
  font-weight: 500;
  font-size: 14px;
}

.menu-arrow {
  opacity: 0.6;
  transition: all 0.3s ease;
}

:deep(.user-menu .el-menu-item:hover .menu-arrow) {
  opacity: 1;
  transform: translateX(2px);
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 20px 16px;
  border-top: 1px solid #f0f0f0;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-item:hover {
  background: #e8f4ff;
  transform: translateY(-2px);
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 11px;
  color: #6b7280;
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.content-wrapper {
  height: 100%;
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .user-container {
    grid-template-columns: 250px 1fr;
    gap: 20px;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .user-container {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 16px;
  }
  
  .sidebar {
    order: 2;
  }
  
  .main-content {
    order: 1;
  }
  
  .header-content {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .header-main {
    width: 100%;
  }
  
  .page-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .page-subtitle {
    margin-left: 0;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 16px;
  }
  
  .user-container {
    padding: 12px;
  }
  
  .sidebar-header {
    padding: 20px 16px;
  }
  
  .quick-stats {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .stat-item {
    padding: 10px 6px;
  }
  
  .stat-number {
    font-size: 16px;
  }
  
  .stat-label {
    font-size: 10px;
  }
}
</style>