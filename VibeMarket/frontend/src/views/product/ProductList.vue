<template>
  <div class="product-list-container">
    <!-- 背景装饰元素 -->
    <div class="background-elements">
      <div class="decoration-shape shape-1"></div>
      <div class="decoration-shape shape-2"></div>
      <div class="decoration-shape shape-3"></div>
    </div>

    <div class="products-content">
      <!-- 顶部筛选区域 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-header">
          <div class="filter-title">
            <el-icon class="title-icon"><Goods /></el-icon>
            <h1>商品列表</h1>
            <p class="subtitle">发现优质好物，尽享购物乐趣</p>
          </div>
          <div class="filter-actions">
            <el-button 
              type="text" 
              @click="handleRefresh" 
              :loading="loading"
              class="refresh-btn"
            >
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>

        <div class="filter-controls">
          <div class="filter-left">
            <el-select
              v-model="selectedCategoryId"
              placeholder="全部分类"
              clearable
              class="category-select"
              @change="handleCategoryChange"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              >
                <div class="category-option">
                  <el-icon><Collection /></el-icon>
                  <span>{{ category.name }}</span>
                </div>
              </el-option>
            </el-select>

            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品名称、描述..."
              class="search-input"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="filter-right">
            <el-button-group class="view-toggle">
              <el-button 
                :type="viewMode === 'grid' ? 'primary' : 'default'"
                @click="viewMode = 'grid'"
              >
                <el-icon><Grid /></el-icon>
              </el-button>
              <el-button 
                :type="viewMode === 'list' ? 'primary' : 'default'"
                @click="viewMode = 'list'"
              >
                <el-icon><List /></el-icon>
              </el-button>
            </el-button-group>
          </div>
        </div>

        <!-- 筛选标签 -->
        <div v-if="selectedCategoryId || searchKeyword" class="filter-tags">
          <span class="tags-label">当前筛选：</span>
          <el-tag
            v-if="selectedCategoryId"
            closable
            @close="selectedCategoryId = null"
            class="filter-tag"
          >
            <el-icon><Collection /></el-icon>
            {{ getCategoryName(selectedCategoryId) }}
          </el-tag>
          <el-tag
            v-if="searchKeyword"
            closable
            @close="searchKeyword = ''"
            class="filter-tag"
          >
            <el-icon><Search /></el-icon>
            搜索：{{ searchKeyword }}
          </el-tag>
          <el-button 
            type="text" 
            @click="clearFilters"
            class="clear-all"
          >
            清除所有
          </el-button>
        </div>
      </el-card>

      <!-- 商品列表 -->
      <div class="products-section">
        <!-- 空状态 -->
        <el-empty 
          v-if="!loading && productList.length === 0" 
          :description="emptyDescription"
          class="products-empty"
        >
          <el-button 
            type="primary" 
            @click="clearFilters"
            class="empty-action"
          >
            <el-icon><Refresh /></el-icon>
            重新加载
          </el-button>
        </el-empty>

        <!-- 网格视图 -->
        <div v-else-if="viewMode === 'grid'" class="products-grid">
          <div 
            v-for="product in displayedProducts" 
            :key="product.id" 
            class="product-card-wrapper"
          >
            <el-card 
              shadow="hover" 
              class="product-card" 
              @click="handleViewDetail(product)"
            >
              <div class="product-image">
                <el-image
                  :src="product.mainImage || '/default-product.jpg'"
                  fit="cover"
                  class="image"
                  :preview-src-list="[product.mainImage || '/default-product.jpg']"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>暂无图片</span>
                    </div>
                  </template>
                  <template #placeholder>
                    <div class="image-loading">
                      <el-icon class="loading-icon"><Loading /></el-icon>
                    </div>
                  </template>
                </el-image>
                
                <!-- 商品标签 -->
                <div class="product-tags">
                  <el-tag v-if="product.isNew" type="success" size="small" class="product-tag">
                    新品
                  </el-tag>
                  <el-tag v-if="product.isHot" type="danger" size="small" class="product-tag">
                    热销
                  </el-tag>
                  <el-tag v-if="product.discount" type="warning" size="small" class="product-tag">
                    {{ product.discount }}折
                  </el-tag>
                </div>

                <!-- 快速操作 -->
                <div class="quick-actions">
                  <el-button 
                    circle 
                    class="action-btn favorite"
                    @click.stop="handleFavorite(product)"
                  >
                    <el-icon><Star /></el-icon>
                  </el-button>
                  <el-button 
                    circle 
                    class="action-btn cart"
                    @click.stop="handleAddToCart(product)"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                  </el-button>
                </div>
              </div>

              <div class="product-info">
                <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
                <p class="product-desc" v-if="product.description">
                  {{ product.description }}
                </p>
                
                <div class="price-section">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original-price">
                    ¥{{ product.originalPrice }}
                  </span>
                  <el-tag v-if="product.discount" type="warning" size="small" class="discount-tag">
                    {{ calculateDiscount(product) }}折
                  </el-tag>
                </div>

                <div class="product-meta">
                  <div class="meta-item">
                    <el-icon><Box /></el-icon>
                    <span>库存 {{ product.stock }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><TrendCharts /></el-icon>
                    <span>销量 {{ product.sales || 0 }}</span>
                  </div>
                </div>

                <div class="product-actions">
                  <el-button 
                    type="primary" 
                    class="add-cart-btn"
                    @click.stop="handleAddToCart(product)"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                    加入购物车
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="products-list">
          <el-card 
            v-for="product in displayedProducts" 
            :key="product.id" 
            shadow="hover" 
            class="list-product-card"
            @click="handleViewDetail(product)"
          >
            <div class="list-product-content">
              <div class="list-product-image">
                <el-image
                  :src="product.mainImage || '/default-product.jpg'"
                  fit="cover"
                  class="image"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              
              <div class="list-product-info">
                <div class="list-main-info">
                  <h3 class="list-product-name">{{ product.name }}</h3>
                  <p class="list-product-desc" v-if="product.description">
                    {{ product.description }}
                  </p>
                  <div class="list-price-section">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="original-price">
                      ¥{{ product.originalPrice }}
                    </span>
                  </div>
                </div>

                <div class="list-product-meta">
                  <div class="meta-stats">
                    <div class="stat-item">
                      <el-icon><Box /></el-icon>
                      <span>库存：{{ product.stock }}</span>
                    </div>
                    <div class="stat-item">
                      <el-icon><TrendCharts /></el-icon>
                      <span>销量：{{ product.sales || 0 }}</span>
                    </div>
                  </div>
                  
                  <div class="list-actions">
                    <el-button 
                      type="primary" 
                      class="add-cart-btn"
                      @click.stop="handleAddToCart(product)"
                    >
                      <el-icon><ShoppingCart /></el-icon>
                      加入购物车
                    </el-button>
                    <el-button 
                      circle 
                      class="favorite-btn"
                      @click.stop="handleFavorite(product)"
                    >
                      <el-icon><Star /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 分页 -->
        <div v-if="productList.length > 0" class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            class="products-pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  Search, 
  Goods, 
  Refresh, 
  Collection, 
  Grid, 
  List, 
  Picture, 
  Loading,
  Star, 
  ShoppingCart, 
  Box,
  TrendCharts
} from '@element-plus/icons-vue'
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
const viewMode = ref('grid')

const emptyDescription = computed(() => {
  if (searchKeyword.value) {
    return `没有找到包含"${searchKeyword.value}"的商品`
  } else if (selectedCategoryId.value) {
    return '该分类下暂无商品'
  }
  return '暂无商品'
})

const displayedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return productList.value.slice(start, end)
})

onMounted(() => {
  loadCategories()
  if (route.query.categoryId) {
    selectedCategoryId.value = Number(route.query.categoryId)
  }
  loadProducts()
})

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
      products = await productApi.searchProducts(searchKeyword.value) || []
    } else if (selectedCategoryId.value) {
      products = await productApi.getProductsByCategory(selectedCategoryId.value) || []
    } else {
      products = await productApi.getProductList() || []
    }
    
    // 添加模拟数据用于演示
    productList.value = products.map(product => ({
      ...product,
      isNew: Math.random() > 0.7,
      isHot: Math.random() > 0.8,
      discount: Math.random() > 0.9 ? Math.floor(Math.random() * 3) + 8 : null
    }))
    
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

const handleCategoryChange = () => {
  currentPage.value = 1
  loadProducts()
}

const handleRefresh = () => {
  loadProducts()
}

const clearFilters = () => {
  selectedCategoryId.value = null
  searchKeyword.value = ''
  currentPage.value = 1
  loadProducts()
}

const getCategoryName = (categoryId) => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : '未知分类'
}

const calculateDiscount = (product) => {
  if (!product.originalPrice) return null
  return Math.round((product.price / product.originalPrice) * 10)
}

const handleViewDetail = (product) => {
  router.push(`/products/${product.id}`)
}

const handleAddToCart = (product) => {
  ElMessage.success(`已添加 ${product.name} 到购物车`)
  // 这里可以调用购物车相关的API
}

const handleFavorite = (product) => {
  ElMessage.info(`已收藏 ${product.name}`)
  // 这里可以调用收藏相关的API
}
</script>

<style scoped>
.product-list-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
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
  width: 100px;
  height: 100px;
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 70%;
  right: 8%;
  animation-delay: 2.5s;
}

.shape-3 {
  width: 40px;
  height: 40px;
  bottom: 20%;
  left: 15%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-10px) scale(1.05);
  }
}

.products-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 筛选卡片样式 */
.filter-card {
  border: none;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  margin-bottom: 24px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.filter-title {
  flex: 1;
}

.filter-title h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 28px;
  color: #67c23a;
}

.subtitle {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.filter-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-left {
  display: flex;
  gap: 16px;
  align-items: center;
  flex: 1;
}

.category-select {
  width: 200px;
}

.search-input {
  width: 300px;
}

.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-tags {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.tags-label {
  font-size: 14px;
  color: #6b7280;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 商品网格视图 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.product-card-wrapper {
  transition: transform 0.3s ease;
}

.product-card-wrapper:hover {
  transform: translateY(-4px);
}

.product-card {
  border: none;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
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

.image-error, .image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f8fafc;
  color: #909399;
}

.loading-icon {
  font-size: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.product-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 4px;
}

.product-tag {
  border: none;
  font-weight: 600;
}

.quick-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.product-card:hover .quick-actions {
  opacity: 1;
  transform: translateY(0);
}

.action-btn {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: none;
  color: #666;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #67c23a;
  color: white;
  transform: scale(1.1);
}

.product-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.current-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}

.original-price {
  color: #9ca3af;
  text-decoration: line-through;
  font-size: 14px;
}

.discount-tag {
  border: none;
  font-weight: 600;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.product-actions {
  margin-top: auto;
}

.add-cart-btn {
  width: 100%;
  border-radius: 8px;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.add-cart-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

/* 列表视图样式 */
.products-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.list-product-card {
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.list-product-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.list-product-content {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.list-product-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.list-product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.list-main-info {
  margin-bottom: 12px;
}

.list-product-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.list-product-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.list-price-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
}

.list-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.favorite-btn {
  border: none;
  color: #666;
}

.favorite-btn:hover {
  color: #f56c6c;
}

/* 其他样式 */
.products-empty {
  padding: 60px 0;
}

.empty-action {
  border-radius: 8px;
}

.loading-container {
  padding: 40px 0;
}

.pagination-section {
  display: flex;
  justify-content: center;
}

.products-pagination {
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .product-list-container {
    padding: 16px;
  }
  
  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-left {
    flex-direction: column;
  }
  
  .category-select,
  .search-input {
    width: 100%;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .list-product-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .list-product-image {
    width: 100%;
    height: 200px;
  }
  
  .list-product-meta {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .meta-stats {
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .filter-title h1 {
    font-size: 20px;
  }
}
</style>