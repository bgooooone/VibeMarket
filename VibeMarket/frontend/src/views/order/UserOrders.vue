<template>
  <div class="user-orders">
    <el-card>
      <template #header>
        <span>我的订单</span>
      </template>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待支付" name="0"></el-tab-pane>
        <el-tab-pane label="待发货" name="1"></el-tab-pane>
        <el-tab-pane label="待收货" name="2"></el-tab-pane>
        <el-tab-pane label="已完成" name="4"></el-tab-pane>
      </el-tabs>
      <el-empty v-if="orderList.length === 0" description="暂无订单" />
      <div v-else class="order-list">
        <el-card v-for="order in orderList" :key="order.id" class="order-item" shadow="hover">
          <div class="order-header">
            <span>订单号：{{ order.orderNo }}</span>
            <span class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-content">
            <div class="order-info">
              <p>总金额：<span class="price">¥{{ order.totalAmount }}</span></p>
              <p>收货地址：{{ order.province }} {{ order.city }} {{ order.district }} {{ order.detailAddress }}</p>
              <p>收货人：{{ order.consignee }} {{ order.phone }}</p>
              <p>下单时间：{{ formatTime(order.createTime) }}</p>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="primary" @click="handlePay(order)">去支付</el-button>
              <el-button v-if="order.status === 0" type="danger" @click="handleCancel(order)">取消订单</el-button>
              <el-button v-if="order.status === 2" type="success" @click="handleConfirm(order)">确认收货</el-button>
              <el-button @click="handleViewDetail(order)">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.order.status)">
              {{ getStatusText(currentOrder.order.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentOrder.order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.order.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.order.consignee }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.order.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ currentOrder.order.province }} {{ currentOrder.order.city }} {{ currentOrder.order.district }} {{ currentOrder.order.detailAddress }}
          </el-descriptions-item>
        </el-descriptions>
        <h4 style="margin-top: 20px;">商品信息</h4>
        <el-table :data="currentOrder.orderItems" style="width: 100%">
          <el-table-column label="商品" width="300">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <el-image
                  :src="row.productImage || '/default-product.jpg'"
                  style="width: 60px; height: 60px; margin-right: 10px;"
                  fit="cover"
                />
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="120">
            <template #default="{ row }">¥{{ row.unitPrice }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column prop="totalPrice" label="小计" width="120">
            <template #default="{ row }">¥{{ row.totalPrice }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, getOrderDetail, cancelOrder } from '@/api/order'

const activeTab = ref('all')
const orderList = ref([])
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  try {
    const status = activeTab.value === 'all' ? null : parseInt(activeTab.value)
    const orders = await getUserOrders(status)
    orderList.value = orders
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

const handleTabChange = (tab) => {
  loadOrders()
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
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
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
  }
}
</script>

<style scoped>
.user-orders {
  max-width: 1200px;
}

.order-list {
  margin-top: 20px;
}

.order-item {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-status {
  font-weight: bold;
}

.status-warning {
  color: #e6a23c;
}

.status-info {
  color: #909399;
}

.status-primary {
  color: #409eff;
}

.status-success {
  color: #67c23a;
}

.status-danger {
  color: #f56c6c;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-info {
  flex: 1;
}

.order-info p {
  margin: 5px 0;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
