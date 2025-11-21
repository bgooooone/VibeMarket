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
import { productApi } from '@/api/product'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)

onMounted(() => {
  loadProduct()
})

const loadProduct = async () => {
  const productId = route.params.id
  try {
    product.value = await productApi.getProductDetail(productId)
    if (!product.value) {
      ElMessage.error('商品不存在')
      router.push('/products')
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
    router.push('/products')
  }
}

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const { addToCart } = await import('@/api/cart')
    await addToCart({
      productId: product.value.id,
      quantity: quantity.value
    })
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加到购物车失败:', error)
  }
}

const handleBuyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 立即购买：先添加到购物车，然后跳转到结算页面
  router.push({
    path: '/checkout',
    query: {
      productId: product.value.id,
      quantity: quantity.value
    }
  })
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

