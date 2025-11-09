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
            <span class="order-status">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-content">
            <div class="order-info">
              <p>总金额：<span class="price">¥{{ order.totalAmount }}</span></p>
              <p>下单时间：{{ order.createTime }}</p>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="primary" @click="handlePay(order)">去支付</el-button>
              <el-button v-if="order.status === 2" type="success" @click="handleConfirm(order)">确认收货</el-button>
              <el-button @click="handleViewDetail(order)">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('all')
const orderList = ref([])

onMounted(() => {
  loadOrders()
})

const loadOrders = () => {
  // TODO: 调用API加载订单列表
  orderList.value = []
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

const handlePay = (order) => {
  ElMessage.info('支付功能开发中')
}

const handleConfirm = (order) => {
  ElMessage.info('确认收货功能开发中')
}

const handleViewDetail = (order) => {
  ElMessage.info('查看详情功能开发中')
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
  color: #409eff;
  font-weight: bold;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  gap: 10px;
}
</style>

