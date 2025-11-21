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
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
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
    
    <!-- 添加/编辑商品对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="productForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0.01" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="productForm.originalPrice" :min="0.01" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input
            v-model="productForm.detail"
            type="textarea"
            :rows="5"
            placeholder="请输入商品详情"
          />
        </el-form-item>
        <el-form-item label="主图">
          <el-upload
            class="image-uploader"
            :http-request="handleImageUpload"
            :show-file-list="false"
            accept="image/*"
          >
            <el-image
              v-if="productForm.mainImage"
              :src="productForm.mainImage"
              class="uploaded-image"
              fit="cover"
            />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div v-if="productForm.mainImage" style="margin-top: 10px;">
            <el-button type="danger" size="small" @click="productForm.mainImage = ''">删除图片</el-button>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="productForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAdminProducts, createProduct, updateProduct, deleteProduct, updateProductStatus } from '@/api/admin'
import { categoryApi } from '@/api/category'
import { uploadImage } from '@/api/upload'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'

const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const productFormRef = ref(null)
const editingId = ref(null)
const categories = ref([])
const userStore = useUserStore()
const adminStore = useAdminStore()

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  const { file } = options
  if (!beforeImageUpload(file)) {
    return
  }
  
  try {
    const response = await uploadImage(file)
    if (response && response.url) {
      productForm.value.mainImage = response.url
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

const productForm = ref({
  name: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  stock: null,
  description: '',
  detail: '',
  mainImage: '',
  images: '',
  status: 1
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

onMounted(() => {
  loadProducts()
  loadCategories()
})

const loadProducts = async () => {
  try {
    const products = await getAdminProducts()
    productList.value = products || []
    total.value = products?.length || 0
  } catch (error) {
    console.error('加载商品失败:', error)
    productList.value = []
    total.value = 0
  }
}

const loadCategories = async () => {
  try {
    const allCategories = await categoryApi.getAllCategories()
    categories.value = allCategories || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const handleAdd = () => {
  dialogTitle.value = '添加商品'
  editingId.value = null
  productForm.value = {
    name: '',
    categoryId: null,
    price: null,
    originalPrice: null,
    stock: null,
    description: '',
    detail: '',
    mainImage: '',
    images: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (product) => {
  dialogTitle.value = '编辑商品'
  editingId.value = product.id
  productForm.value = {
    name: product.name,
    categoryId: product.categoryId,
    price: product.price,
    originalPrice: product.originalPrice,
    stock: product.stock,
    description: product.description || '',
    detail: product.detail || '',
    mainImage: product.mainImage || '',
    images: product.images || '',
    status: product.status
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await productFormRef.value.validate()
    if (editingId.value) {
      await updateProduct(editingId.value, productForm.value)
      ElMessage.success('更新成功')
    } else {
      await createProduct(productForm.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch (error) {
    if (error !== false) {
      console.error('操作失败:', error)
    }
  }
}

const handleToggleStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 0 : 1
    await updateProductStatus(product.id, newStatus)
    ElMessage.success('状态已更新')
    loadProducts()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

const handleDelete = async (product) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProduct(product.id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>

<style scoped>
.admin-products {
  padding: 20px;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
}

.image-uploader:hover {
  border-color: #409eff;
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.uploaded-image {
  width: 100%;
  height: 100%;
}
</style>
