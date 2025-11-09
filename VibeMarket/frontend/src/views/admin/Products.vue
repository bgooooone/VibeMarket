<template>
  <div class="admin-products">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">添加商品</el-button>
        </div>
      </template>
      <el-table :data="productList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品" width="200">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-image
                :src="row.mainImage || '/default-product.jpg'"
                style="width: 50px; height: 50px; margin-right: 10px;"
                fit="cover"
              />
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadProducts()
})

const loadProducts = () => {
  // TODO: 调用API加载商品列表
  productList.value = []
  total.value = 0
}

const handleAdd = () => {
  ElMessage.info('添加商品功能开发中')
}

const handleEdit = (product) => {
  ElMessage.info('编辑商品功能开发中')
}

const handleToggleStatus = (product) => {
  // TODO: 调用API切换商品状态
  ElMessage.success('状态已更新')
  loadProducts()
}

const handleDelete = async (product) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // TODO: 调用API删除商品
    ElMessage.success('删除成功')
    loadProducts()
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.admin-products {
  padding: 20px;
}
</style>

