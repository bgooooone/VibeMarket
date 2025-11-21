<template>
  <div class="cart">
    <div class="cart-header">
      <el-button type="primary" link @click="$router.push('/')" class="back-home-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
    </div>
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
        <el-table :data="cartList" style="width: 100%" @selection-change="handleSelectionChange">
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
import { ArrowLeft } from '@element-plus/icons-vue'
import { getCartList, updateCart, removeFromCart, clearCart, selectCartItem } from '@/api/cart'
import { getProductById } from '@/api/product'

const router = useRouter()
const cartList = ref([])
const productMap = ref({})

onMounted(() => {
  loadCart()
})

const loadCart = async () => {
  try {
    const carts = await getCartList()
    cartList.value = []
    for (const cart of carts) {
      try {
        const product = await getProductById(cart.productId)
        cartList.value.push({
          ...cart,
          productName: product.name,
          price: product.price,
          stock: product.stock,
          mainImage: product.mainImage
        })
      } catch (error) {
        console.error('加载商品信息失败:', error)
      }
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
  }
}

const totalCount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const handleQuantityChange = async (item) => {
  try {
    await updateCart(item.id, {
      productId: item.productId,
      quantity: item.quantity,
      selected: item.selected
    })
    ElMessage.success('数量已更新')
  } catch (error) {
    loadCart()
  }
}

const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromCart(item.id)
    ElMessage.success('删除成功')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearCart()
    ElMessage.success('购物车已清空')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

const handleSelectionChange = (selection) => {
  // 更新选中状态
  const selectedIds = selection.map(item => item.id)
  cartList.value.forEach(item => {
    if (selectedIds.includes(item.id)) {
      item.selected = 1
      selectCartItem(item.id, 1).catch(() => {})
    } else {
      item.selected = 0
      selectCartItem(item.id, 0).catch(() => {})
    }
  })
}

const handleCheckout = () => {
  const selectedItems = cartList.value.filter(item => item.selected === 1)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push({
    path: '/checkout',
    query: {
      cartIds: selectedItems.map(item => item.id).join(',')
    }
  })
}
</script>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  margin-bottom: 20px;
  padding: 10px 0;
}

.back-home-btn {
  font-size: 14px;
  padding: 0;
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

