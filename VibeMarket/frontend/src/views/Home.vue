<template>
  <div class="home-container">
    <!-- 背景装饰元素 -->
    <div class="background-elements">
      <div class="decoration-shape shape-1"></div>
      <div class="decoration-shape shape-2"></div>
      <div class="decoration-shape shape-3"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="main-header">
      <div class="header-container">
        <div class="brand-section">
          <div class="logo" @click="$router.push('/')">
            <div class="logo-icon">
              <!-- 自定义图标 -->
              <img src="/logo.svg" alt="校园购物商城" class="logo-image">
            </div>
            <div class="brand-text">
              <h1 class="brand-name">校园购物商城</h1>
              <p class="brand-subtitle">Campus Shopping Mall</p>
            </div>
          </div>
        </div>

        <div class="header-actions">
          <template v-if="!userStore.isLoggedIn && !adminStore.isLoggedIn">
            <el-button 
              @click="$router.push('/login')" 
              class="nav-btn login-btn"
            >
              <i class="el-icon-user"></i>
              登录
            </el-button>
            <el-button 
              type="primary" 
              @click="$router.push('/register')"
              class="nav-btn register-btn"
            >
              注册
            </el-button>
            <el-button 
              @click="$router.push('/admin/login')" 
              class="nav-btn admin-btn"
            >
              <i class="el-icon-s-custom"></i>
              管理员登录
            </el-button>
          </template>
          
          <!-- 管理员登录状态 -->
          <el-dropdown 
            v-else-if="adminStore.isLoggedIn" 
            class="user-dropdown admin-dropdown"
            trigger="click"
            :hide-on-click="false"
          >
            <span class="user-info admin-info">
              <el-avatar :size="36" class="user-avatar admin-avatar">
                {{ adminStore.adminInfo?.username?.charAt(0) || '管' }}
              </el-avatar>
              <div class="user-details">
                <span class="username">{{ adminStore.adminInfo?.username || '管理员' }}</span>
                <span class="user-role">管理员</span>
              </div>
              <i class="el-icon-arrow-down dropdown-arrow"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu admin-menu">
                <el-dropdown-item 
                  @click="$router.push('/admin/dashboard')"
                  class="dropdown-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-s-tools menu-icon"></i>
                    <span class="menu-text">管理后台</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="$router.push('/user/profile')"
                  class="dropdown-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-user menu-icon"></i>
                    <span class="menu-text">个人中心</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item 
                  divided 
                  @click="handleAdminLogout"
                  class="dropdown-item logout-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-switch-button menu-icon"></i>
                    <span class="menu-text">退出登录</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 用户登录状态 -->
          <el-dropdown 
            v-else 
            class="user-dropdown"
            trigger="click"
            :hide-on-click="false"
          >
            <span class="user-info">
              <el-avatar :size="36" class="user-avatar">
                {{ userStore.userInfo?.username?.charAt(0) || '用' }}
              </el-avatar>
              <div class="user-details">
                <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
                <span class="user-role">普通会员</span>
              </div>
              <i class="el-icon-arrow-down dropdown-arrow"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu">
                <el-dropdown-item 
                  @click="$router.push('/user/profile')"
                  class="dropdown-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-user menu-icon"></i>
                    <span class="menu-text">个人中心</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="$router.push('/cart')"
                  class="dropdown-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-shopping-cart-full menu-icon"></i>
                    <span class="menu-text">购物车</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="$router.push('/user/orders')"
                  class="dropdown-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-document menu-icon"></i>
                    <span class="menu-text">我的订单</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item 
                  divided 
                  @click="handleLogout"
                  class="dropdown-item logout-item"
                >
                  <div class="menu-item-content">
                    <i class="el-icon-switch-button menu-icon"></i>
                    <span class="menu-text">退出登录</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main-content">
      <!-- 英雄区域 -->
      <section class="hero-section">
        <div class="hero-container">
          <div class="hero-content">
            <div class="hero-text">
              <h1 class="hero-title">
                发现校园
                <span class="highlight">优质好物</span>
              </h1>
              <p class="hero-description">
                专为校园生活打造的购物平台，精选学习用品、生活必备、电子产品等，
                让您的校园生活更便捷、更精彩
              </p>
              <div class="hero-actions">
                <el-button 
                  type="primary" 
                  size="large"
                  @click="$router.push('/products')"
                  class="hero-btn primary"
                >
                  <i class="el-icon-shopping-cart-full"></i>
                  立即购物
                </el-button>
                <el-button 
                  size="large"
                  @click="$router.push('/products')"
                  class="hero-btn secondary"
                >
                  <i class="el-icon-menu"></i>
                  浏览分类
                </el-button>
              </div>
              <div class="hero-stats">
                <div class="stat-item">
                  <div class="stat-number">1000+</div>
                  <div class="stat-label">优质商品</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">5000+</div>
                  <div class="stat-label">满意用户</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">99%</div>
                  <div class="stat-label">正品保障</div>
                </div>
              </div>
            </div>
            <div class="hero-visual">
              <div class="floating-cards">
                <div class="card card-1">
                  <i class="el-icon-notebook-2"></i>
                  <span>学习用品</span>
                </div>
                <div class="card card-2">
                  <i class="el-icon-mobile-phone"></i>
                  <span>电子产品</span>
                </div>
                <div class="card card-3">
                  <i class="el-icon-office-building"></i>
                  <span>生活用品</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 分类展示 -->
      <section v-if="categories.length > 0" class="categories-section">
        <div class="section-container">
          <div class="section-header">
            <div class="header-content">
              <h2 class="section-title">
                <i class="el-icon-menu"></i>
                热门分类
              </h2>
              <p class="section-subtitle">精选商品分类，快速找到您需要的商品</p>
            </div>
            <el-button 
              link 
              @click="$router.push('/products')" 
              class="view-all-btn"
            >
              查看全部
              <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          
          <div class="categories-grid">
            <div 
              v-for="category in categories" 
              :key="category.id" 
              class="category-card"
              @click="handleCategoryClick(category)"
            >
              <div class="category-content">
                <div class="category-icon">
                  <i :class="category.icon || 'el-icon-goods'"></i>
                </div>
                <div class="category-info">
                  <h3 class="category-name">{{ category.name }}</h3>
                  <p class="category-desc">精选好物 · 品质保证</p>
                </div>
                <div class="category-arrow">
                  <i class="el-icon-arrow-right"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 推荐商品 -->
      <section v-if="recommendedProducts.length > 0" class="products-section">
        <div class="section-container">
          <div class="section-header">
            <div class="header-content">
              <h2 class="section-title">
                <i class="el-icon-star"></i>
                热门推荐
              </h2>
              <p class="section-subtitle">精心挑选的热门商品，品质与性价比兼具</p>
            </div>
            <el-button 
              link 
              @click="$router.push('/products')" 
              class="view-all-btn"
            >
              更多商品
              <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          
          <div class="products-grid">
            <div 
              v-for="product in recommendedProducts" 
              :key="product.id" 
              class="product-card"
              @click="handleProductClick(product)"
            >
              <div class="product-image">
                <el-image
                  :src="product.mainImage || '/default-product.jpg'"
                  fit="cover"
                  class="image"
                >
                  <template #error>
                    <div class="image-error">
                      <i class="el-icon-picture-outline"></i>
                      <span>暂无图片</span>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <div class="product-footer">
                  <div class="price-section">
                    <span class="current-price">¥{{ product.price }}</span>
                  </div>
                  <div class="product-meta">
                    <span class="stock">库存 {{ product.stock }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 欢迎信息 -->
      <section v-if="categories.length === 0 && recommendedProducts.length === 0" class="welcome-section">
        <div class="welcome-container">
          <div class="welcome-content">
            <div class="welcome-visual">
              <div class="welcome-icon">
                <i class="el-icon-shopping-bag-1"></i>
              </div>
            </div>
            <div class="welcome-text">
              <h2>欢迎来到校园购物商城</h2>
              <p>这是一个基于Vue 3 + Spring Boot的校园购物平台，为您提供优质的购物体验</p>
              <div class="welcome-actions">
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="$router.push('/products')"
                  class="welcome-btn primary"
                >
                  <i class="el-icon-shopping-cart-full"></i>
                  开始购物
                </el-button>
                <el-button 
                  size="large" 
                  @click="$router.push('/register')"
                  class="welcome-btn secondary"
                >
                  <i class="el-icon-user"></i>
                  立即注册
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import { categoryApi } from '@/api/category'
import { productApi } from '@/api/product'

const router = useRouter()
const userStore = useUserStore()
const adminStore = useAdminStore()

const categories = ref([])
const recommendedProducts = ref([])

onMounted(() => {
  loadCategories()
  loadRecommendedProducts()
  // 如果已登录但没有用户信息，重新获取
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.getUserInfo()
  }
  // 如果管理员已登录但没有管理员信息，重新获取
  if (adminStore.isLoggedIn && !adminStore.adminInfo) {
    adminStore.getAdminInfo()
  }
})

const loadCategories = async () => {
  try {
    const allCategories = await categoryApi.getAllCategories()
    // 只显示一级分类
    categories.value = allCategories.filter(cat => cat.level === 1).slice(0, 8)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadRecommendedProducts = async () => {
  try {
    const products = await productApi.getProductList()
    // 取前4个作为推荐商品
    recommendedProducts.value = (products || []).slice(0, 4)
  } catch (error) {
    console.error('加载推荐商品失败:', error)
  }
}

const handleCategoryClick = (category) => {
  router.push({ name: 'ProductList', query: { categoryId: category.id } })
}

const handleProductClick = (product) => {
  router.push(`/products/${product.id}`)
}

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出登录')
  location.reload()
}

const handleAdminLogout = async () => {
  await adminStore.logout()
  ElMessage.success('已退出登录')
  location.reload()
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow-x: hidden;
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
    transform: translateY(-20px) scale(1.05);
  }
}

/* 头部导航栏样式 */
.main-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
}

.brand-section .logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

/* 修改后的logo-icon样式 */
.logo-icon {
  width: 40px;
  height: 40px;
  /* background: linear-gradient(135deg, #67c23a, #19be6b); */
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.logo-image {
  width: 32px;
  height: 32px;
  object-fit: contain;
  /*filter: brightness(0) invert(1);  将图标变为白色 */
}

.brand-text h1 {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 11px;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-btn {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-btn {
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border: none;
}

.admin-btn {
  border-color: #e6a23c;
  color: #e6a23c;
}

.admin-btn:hover {
  background: #e6a23c;
  color: white;
}

/* ========== 优化后的下拉框样式 ========== */

/* 下拉框触发器样式优化 */
.user-dropdown {
  margin-left: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.user-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.user-info:hover::before {
  left: 100%;
}

.user-info:hover {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.user-avatar {
  background: linear-gradient(135deg, #67c23a, #19be6b);
  color: white;
  font-weight: 600;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.user-avatar.admin-avatar {
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
}

.user-info:hover .user-avatar {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.user-info:hover .user-avatar.admin-avatar {
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.4);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  position: relative;
  z-index: 1;
}

.username {
  font-weight: 700;
  color: #1f2937;
  font-size: 14px;
  letter-spacing: -0.2px;
}

.user-role {
  font-size: 11px;
  color: #6b7280;
  font-weight: 500;
  background: rgba(107, 114, 128, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
  display: inline-block;
  width: fit-content;
}

.dropdown-arrow {
  color: #9ca3af;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

/* 管理员样式特殊处理 */
.admin-info {
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.1), rgba(245, 108, 108, 0.1));
  border: 1px solid rgba(230, 162, 60, 0.2);
}

.admin-info:hover {
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.15), rgba(245, 108, 108, 0.15));
  border-color: rgba(230, 162, 60, 0.4);
}

/* 下拉菜单样式优化 */
:deep(.custom-dropdown-menu) {
  border: none;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15), 0 4px 12px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
  padding: 8px;
  min-width: 200px;
  animation: dropdownSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

:deep(.admin-menu) {
  background: linear-gradient(135deg, rgba(255, 251, 235, 0.95), rgba(254, 242, 242, 0.95));
  border: 1px solid rgba(230, 162, 60, 0.2);
}

@keyframes dropdownSlideIn {
  from {
    opacity: 0;
    transform: translateY(-8px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

:deep(.custom-dropdown-menu .dropdown-item) {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 12px 16px;
  border-radius: 12px;
  font-weight: 500;
  color: #4b5563;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 2px 0;
  font-size: 14px;
  text-align: left;
  width: 100%;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

:deep(.custom-dropdown-menu .dropdown-item::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(103, 194, 58, 0.1), transparent);
  transition: left 0.5s ease;
}

:deep(.custom-dropdown-menu .dropdown-item:hover::before) {
  left: 100%;
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  position: relative;
  z-index: 1;
}

.menu-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
  color: #67c23a;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

:deep(.custom-dropdown-menu .dropdown-item:hover) {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.1), rgba(25, 190, 107, 0.1));
  color: #67c23a;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
}

:deep(.custom-dropdown-menu .dropdown-item:hover .menu-icon) {
  color: #67c23a;
  transform: scale(1.1);
}

:deep(.custom-dropdown-menu .dropdown-item.logout-item:hover) {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.1), rgba(230, 162, 60, 0.1));
  color: #f56c6c;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.15);
}

:deep(.custom-dropdown-menu .dropdown-item.logout-item:hover .menu-icon) {
  color: #f56c6c;
}

:deep(.custom-dropdown-menu .el-dropdown-menu__item--divided) {
  border-top: 1px solid rgba(229, 231, 235, 0.8);
  margin-top: 6px;
  padding-top: 14px;
  position: relative;
}

:deep(.custom-dropdown-menu .el-dropdown-menu__item--divided::after) {
  content: '';
  position: absolute;
  top: -3px;
  left: 16px;
  right: 16px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(229, 231, 235, 0.8), transparent);
}

/* 管理员下拉菜单特殊样式 */
:deep(.admin-menu .dropdown-item:first-child) {
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.05), rgba(245, 108, 108, 0.05));
  border: 1px solid rgba(230, 162, 60, 0.1);
}

:deep(.admin-menu .dropdown-item:first-child .menu-icon) {
  color: #e6a23c;
}

:deep(.admin-menu .dropdown-item:hover .menu-icon) {
  color: #e6a23c;
}

/* 下拉箭头动画 */
:deep(.el-dropdown .el-dropdown-selfdefine:focus) {
  outline: none;
}

:deep(.el-dropdown .el-dropdown-selfdefine:focus-visible) {
  outline: 2px solid #67c23a;
  outline-offset: 2px;
  border-radius: 20px;
}

/* 激活状态 */
:deep(.el-dropdown.is-active .dropdown-arrow) {
  transform: rotate(180deg);
  color: #67c23a;
}

:deep(.admin-dropdown.is-active .dropdown-arrow) {
  color: #e6a23c;
}

/* ========== 下拉框样式优化结束 ========== */

/* 主要内容区域 */
.main-content {
  position: relative;
  z-index: 1;
}

/* 英雄区域 */
.hero-section {
  padding: 80px 0;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.05) 0%, rgba(25, 190, 107, 0.05) 100%);
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  color: #1f2937;
  line-height: 1.1;
  margin-bottom: 24px;
}

.highlight {
  background: linear-gradient(135deg, #67c23a, #19be6b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 18px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.hero-btn {
  border-radius: 10px;
  padding: 14px 32px;
  font-weight: 600;
  font-size: 16px;
}

.hero-btn.primary {
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border: none;
}

.hero-btn.secondary {
  background: white;
  border: 2px solid #e5e7eb;
  color: #374151;
}

.hero-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #67c23a;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.hero-visual {
  position: relative;
  height: 400px;
}

.floating-cards {
  position: relative;
  height: 100%;
}

.card {
  position: absolute;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
}

.card i {
  font-size: 32px;
  color: #67c23a;
}

.card span {
  font-weight: 600;
  color: #1f2937;
}

.card-1 {
  top: 20%;
  left: 10%;
  animation: float-card 6s ease-in-out infinite;
}

.card-2 {
  top: 50%;
  right: 20%;
  animation: float-card 6s ease-in-out infinite 2s;
}

.card-3 {
  bottom: 20%;
  left: 30%;
  animation: float-card 6s ease-in-out infinite 4s;
}

@keyframes float-card {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

/* 通用分区样式 */
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
}

.header-content {
  flex: 1;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title i {
  color: #67c23a;
  font-size: 28px;
}

.section-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

.view-all-btn {
  color: #67c23a;
  font-weight: 600;
}

/* 分类区域 */
.categories-section {
  padding: 80px 0;
  background: white;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.category-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
}

.category-card:hover {
  background: white;
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  border-color: #67c23a;
}

.category-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.category-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.category-info {
  flex: 1;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.category-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.category-arrow {
  color: #9ca3af;
  transition: all 0.3s ease;
}

.category-card:hover .category-arrow {
  color: #67c23a;
  transform: translateX(4px);
}

/* 商品区域 */
.products-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.product-card:hover .image {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f8fafc;
  color: #9ca3af;
}

.image-error i {
  font-size: 32px;
  margin-bottom: 8px;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}

.product-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.stock {
  font-size: 12px;
  color: #6b7280;
}

/* 欢迎信息区域 */
.welcome-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
}

.welcome-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.welcome-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.welcome-visual {
  text-align: center;
}

.welcome-icon {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  color: white;
  font-size: 48px;
  backdrop-filter: blur(10px);
}

.welcome-text {
  color: white;
}

.welcome-text h2 {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.welcome-text p {
  font-size: 18px;
  margin: 0 0 32px 0;
  line-height: 1.6;
  opacity: 0.9;
}

.welcome-actions {
  display: flex;
  gap: 16px;
}

.welcome-btn {
  border-radius: 10px;
  padding: 14px 32px;
  font-weight: 600;
  font-size: 16px;
}

.welcome-btn.primary {
  background: white;
  color: #67c23a;
  border: none;
}

.welcome-btn.secondary {
  background: transparent;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.welcome-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hero-content,
  .welcome-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .hero-title {
    font-size: 36px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .hero-section {
    padding: 60px 0;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-stats {
    justify-content: center;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .categories-grid,
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  /* 响应式下拉框优化 */
  .user-info {
    padding: 6px 12px;
    gap: 8px;
  }
  
  .user-details {
    display: none;
  }
  
  .user-info .dropdown-arrow {
    display: none;
  }
  
  :deep(.custom-dropdown-menu) {
    min-width: 160px;
    border-radius: 12px;
  }
  
  :deep(.custom-dropdown-menu .dropdown-item) {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .menu-item-content {
    gap: 8px;
  }
  
  .menu-icon {
    font-size: 14px;
    width: 16px;
  }
}

@media (max-width: 480px) {
  .header-container {
    flex-wrap: wrap;
    height: auto;
    padding: 12px 16px;
    gap: 12px;
  }
  
  .brand-section {
    order: 1;
  }
  
  .header-actions {
    order: 3;
    width: 100%;
    justify-content: space-between;
  }
  
  .section-container {
    padding: 0 16px;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .welcome-actions {
    flex-direction: column;
  }
  
  /* 移动端下拉框进一步优化 */
  .user-info {
    background: transparent;
    border: none;
    box-shadow: none;
    padding: 4px;
  }
  
  .user-info:hover {
    background: rgba(255, 255, 255, 0.5);
    transform: none;
  }
  
  :deep(.custom-dropdown-menu) {
    min-width: 140px;
  }
  
  :deep(.custom-dropdown-menu .dropdown-item) {
    padding: 8px 10px;
    font-size: 12px;
  }
}
</style>