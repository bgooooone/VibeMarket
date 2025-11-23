<template>
  <div class="profile-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>

    <div class="profile-content">
      <!-- 左侧用户头像区域 -->
      <div class="profile-sidebar">
        <div class="user-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="userInfo.avatar" class="user-avatar">
                {{ userInfo.username?.charAt(0) || '用户' }}
              </el-avatar>
              <div class="avatar-overlay">
                <i class="el-icon-camera"></i>
              </div>
            </div>
            <h3 class="username">{{ userInfo.username || '用户' }}</h3>
            <p class="user-level">普通会员</p>
          </div>
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">收藏商品</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">我的订单</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">购物车</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="profile-main">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-content">
                <i class="el-icon-user-solid header-icon"></i>
                <span class="header-title">个人信息</span>
              </div>
              <div class="header-actions">
                <el-button type="text" @click="handleReset">
                  <i class="el-icon-refresh"></i>
                  重置
                </el-button>
              </div>
            </div>
          </template>

          <div class="form-container">
            <el-form 
              :model="userInfo" 
              label-position="top"
              class="profile-form"
            >
              <!-- 用户名 -->
              <el-form-item label="用户名" class="form-item-custom">
                <div class="input-with-icon">
                  <i class="el-icon-user input-icon"></i>
                  <el-input 
                    v-model="userInfo.username" 
                    placeholder="请输入用户名"
                    disabled
                    class="custom-input"
                  />
                </div>
                <div class="input-tips">用户名创建后不可修改</div>
              </el-form-item>

              <!-- 手机号 -->
              <el-form-item label="手机号" class="form-item-custom">
                <div class="input-with-icon">
                  <i class="el-icon-mobile-phone input-icon"></i>
                  <el-input 
                    v-model="userInfo.phone" 
                    placeholder="请输入手机号"
                    maxlength="11"
                    class="custom-input"
                  />
                </div>
                <div class="input-tips">用于登录和接收重要通知</div>
              </el-form-item>

              <!-- 邮箱 -->
              <el-form-item label="邮箱" class="form-item-custom">
                <div class="input-with-icon">
                  <i class="el-icon-message input-icon"></i>
                  <el-input 
                    v-model="userInfo.email" 
                    placeholder="请输入邮箱地址"
                    type="email"
                    class="custom-input"
                  />
                </div>
                <div class="input-tips">用于密码找回和优惠信息推送</div>
              </el-form-item>

              <!-- 昵称 -->
              <el-form-item label="昵称" class="form-item-custom">
                <div class="input-with-icon">
                  <i class="el-icon-edit input-icon"></i>
                  <el-input 
                    v-model="userInfo.nickname" 
                    placeholder="请输入昵称"
                    maxlength="20"
                    show-word-limit
                    class="custom-input"
                  />
                </div>
                <div class="input-tips">昵称将在社区和评论中显示</div>
              </el-form-item>

              <!-- 操作按钮 -->
              <el-form-item class="form-actions">
                <el-button 
                  type="primary" 
                  @click="handleSave" 
                  :loading="loading"
                  class="save-btn"
                  size="large"
                >
                  <template #loading>
                    <i class="el-icon-loading"></i>
                    保存中...
                  </template>
                  <template #default>
                    <i class="el-icon-check"></i>
                    保存修改
                  </template>
                </el-button>
                
                <el-button 
                  @click="handleCancel"
                  class="cancel-btn"
                  size="large"
                >
                  <i class="el-icon-close"></i>
                  取消
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 安全设置卡片 -->
        <el-card class="security-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-content">
                <i class="el-icon-lock header-icon"></i>
                <span class="header-title">安全设置</span>
              </div>
            </div>
          </template>
          
          <div class="security-options">
            <div class="security-item">
              <div class="security-info">
                <i class="el-icon-key security-icon"></i>
                <div class="security-content">
                  <h4>修改密码</h4>
                  <p>定期修改密码有助于保护账户安全</p>
                </div>
              </div>
              <el-button type="primary" link>立即修改</el-button>
            </div>
            
            <div class="security-item">
              <div class="security-info">
                <i class="el-icon-mobile security-icon"></i>
                <div class="security-content">
                  <h4>手机验证</h4>
                  <p>绑定手机号，增强账户安全性</p>
                </div>
              </div>
              <el-tag type="success" v-if="userInfo.phone">已绑定</el-tag>
              <el-button type="primary" link v-else>立即绑定</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const loading = ref(false)
const originalInfo = ref({})

const userInfo = ref({
  username: '',
  phone: '',
  email: '',
  nickname: '',
  avatar: ''
})

onMounted(async () => {
  // 如果store中没有用户信息，先获取
  if (!userStore.userInfo) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      ElMessage.error('获取用户信息失败')
      return
    }
  }
  if (userStore.userInfo) {
    const userData = {
      username: userStore.userInfo.username || '',
      phone: userStore.userInfo.phone || '',
      email: userStore.userInfo.email || '',
      nickname: userStore.userInfo.nickname || '',
      avatar: userStore.userInfo.avatar || ''
    }
    Object.assign(userInfo.value, userData)
    // 保存原始数据用于重置
    Object.assign(originalInfo.value, userData)
  }
})

const handleSave = async () => {
  loading.value = true
  try {
    // 只发送需要更新的字段
    const updateData = {
      phone: userInfo.value.phone,
      email: userInfo.value.email || null,
      nickname: userInfo.value.nickname || null
    }
    
    const updatedUser = await userApi.updateProfile(updateData)
    
    // 更新store中的用户信息
    userStore.userInfo = updatedUser
    localStorage.setItem('userInfo', JSON.stringify(updatedUser))
    
    // 更新原始数据
    Object.assign(originalInfo.value, userInfo.value)
    
    ElMessage.success({
      message: '个人信息更新成功',
      duration: 2000,
      showClose: true
    })
  } catch (error) {
    ElMessage.error({
      message: error.message || '保存失败，请重试',
      duration: 3000,
      showClose: true
    })
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(userInfo.value, originalInfo.value)
  ElMessage.info('已重置为原始数据')
}

const handleCancel = () => {
  // 可以添加路由跳转或其他取消逻辑
  ElMessage.info('取消修改')
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  animation: float 8s ease-in-out infinite;
}

.circle-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.circle-2 {
  width: 80px;
  height: 80px;
  top: 70%;
  right: 8%;
  animation-delay: 2.5s;
}

.circle-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 15%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-15px) scale(1.05);
  }
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  position: relative;
  z-index: 1;
}

/* 左侧用户卡片 */
.profile-sidebar .user-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.avatar-section {
  margin-bottom: 30px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.user-avatar {
  border: 4px solid #e8f5e8;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  background: linear-gradient(135deg, #67c23a, #19be6b);
  color: white;
  font-weight: 600;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-overlay:hover {
  transform: scale(1.1);
}

.username {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.user-level {
  color: #67c23a;
  font-size: 14px;
  font-weight: 500;
  background: #e8f5e8;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #67c23a;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

/* 右侧主内容 */
.profile-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card,
.security-card {
  border: none;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

:deep(.profile-card .el-card__header),
:deep(.security-card .el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 24px 30px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 20px;
  color: #67c23a;
}

.header-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

/* 表单样式 */
.form-container {
  padding: 10px 0;
}

.profile-form {
  max-width: 500px;
}

:deep(.form-item-custom .el-form-item__label) {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.input-with-icon {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  z-index: 1;
}

:deep(.custom-input .el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  padding-left: 40px;
  border: 1px solid #e5e7eb;
}

:deep(.custom-input .el-input__wrapper:hover) {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  border-color: #d1d5db;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
  border-color: #67c23a;
}

:deep(.custom-input.is-disabled .el-input__wrapper) {
  background: #f8f9fa;
  border-color: #e9ecef;
}

.input-tips {
  font-size: 12px;
  color: #6b7280;
  margin-top: 6px;
}

/* 操作按钮 */
.form-actions {
  margin-top: 30px;
  text-align: center;
}

.save-btn {
  border-radius: 10px;
  padding: 12px 32px;
  font-weight: 600;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border: none;
  transition: all 0.3s ease;
}

.save-btn:hover:not(.is-loading) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4);
}

.cancel-btn {
  border-radius: 10px;
  padding: 12px 32px;
  margin-left: 12px;
}

/* 安全设置 */
.security-options {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.security-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.security-content p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 20px 16px;
  }
  
  .profile-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .profile-sidebar .user-card {
    padding: 20px 16px;
  }
  
  .user-stats {
    gap: 12px;
  }
  
  :deep(.profile-card .el-card__header),
  :deep(.security-card .el-card__header) {
    padding: 20px 24px 16px;
  }
  
  .form-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .cancel-btn {
    margin-left: 0;
  }
}

@media (max-width: 480px) {
  .profile-container {
    padding: 16px 12px;
  }
  
  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .security-item :deep(.el-button) {
    align-self: flex-end;
  }
}
</style>