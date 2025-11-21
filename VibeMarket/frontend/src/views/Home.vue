<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>校园购物商城</h1>
          <div class="header-actions">
            <el-button v-if="!userStore.isLoggedIn" @click="$router.push('/login')">登录</el-button>
            <el-button v-if="!userStore.isLoggedIn" type="primary" @click="$router.push('/register')">注册</el-button>
            <el-button v-if="!userStore.isLoggedIn" @click="$router.push('/admin/login')" style="margin-left: 10px;">管理员登录</el-button>
            <el-dropdown v-else>
              <span class="user-info">
                {{ userStore.userInfo?.username || '用户' }}
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/cart')">购物车</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/user/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <!-- 分类展示 -->
        <div v-if="categories.length > 0" class="categories-section">
          <h3>商品分类</h3>
          <el-row :gutter="20">
            <el-col
              v-for="category in categories"
              :key="category.id"
              :span="6"
              style="margin-bottom: 20px;"
            >
              <el-card shadow="hover" class="category-card" @click="handleCategoryClick(category)">
                <div class="category-content">
                  <el-icon v-if="category.icon" :size="40"><component :is="category.icon" /></el-icon>
                  <span class="category-name">{{ category.name }}</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 推荐商品 -->
        <div v-if="recommendedProducts.length > 0" class="products-section">
          <h3>推荐商品</h3>
          <el-row :gutter="20">
            <el-col
              v-for="product in recommendedProducts"
              :key="product.id"
              :span="6"
              style="margin-bottom: 20px;"
            >
              <el-card shadow="hover" class="product-card" @click="handleProductClick(product)">
                <el-image
                  :src="product.mainImage || '/default-product.jpg'"
                  fit="cover"
                  style="width: 100%; height: 150px;"
                >
                  <template #error>
                    <div class="image-slot" style="display: flex; justify-content: center; align-items: center; width: 100%; height: 150px; background: #f5f7fa;">
                      <span>暂无图片</span>
                    </div>
                  </template>
                </el-image>
                <div class="product-info">
                  <h4>{{ product.name }}</h4>
                  <div class="price">¥{{ product.price }}</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 欢迎信息 -->
        <div v-if="categories.length === 0 && recommendedProducts.length === 0" class="welcome">
          <h2>欢迎来到校园购物商城</h2>
          <p>这是一个基于Vue 3 + Spring Boot的校园购物平台</p>
          <el-button type="primary" size="large" @click="$router.push({ name: 'ProductList' })">开始购物</el-button>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { categoryApi } from '@/api/category'
import { productApi } from '@/api/product'

const router = useRouter()
const userStore = useUserStore()

const categories = ref([])
const recommendedProducts = ref([])

onMounted(() => {
  loadCategories()
  loadRecommendedProducts()
  // 如果已登录但没有用户信息，重新获取
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.getUserInfo()
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
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.el-header {
  background-color: #409eff;
  color: white;
  line-height: 60px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.user-info {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.categories-section,
.products-section {
  margin-bottom: 40px;
}

.categories-section h3,
.products-section h3 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.category-card {
  cursor: pointer;
  transition: transform 0.3s;
  text-align: center;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
}

.category-name {
  font-size: 16px;
  font-weight: bold;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-info {
  padding: 15px 0;
  text-align: center;
}

.product-info h4 {
  font-size: 14px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info .price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.welcome {
  text-align: center;
  padding: 60px 20px;
}

.welcome h2 {
  font-size: 32px;
  margin-bottom: 20px;
}

.welcome p {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}
</style>

