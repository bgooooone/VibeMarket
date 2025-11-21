<template>
  <div class="addresses">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>收货地址</span>
          <el-button type="primary" @click="handleAdd">添加地址</el-button>
        </div>
      </template>
      <el-empty v-if="addressList.length === 0" description="暂无收货地址" />
      <div v-else class="address-list">
        <el-card v-for="address in addressList" :key="address.id" class="address-item" shadow="hover">
          <div class="address-header">
            <div class="address-info">
              <span class="consignee">{{ address.consignee }}</span>
              <span class="phone">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-actions">
              <el-button type="primary" link @click="handleEdit(address)">编辑</el-button>
              <el-button
                v-if="address.isDefault !== 1"
                type="success"
                link
                @click="handleSetDefault(address)"
              >
                设为默认
              </el-button>
              <el-button type="danger" link @click="handleDelete(address)">删除</el-button>
            </div>
          </div>
          <div class="address-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 添加/编辑地址对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="addressForm.consignee" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
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
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const addressList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加地址')
const addressFormRef = ref(null)
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
  consignee: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
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
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加地址'
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
  dialogTitle.value = '编辑地址'
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
  try {
    await addressFormRef.value.validate()
    if (editingId.value) {
      await updateAddress(editingId.value, addressForm.value)
      ElMessage.success('更新成功')
    } else {
      await addAddress(addressForm.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadAddresses()
  } catch (error) {
    if (error !== false) {
      console.error('操作失败:', error)
    }
  }
}

const handleSetDefault = async (address) => {
  try {
    await setDefaultAddress(address.id)
    ElMessage.success('设置成功')
    loadAddresses()
  } catch (error) {
    console.error('设置失败:', error)
  }
}

const handleDelete = async (address) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(address.id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>

<style scoped>
.addresses {
  max-width: 1000px;
}

.address-list {
  margin-top: 20px;
}

.address-item {
  margin-bottom: 15px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.consignee {
  font-weight: bold;
  font-size: 16px;
}

.phone {
  color: #666;
}

.address-detail {
  color: #666;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 10px;
}
</style>
