<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px;">
          <span>商品列表</span>
          <div style="display: flex; gap: 10px; align-items: center;">
            <el-select
              v-model="selectedCategoryId"
              placeholder="选择分类"
              clearable
              style="width: 150px;"
              @change="loadProducts"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品"
              style="width: 300px;"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />
      <el-row v-else :gutter="20">
        <el-col
          v-for="product in productList"
          :key="product.id"
          :span="6"
          style="margin-bottom: 20px;"
        >
          <el-card shadow="hover" class="product-card" @click="handleViewDetail(product)">
            <el-image
              :src="product.mainImage || '/default-product.jpg'"
              fit="cover"
              style="width: 100%; height: 200px;"
            >
              <template #error>
                <div class="image-slot" style="display: flex; justify-content: center; align-items: center; width: 100%; height: 200px; background: #f5f7fa;">
                  <span>暂无图片</span>
                </div>
              </template>
            </el-image>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <div class="price-section">
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              </div>
              <div class="product-meta">
                <span>库存：{{ product.stock }}</span>
                <span>销量：{{ product.sales }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { categoryApi } from '@/api/category'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const searchKeyword = ref('')
const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const selectedCategoryId = ref(null)
const categories = ref([])

onMounted(() => {
  loadCategories()
  // 检查URL参数中是否有分类ID
  if (route.query.categoryId) {
    selectedCategoryId.value = Number(route.query.categoryId)
  }
  loadProducts()
})

// 监听分类变化
watch(selectedCategoryId, () => {
  currentPage.value = 1
  loadProducts()
})

const loadCategories = async () => {
  try {
    const allCategories = await categoryApi.getAllCategories()
    categories.value = allCategories.filter(cat => cat.level === 1)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    let products = []
    if (searchKeyword.value) {
      // 搜索商品
      products = await productApi.searchProducts(searchKeyword.value) || []
    } else if (selectedCategoryId.value) {
      // 按分类获取商品
      products = await productApi.getProductsByCategory(selectedCategoryId.value) || []
    } else {
      // 获取所有商品
      products = await productApi.getProductList() || []
    }
    productList.value = products
    total.value = products.length
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
    productList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleViewDetail = (product) => {
  router.push(`/products/${product.id}`)
}
</script>

<style scoped>
.product-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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
}

.product-info h3 {
  font-size: 16px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  margin-bottom: 10px;
}

.current-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 12px;
}
</style>

