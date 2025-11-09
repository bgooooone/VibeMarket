<template>
  <div class="cart">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>购物车</span>
          <el-button v-if="cartList.length > 0" type="danger" @click="handleClear">清空购物车</el-button>
        </div>
      </template>
      <el-empty v-if="cartList.length === 0" description="购物车是空的，快去选购商品吧！">
        <el-button type="primary" @click="$router.push('/')">去购物</el-button>
      </el-empty>
      <div v-else>
        <el-table :data="cartList" style="width: 100%">
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品" width="300">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <el-image
                  :src="row.mainImage || '/default-product.jpg'"
                  style="width: 80px; height: 80px; margin-right: 10px;"
                  fit="cover"
                />
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.stock"
                @change="handleQuantityChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" link @click="handleRemove(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="cart-footer">
          <div class="total-info">
            <span>共 {{ totalCount }} 件商品，合计：</span>
            <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="handleCheckout">去结算</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartList = ref([])

onMounted(() => {
  loadCart()
})

const loadCart = () => {
  // TODO: 调用API加载购物车
  cartList.value = []
}

const totalCount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const handleQuantityChange = (item) => {
  // TODO: 更新购物车商品数量
  ElMessage.success('数量已更新')
}

const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // TODO: 调用API删除商品
    ElMessage.success('删除成功')
    loadCart()
  } catch {
    // 用户取消
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // TODO: 调用API清空购物车
    ElMessage.success('购物车已清空')
    loadCart()
  } catch {
    // 用户取消
  }
}

const handleCheckout = () => {
  router.push('/checkout')
}
</script>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.total-info {
  font-size: 16px;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
  margin-left: 10px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>

