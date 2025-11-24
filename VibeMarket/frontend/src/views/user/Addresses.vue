<template>
  <div class="addresses-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h1 class="page-title">收货地址管理</h1>
      <p class="page-subtitle">管理您的收货地址，方便快速下单</p>
    </div>

    <el-card class="addresses-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-content">
            <div class="header-info">
              <el-icon class="header-icon"><Location /></el-icon>
              <span class="header-title">收货地址</span>
              <el-tag v-if="addressList.length > 0" type="info" size="small">
                {{ addressList.length }}个地址
              </el-tag>
            </div>
            <el-button 
              type="primary" 
              @click="handleAdd"
              class="add-btn"
              :icon="Plus"
            >
              添加新地址
            </el-button>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty 
        v-if="addressList.length === 0" 
        description="暂无收货地址"
        :image-size="200"
        class="custom-empty"
      >
        <el-button type="primary" @click="handleAdd">添加第一个地址</el-button>
      </el-empty>

      <!-- 地址列表 -->
      <div v-else class="address-list">
        <div 
          v-for="address in addressList" 
          :key="address.id" 
          class="address-item"
          :class="{ 'default-address': address.isDefault === 1 }"
        >
          <div class="address-main">
            <!-- 地址标识 -->
            <div class="address-badge" v-if="address.isDefault === 1">
              <el-icon><StarFilled /></el-icon>
              <span>默认地址</span>
            </div>

            <!-- 地址信息 -->
            <div class="address-content">
              <div class="address-header">
                <div class="contact-info">
                  <span class="consignee">{{ address.consignee }}</span>
                  <span class="phone">{{ address.phone }}</span>
                </div>
                <div class="address-tags">
                  <el-tag 
                    v-if="address.isDefault === 1" 
                    type="success" 
                    size="small"
                    effect="dark"
                    class="default-tag"
                  >
                    默认地址
                  </el-tag>
                </div>
              </div>
              
              <div class="address-detail">
                <el-icon class="location-icon"><Location /></el-icon>
                <span>{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="address-actions">
              <el-button 
                type="primary" 
                link 
                @click="handleEdit(address)"
                class="action-btn"
                :icon="Edit"
              >
                编辑
              </el-button>
              <el-button
                v-if="address.isDefault !== 1"
                type="success"
                link
                @click="handleSetDefault(address)"
                class="action-btn"
                :icon="Star"
              >
                设为默认
              </el-button>
              <el-button 
                type="danger" 
                link 
                @click="handleDelete(address)"
                class="action-btn"
                :icon="Delete"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 添加/编辑地址对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="680px"
      class="address-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <el-form 
          :model="addressForm" 
          :rules="addressRules" 
          ref="addressFormRef" 
          label-width="100px"
          label-position="left"
          class="address-form"
        >
          <div class="form-row">
            <el-form-item label="收货人" prop="consignee" class="form-item">
              <el-input 
                v-model="addressForm.consignee" 
                placeholder="请输入收货人姓名" 
                size="large"
                class="custom-input"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="手机号" prop="phone" class="form-item">
              <el-input 
                v-model="addressForm.phone" 
                placeholder="请输入手机号" 
                size="large"
                class="custom-input"
                :prefix-icon="Iphone"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="省份" prop="province" class="form-item">
              <el-input 
                v-model="addressForm.province" 
                placeholder="请输入省份" 
                size="large"
                class="custom-input"
                :prefix-icon="MapLocation"
              />
            </el-form-item>
            <el-form-item label="城市" prop="city" class="form-item">
              <el-input 
                v-model="addressForm.city" 
                placeholder="请输入城市" 
                size="large"
                class="custom-input"
                :prefix-icon="OfficeBuilding"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="区县" prop="district" class="form-item">
              <el-input 
                v-model="addressForm.district" 
                placeholder="请输入区县" 
                size="large"
                class="custom-input"
                :prefix-icon="Position"
              />
            </el-form-item>
          </div>

          <el-form-item label="详细地址" prop="detailAddress" class="full-width">
            <el-input
              v-model="addressForm.detailAddress"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址（街道、门牌号等）"
              class="custom-textarea"
              :prefix-icon="House"
            />
          </el-form-item>

          <el-form-item label="设为默认" class="full-width switch-item">
            <div class="switch-container">
              <el-switch 
                v-model="addressForm.isDefault" 
                :active-value="1" 
                :inactive-value="0"
                size="large"
                inline-prompt
                active-text="是"
                inactive-text="否"
              />
              <span class="switch-tip">设置为默认收货地址</span>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            @click="dialogVisible = false" 
            size="large"
            class="cancel-btn"
          >
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            size="large"
            :loading="submitLoading"
            class="submit-btn"
          >
            {{ editingId ? '更新地址' : '添加地址' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, 
  Plus, 
  Edit, 
  Delete, 
  Star, 
  StarFilled,
  User,
  Iphone,
  MapLocation,
  OfficeBuilding,
  Position,
  House
} from '@element-plus/icons-vue'
import { 
  getAddressList, 
  addAddress, 
  updateAddress, 
  deleteAddress, 
  setDefaultAddress 
} from '@/api/address'

const addressList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加地址')
const addressFormRef = ref(null)
const submitLoading = ref(false)
const addressForm = ref({
  consignee: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})
const editingId = ref(null)

const addressRules = {
  consignee: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在2到10个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度在5到100个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadAddresses()
})

const loadAddresses = async () => {
  try {
    const addresses = await getAddressList()
    addressList.value = addresses
  } catch (error) {
    console.error('加载地址失败:', error)
    ElMessage.error('加载地址失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加收货地址'
  editingId.value = null
  addressForm.value = {
    consignee: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  }
  dialogVisible.value = true
}

const handleEdit = (address) => {
  dialogTitle.value = '编辑收货地址'
  editingId.value = address.id
  addressForm.value = {
    consignee: address.consignee,
    phone: address.phone,
    province: address.province,
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    isDefault: address.isDefault
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!addressFormRef.value) return
  
  try {
    submitLoading.value = true
    await addressFormRef.value.validate()
    
    if (editingId.value) {
      await updateAddress(editingId.value, addressForm.value)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(addressForm.value)
      ElMessage.success('地址添加成功')
    }
    
    dialogVisible.value = false
    await loadAddresses()
  } catch (error) {
    if (error !== false) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleSetDefault = async (address) => {
  try {
    await setDefaultAddress(address.id)
    ElMessage.success('默认地址设置成功')
    await loadAddresses()
  } catch (error) {
    console.error('设置失败:', error)
    ElMessage.error('设置失败，请重试')
  }
}

const handleDelete = async (address) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除收货地址吗？\n${address.consignee} ${address.phone}\n${address.province}${address.city}${address.district}${address.detailAddress}`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '再想想',
        type: 'warning',
        confirmButtonClass: 'delete-confirm-btn',
        cancelButtonClass: 'delete-cancel-btn'
      }
    )
    
    await deleteAddress(address.id)
    ElMessage.success('地址删除成功')
    await loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}
</script>

<style scoped>
.addresses-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

/* 页面标题 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

/* 卡片样式 */
.addresses-card {
  border-radius: 16px;
  border: 1px solid #e8e8e8;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  padding: 8px 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  color: #667eea;
  font-size: 20px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.add-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
}

/* 空状态 */
.custom-empty {
  padding: 60px 0;
}

:deep(.custom-empty .el-empty__description) {
  margin-top: 16px;
  color: #666;
}

/* 地址列表 */
.address-list {
  display: grid;
  gap: 16px;
  margin-top: 8px;
}

.address-item {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  background: #fff;
  position: relative;
  overflow: hidden;
}

.address-item:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.address-item.default-address {
  border-color: #667eea;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
}

.address-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 4px 12px;
  border-radius: 0 12px 0 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.address-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.address-content {
  flex: 1;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.consignee {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.phone {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.default-tag {
  border-radius: 6px;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #666;
  line-height: 1.6;
  font-size: 15px;
}

.location-icon {
  color: #667eea;
  margin-top: 2px;
  flex-shrink: 0;
}

.address-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
}

/* 对话框样式 */
.address-dialog {
  border-radius: 16px;
}

:deep(.address-dialog .el-dialog__header) {
  margin: 0;
  padding: 24px 24px 0;
  text-align: center;
}

:deep(.address-dialog .el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.dialog-content {
  padding: 0 24px;
}

.address-form {
  margin-top: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.full-width {
  grid-column: 1 / -1;
}

:deep(.custom-input .el-input__inner) {
  border-radius: 8px;
  height: 48px;
}

:deep(.custom-textarea .el-textarea__inner) {
  border-radius: 8px;
  resize: vertical;
  min-height: 80px;
}

.switch-item {
  margin-bottom: 0;
}

.switch-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switch-tip {
  color: #666;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 0 24px 24px;
}

.cancel-btn {
  width: 100px;
}

.submit-btn {
  width: 140px;
}

/* 这一部分是删除确认对话框样式 */
:deep(.delete-confirm-btn) {
  background: #f56c6c;
  border-color: #f56c6c;
}

:deep(.delete-confirm-btn:hover) {
  background: #f78989;
  border-color: #f78989;
}

:deep(.delete-cancel-btn) {
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .addresses-container {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .address-main {
    flex-direction: column;
    gap: 16px;
  }
  
  .address-actions {
    align-self: stretch;
    justify-content: center;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .address-dialog {
    width: 95%;
    margin: 20px auto;
  }
  
  :deep(.address-dialog .el-dialog) {
    max-width: 100%;
  }
}
</style>