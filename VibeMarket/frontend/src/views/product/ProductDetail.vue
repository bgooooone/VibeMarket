<template>
  <div class="product-detail">
    <el-card v-if="product">
      <div class="product-content">
        <div class="product-images">
          <el-image
            :src="product.mainImage || '/default-product.jpg'"
            fit="cover"
            style="width: 100%; height: 400px;"
          />
        </div>
        <div class="product-info">
          <h2>{{ product.name }}</h2>
          <div class="price-section">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-meta">
            <p>库存：{{ product.stock }}</p>
            <p>销量：{{ product.sales }}</p>
          </div>
          <div class="quantity-section">
            <span>数量：</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock"
              style="margin-left: 10px;"
            />
          </div>
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="handleAddToCart">加入购物车</el-button>
            <el-button type="success" size="large" @click="handleBuyNow">立即购买</el-button>
          </div>
        </div>
      </div>
      <el-divider />
      <div class="product-description">
        <h3>商品详情</h3>
        <div v-html="product.description || product.detail || '暂无描述'"></div>
      </div>
    </el-card>
    <el-empty v-else description="商品不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)

onMounted(() => {
  loadProduct()
})

const loadProduct = () => {
  const productId = route.params.id
  // TODO: 调用API加载商品详情
  product.value = {
    id: productId,
    name: '示例商品',
    price: 99.00,
    originalPrice: 129.00,
    stock: 100,
    sales: 50,
    mainImage: '/default-product.jpg',
    description: '这是商品描述'
  }
}

const handleAddToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // TODO: 调用API添加到购物车
  ElMessage.success('已添加到购物车')
}

const handleBuyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // TODO: 跳转到结算页面
  router.push('/checkout')
}
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-content {
  display: flex;
  gap: 30px;
}

.product-images {
  flex: 1;
}

.product-info {
  flex: 1;
}

.product-info h2 {
  font-size: 24px;
  margin-bottom: 20px;
}

.price-section {
  margin-bottom: 20px;
}

.current-price {
  color: #f56c6c;
  font-size: 32px;
  font-weight: bold;
  margin-right: 15px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 18px;
}

.product-meta {
  margin: 20px 0;
  color: #666;
}

.quantity-section {
  margin: 30px 0;
  display: flex;
  align-items: center;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}

.product-description {
  margin-top: 30px;
}

.product-description h3 {
  margin-bottom: 20px;
}
</style>

