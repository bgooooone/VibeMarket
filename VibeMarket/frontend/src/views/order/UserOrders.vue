<template>
  <div class="user-orders">
    <!-- 背景装饰 -->
    <div class="background-elements">
      <div class="decoration-shape shape-1"></div>
      <div class="decoration-shape shape-2"></div>
      <div class="decoration-shape shape-3"></div>
    </div>

    <div class="orders-container">
      <!-- 订单统计卡片 -->
      <div class="stats-cards">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ getStatusCount(0) }}</div>
              <div class="stat-label">待支付</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon shipping">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ getStatusCount(1) + getStatusCount(2) }}</div>
              <div class="stat-label">待收货</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ getStatusCount(4) }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ orderList.length }}</div>
              <div class="stat-label">全部订单</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 订单列表 -->
      <el-card class="orders-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-content">
              <el-icon class="header-icon"><List /></el-icon>
              <span class="header-title">我的订单</span>
            </div>
            <div class="header-actions">
              <el-button type="text" @click="loadOrders" :loading="loading">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </div>
        </template>

        <!-- 标签页 -->
        <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="order-tabs">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="待支付" name="0"></el-tab-pane>
          <el-tab-pane label="待发货" name="1"></el-tab-pane>
          <el-tab-pane label="待收货" name="2"></el-tab-pane>
          <el-tab-pane label="已完成" name="4"></el-tab-pane>
        </el-tabs>

        <!-- 订单列表内容 -->
        <div class="orders-content">
          <el-empty 
            v-if="orderList.length === 0 && !loading" 
            description="暂无订单" 
            class="orders-empty"
          >
            <el-button type="primary" @click="$router.push('/products')">
              去购物
            </el-button>
          </el-empty>

          <div v-else class="order-list">
            <div v-for="order in orderList" :key="order.id" class="order-item">
              <div class="order-header">
                <div class="order-basic-info">
                  <div class="order-no">
                    <el-icon><Document /></el-icon>
                    订单号：{{ order.orderNo }}
                  </div>
                  <div class="order-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(order.createTime) }}
                  </div>
                </div>
                <div class="order-status-info">
                  <span class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
                  <div class="order-amount">
                    实付：<span class="amount">¥{{ order.totalAmount }}</span>
                  </div>
                </div>
              </div>

              <div class="order-body">
                <div class="order-address">
                  <div class="address-info">
                    <el-icon><Location /></el-icon>
                    <span class="consignee">{{ order.consignee }}</span>
                    <span class="phone">{{ order.phone }}</span>
                    <span class="address-detail">
                      {{ order.province }} {{ order.city }} {{ order.district }} {{ order.detailAddress }}
                    </span>
                  </div>
                </div>

                <div class="order-actions">
                  <el-button 
                    v-if="order.status === 0" 
                    type="primary" 
                    @click="handlePay(order)"
                    class="action-btn primary"
                  >
                    立即支付
                  </el-button>
                  <el-button 
                    v-if="order.status === 0" 
                    type="danger" 
                    @click="handleCancel(order)"
                    class="action-btn danger"
                  >
                    取消订单
                  </el-button>
                  <el-button 
                    v-if="order.status === 2" 
                    type="success" 
                    @click="handleConfirm(order)"
                    class="action-btn success"
                  >
                    确认收货
                  </el-button>
                  <el-button 
                    @click="handleViewDetail(order)"
                    class="action-btn default"
                  >
                    订单详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="订单详情" 
      width="800px"
      class="order-detail-dialog"
    >
      <div v-if="currentOrder" class="detail-content">
        <!-- 订单基本信息 -->
        <el-descriptions :column="2" border class="order-descriptions">
          <el-descriptions-item label="订单号">
            <span class="detail-value">{{ currentOrder.order.orderNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.order.status)" class="detail-tag">
              {{ getStatusText(currentOrder.order.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总金额">
            <span class="detail-price">¥{{ currentOrder.order.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">
            <span class="detail-value">{{ formatTime(currentOrder.order.createTime) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">
            <span class="detail-value">{{ currentOrder.order.consignee }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            <span class="detail-value">{{ currentOrder.order.phone }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            <span class="detail-value">
              {{ currentOrder.order.province }} {{ currentOrder.order.city }} 
              {{ currentOrder.order.district }} {{ currentOrder.order.detailAddress }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 商品信息 -->
        <div class="products-section">
          <h4 class="section-title">商品信息</h4>
          <el-table :data="currentOrder.orderItems" class="products-table">
            <el-table-column label="商品" width="300">
              <template #default="{ row }">
                <div class="product-info">
                  <el-image
                    :src="row.productImage || '/default-product.jpg'"
                    class="product-image"
                    fit="cover"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  <div class="product-details">
                    <div class="product-name">{{ row.productName }}</div>
                    <div class="product-spec">规格：默认</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120" align="center">
              <template #default="{ row }">
                <span class="product-price">¥{{ row.unitPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="100" align="center">
              <template #default="{ row }">
                <span class="product-quantity">x{{ row.quantity }}</span>
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120" align="center">
              <template #default="{ row }">
                <span class="product-total">¥{{ row.totalPrice }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Clock, 
  CircleCheck, 
  Document, 
  List, 
  Refresh, 
  Box, 
  Location,
  Picture
} from '@element-plus/icons-vue'
import { getUserOrders, getOrderDetail, cancelOrder } from '@/api/order'

const activeTab = ref('all')
const orderList = ref([])
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const loading = ref(false)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const status = activeTab.value === 'all' ? null : parseInt(activeTab.value)
    const orders = await getUserOrders(status)
    orderList.value = orders
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab) => {
  loadOrders()
}

const getStatusCount = (status) => {
  if (activeTab.value !== 'all') return 0
  return orderList.value.filter(order => order.status === status).length
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    3: '待评价',
    4: '已完成',
    5: '已取消',
    6: '已退款'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: '',
    4: 'success',
    5: 'danger',
    6: 'danger'
  }
  return typeMap[status] || ''
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'status-warning',
    1: 'status-info',
    2: 'status-primary',
    4: 'status-success',
    5: 'status-danger'
  }
  return classMap[status] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const handlePay = (order) => {
  ElMessage.info('支付功能开发中')
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'confirm-danger'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const handleConfirm = (order) => {
  ElMessage.info('确认收货功能开发中')
}

const handleViewDetail = async (order) => {
  try {
    const data = await getOrderDetail(order.id)
    currentOrder.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  }
}
</script>

<style scoped>
.user-orders {
  min-height: 100%;
  position: relative;
  padding: 0;
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
  background: rgba(255, 255, 255, 0.1);
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

.orders-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
}

.stat-icon.shipping {
  background: linear-gradient(135deg, #409eff, #67c23a);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #67c23a, #19be6b);
}

.stat-icon.total {
  background: linear-gradient(135deg, #909399, #606266);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

/* 订单卡片样式 */
.orders-card {
  border: none;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.header-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

/* 标签页样式 */
.order-tabs {
  margin-top: 10px;
}

:deep(.order-tabs .el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.order-tabs .el-tabs__nav-wrap::after) {
  height: 1px;
}

/* 订单列表样式 */
.orders-content {
  min-height: 400px;
}

.orders-empty {
  padding: 60px 0;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.order-item:hover {
  background: #f0f7ff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.order-basic-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-no, .order-time {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.order-status-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.order-status {
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 14px;
}

.status-warning {
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
}

.status-info {
  color: #909399;
  background: rgba(144, 147, 153, 0.1);
}

.status-primary {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

.status-success {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
}

.status-danger {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

.order-amount {
  font-size: 16px;
  color: #303133;
}

.amount {
  color: #f56c6c;
  font-weight: 700;
  font-size: 18px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-address {
  flex: 1;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.consignee {
  font-weight: 600;
  color: #303133;
}

.phone {
  color: #409eff;
}

.address-detail {
  color: #909399;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  border-radius: 6px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* 加载状态 */
.loading-container {
  padding: 40px 0;
}

/* 订单详情对话框 */
.order-detail-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e4e7ed;
  padding: 20px 24px;
}

.detail-content {
  padding: 10px 0;
}

.order-descriptions {
  margin-bottom: 24px;
}

.detail-value {
  font-weight: 500;
}

.detail-price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 16px;
}

.detail-tag {
  border: none;
  border-radius: 6px;
  font-weight: 600;
}

.products-section {
  margin-top: 24px;
}

.section-title {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.products-table {
  border-radius: 8px;
  overflow: hidden;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  flex-shrink: 0;
}

.image-error {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #303133;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

.product-price, .product-total {
  font-weight: 600;
  color: #f56c6c;
}

.product-quantity {
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .order-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .order-status-info {
    align-items: flex-start;
  }
  
  .order-body {
    flex-direction: column;
    gap: 16px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .order-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
}
</style>