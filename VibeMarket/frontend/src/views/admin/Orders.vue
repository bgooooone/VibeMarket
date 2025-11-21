<template>
  <div class="admin-orders">
    <el-card>
      <template #header>
        <span>订单管理</span>
      </template>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待支付" name="0"></el-tab-pane>
        <el-tab-pane label="待发货" name="1"></el-tab-pane>
        <el-tab-pane label="待收货" name="2"></el-tab-pane>
        <el-tab-pane label="已完成" name="4"></el-tab-pane>
      </el-tabs>
      <el-table :data="orderList" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="consignee" label="收货人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              link
              @click="handleUpdateStatus(row, 2)"
            >
              标记已发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
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
import { getAllOrders, getAdminOrderDetail, shipOrder, updateOrderStatus } from '@/api/order'

const activeTab = ref('all')
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  try {
    const status = activeTab.value === 'all' ? null : parseInt(activeTab.value)
    const data = await getAllOrders(status, currentPage.value, pageSize.value)
    orderList.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

const handleTabChange = (tab) => {
  currentPage.value = 1
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

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const handleViewDetail = async (order) => {
  try {
    const data = await getAdminOrderDetail(order.id)
    currentOrder.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

const handleShip = async (order) => {
  try {
    await ElMessageBox.confirm('确定要发货这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await shipOrder(order.id)
    ElMessage.success('发货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
    }
  }
}

const handleUpdateStatus = async (order, status) => {
  try {
    await updateOrderStatus(order.id, status)
    ElMessage.success('状态更新成功')
    loadOrders()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}
</script>

<style scoped>
.admin-orders {
  padding: 20px;
}
</style>
