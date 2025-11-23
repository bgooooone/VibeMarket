<template>
  <el-container class="admin-container">
    <!-- 顶部导航栏 -->
    <el-header class="admin-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo" @click="$router.push('/')">
            <el-icon><ShoppingBag /></el-icon>
            <span class="logo-text">校园购物商城</span>
          </div>
        </div>
        <div class="header-right">
          <el-button 
            type="info" 
            plain 
            @click="handleBackHome"
            class="header-btn"
          >
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
          <el-dropdown @command="handleCommand" class="admin-dropdown">
            <div class="admin-info">
              <el-avatar :size="32" class="admin-avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="admin-name">{{ adminStore.adminInfo?.username || '管理员' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="admin-aside">
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  DataBoard, 
  User, 
  Goods, 
  Document,
  ShoppingBag,
  House,
  UserFilled,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const activeMenu = computed(() => route.path)

const handleBackHome = () => {
  router.push('/')
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
      await adminStore.logout()
      ElMessage.success('已退出登录')
      router.push('/admin/login')
    } catch (error) {
      // 用户取消或登出失败
      if (error !== 'cancel') {
        console.error('登出失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
  overflow: hidden;
}

.admin-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px !important;
  line-height: 60px;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #409eff;
  font-weight: 600;
  font-size: 18px;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.logo .el-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.admin-dropdown {
  cursor: pointer;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.admin-info:hover {
  background-color: #f5f7fa;
}

.admin-avatar {
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: white;
}

.admin-name {
  font-weight: 500;
  color: #333;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
}

.admin-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.admin-menu {
  height: calc(100vh - 60px);
  border-right: none;
}

.admin-main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>

