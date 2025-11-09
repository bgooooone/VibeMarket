<template>
  <div class="checkout">
    <el-card>
      <template #header>
        <span>结算</span>
      </template>
      <el-steps :active="currentStep" finish-status="success">
        <el-step title="确认订单"></el-step>
        <el-step title="支付"></el-step>
        <el-step title="完成"></el-step>
      </el-steps>
      <div class="checkout-content">
        <el-card v-if="currentStep === 0" class="step-card">
          <h3>收货地址</h3>
          <el-radio-group v-model="selectedAddress">
            <el-radio v-for="address in addressList" :key="address.id" :label="address.id" border>
              <div>
                <p>{{ address.consignee }} {{ address.phone }}</p>
                <p>{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}</p>
              </div>
            </el-radio>
          </el-radio-group>
          <el-button type="primary" link @click="handleAddAddress">添加新地址</el-button>
          
          <h3 style="margin-top: 30px;">商品信息</h3>
          <el-table :data="orderItems" style="width: 100%">
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="price" label="单价" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
          
          <div class="order-summary">
            <p>商品总计：¥{{ totalAmount.toFixed(2) }}</p>
            <p class="total">应付总额：<span>¥{{ totalAmount.toFixed(2) }}</span></p>
          </div>
          
          <div class="checkout-actions">
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="primary" @click="handleSubmitOrder">提交订单</el-button>
          </div>
        </el-card>
        
        <el-card v-if="currentStep === 1" class="step-card">
          <h3>选择支付方式</h3>
          <el-radio-group v-model="paymentType">
            <el-radio label="1" border>微信支付</el-radio>
            <el-radio label="2" border>支付宝</el-radio>
          </el-radio-group>
          <div class="checkout-actions" style="margin-top: 30px;">
            <el-button @click="currentStep = 0">上一步</el-button>
            <el-button type="primary" @click="handlePay">立即支付</el-button>
          </div>
        </el-card>
        
        <el-card v-if="currentStep === 2" class="step-card">
          <el-result
            icon="success"
            title="订单提交成功！"
            sub-title="您的订单已提交，我们将尽快为您处理"
          >
            <template #extra>
              <el-button type="primary" @click="$router.push('/user/orders')">查看订单</el-button>
              <el-button @click="$router.push('/')">继续购物</el-button>
            </template>
          </el-result>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentStep = ref(0)
const selectedAddress = ref(null)
const paymentType = ref('1')
const addressList = ref([])
const orderItems = ref([])

onMounted(() => {
  loadAddresses()
  loadOrderItems()
})

const loadAddresses = () => {
  // TODO: 调用API加载地址列表
  addressList.value = []
}

const loadOrderItems = () => {
  // TODO: 从购物车或路由参数获取订单商品
  orderItems.value = []
}

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const handleAddAddress = () => {
  router.push('/user/addresses')
}

const handleSubmitOrder = () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  // TODO: 提交订单
  currentStep.value = 1
  ElMessage.success('订单提交成功，请支付')
}

const handlePay = () => {
  // TODO: 调用支付接口
  ElMessage.info('支付功能开发中，模拟支付成功')
  currentStep.value = 2
}
</script>

<style scoped>
.checkout {
  max-width: 1200px;
  margin: 0 auto;
}

.checkout-content {
  margin-top: 30px;
}

.step-card {
  margin-top: 20px;
}

.order-summary {
  margin-top: 20px;
  text-align: right;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.order-summary p {
  margin: 10px 0;
  font-size: 16px;
}

.order-summary .total {
  font-size: 20px;
  font-weight: bold;
}

.order-summary .total span {
  color: #f56c6c;
  font-size: 24px;
}

.checkout-actions {
  margin-top: 30px;
  text-align: right;
}
</style>

