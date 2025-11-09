<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>商品列表</span>
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
      </template>
      <el-row :gutter="20">
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
            />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  loadProducts()
})

const loadProducts = () => {
  // TODO: 调用API加载商品列表
  productList.value = []
  total.value = 0
}

const handleSearch = () => {
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

