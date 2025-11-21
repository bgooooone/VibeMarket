<template>
  <div class="checkout">
    <div class="checkout-header">
      <el-button type="primary" link @click="$router.push('/')" class="back-home-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
    </div>
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
          <div class="section-header">
            <h3>收货地址</h3>
            <el-button type="primary" link @click="handleAddAddress">
              <el-icon><Plus /></el-icon>
              添加新地址
            </el-button>
          </div>
          <div v-if="addressList.length === 0" class="empty-address">
            <el-empty description="暂无收货地址" :image-size="100">
              <el-button type="primary" @click="handleAddAddress">添加地址</el-button>
            </el-empty>
          </div>
          <el-radio-group v-else v-model="selectedAddress" class="address-radio-group">
            <div
              v-for="address in addressList"
              :key="address.id"
              class="address-item"
              :class="{ 'address-selected': selectedAddress === address.id }"
              @click="selectedAddress = address.id"
            >
              <el-radio :label="address.id" class="address-radio">
                <div class="address-content">
                  <div class="address-header">
                    <span class="consignee-name">{{ address.consignee }}</span>
                    <span class="consignee-phone">{{ address.phone }}</span>
                    <el-tag v-if="address.isDefault === 1" type="success" size="small" class="default-tag">默认</el-tag>
                  </div>
                  <div class="address-detail">
                    {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
                  </div>
                </div>
              </el-radio>
            </div>
          </el-radio-group>
          
          <h3 style="margin-top: 30px;">商品信息</h3>
          <el-table :data="orderItems" style="width: 100%">
            <el-table-column label="商品" width="300">
              <template #default="{ row }">
                <div style="display: flex; align-items: center;">
                  <el-image
                    :src="row.mainImage || '/default-product.jpg'"
                    style="width: 60px; height: 60px; margin-right: 10px;"
                    fit="cover"
                  />
                  <span>{{ row.productName }}</span>
                </div>
              </template>
            </el-table-column>
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, ArrowLeft } from '@element-plus/icons-vue'
import { getAddressList } from '@/api/address'
import { getCartList } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getProductById } from '@/api/product'

const router = useRouter()
const route = useRoute()
const currentStep = ref(0)
const selectedAddress = ref(null)
const paymentType = ref('1')
const addressList = ref([])
const orderItems = ref([])
const cartIds = ref([])
const orderId = ref(null)

onMounted(() => {
  const cartIdsParam = route.query.cartIds
  const productIdParam = route.query.productId
  const quantityParam = route.query.quantity
  
  if (cartIdsParam) {
    cartIds.value = cartIdsParam.split(',').map(id => parseInt(id))
  } else if (productIdParam && quantityParam) {
    // 立即购买模式：直接使用商品ID和数量
    cartIds.value = []
  }
  loadAddresses()
  loadOrderItems()
})

const loadAddresses = async () => {
  try {
    const addresses = await getAddressList()
    addressList.value = addresses
    const defaultAddress = addresses.find(addr => addr.isDefault === 1)
    if (defaultAddress) {
      selectedAddress.value = defaultAddress.id
    } else if (addresses.length > 0) {
      selectedAddress.value = addresses[0].id
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

const loadOrderItems = async () => {
  try {
    const productIdParam = route.query.productId
    const quantityParam = route.query.quantity
    
    // 立即购买模式
    if (productIdParam && quantityParam) {
      try {
        const product = await getProductById(productIdParam)
        orderItems.value = [{
          productId: product.id,
          productName: product.name,
          price: parseFloat(product.price),
          quantity: parseInt(quantityParam),
          mainImage: product.mainImage
        }]
        return
      } catch (error) {
        console.error('加载商品信息失败:', error)
        return
      }
    }
    
    // 购物车模式
    if (cartIds.value.length === 0) {
      const carts = await getCartList()
      const selectedCarts = carts.filter(cart => cart.selected === 1)
      cartIds.value = selectedCarts.map(cart => cart.id)
    }
    
    if (cartIds.value.length === 0) {
      ElMessage.warning('没有可结算的商品')
      router.push('/cart')
      return
    }
    
    const carts = await getCartList()
    const selectedCarts = carts.filter(cart => cartIds.value.includes(cart.id))
    
    orderItems.value = []
    for (const cart of selectedCarts) {
      try {
        const product = await getProductById(cart.productId)
        orderItems.value.push({
          productId: product.id,
          productName: product.name,
          price: parseFloat(product.price),
          quantity: cart.quantity,
          mainImage: product.mainImage
        })
      } catch (error) {
        console.error('加载商品信息失败:', error)
      }
    }
  } catch (error) {
    console.error('加载订单商品失败:', error)
  }
}

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const handleAddAddress = () => {
  router.push('/user/addresses')
}

const handleSubmitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (orderItems.value.length === 0) {
    ElMessage.warning('没有可结算的商品')
    return
  }
  
  try {
    const productIdParam = route.query.productId
    const quantityParam = route.query.quantity
    
    // 立即购买模式：先添加到购物车
    if (productIdParam && quantityParam) {
      const { addToCart } = await import('@/api/cart')
      const cart = await addToCart({
        productId: parseInt(productIdParam),
        quantity: parseInt(quantityParam),
        selected: 1
      })
      cartIds.value = [cart.id]
    }
    
    const order = await createOrder({
      cartIds: cartIds.value,
      addressId: selectedAddress.value,
      remark: ''
    })
    orderId.value = order.id
    currentStep.value = 1
    ElMessage.success('订单创建成功，请支付')
  } catch (error) {
    console.error('创建订单失败:', error)
  }
}

const handlePay = async () => {
  if (!orderId.value) {
    ElMessage.error('订单ID不存在')
    return
  }
  
  try {
    // 调用支付接口，更新订单状态为待发货（1）
    const { payOrder } = await import('@/api/order')
    await payOrder(orderId.value)
    ElMessage.success('支付成功')
    currentStep.value = 2
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  }
}
</script>

<style scoped>
.checkout {
  max-width: 1200px;
  margin: 0 auto;
}

.checkout-header {
  margin-bottom: 20px;
  padding: 10px 0;
}

.back-home-btn {
  font-size: 14px;
  padding: 0;
}

.checkout-content {
  margin-top: 30px;
}

.step-card {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.empty-address {
  padding: 40px 0;
}

.address-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.address-item {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.address-item.address-selected {
  border-color: #409eff;
  background-color: #ecf5ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.address-radio {
  width: 100%;
  margin: 0;
}

.address-radio :deep(.el-radio__input) {
  margin-right: 12px;
}

.address-radio :deep(.el-radio__label) {
  width: 100%;
  padding-left: 0;
}

.address-content {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.consignee-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.consignee-phone {
  font-size: 14px;
  color: #606266;
}

.default-tag {
  margin-left: auto;
}

.address-detail {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  padding-left: 0;
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
