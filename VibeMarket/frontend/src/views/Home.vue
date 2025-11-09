<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>校园购物商城</h1>
          <div class="header-actions">
            <el-button v-if="!userStore.isLoggedIn" @click="$router.push('/login')">登录</el-button>
            <el-button v-if="!userStore.isLoggedIn" type="primary" @click="$router.push('/register')">注册</el-button>
            <el-button v-if="!userStore.isLoggedIn" @click="$router.push('/admin/login')" style="margin-left: 10px;">管理员登录</el-button>
            <el-dropdown v-else>
              <span class="user-info">
                {{ userStore.userInfo?.username || '用户' }}
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/cart')">购物车</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/user/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <div class="welcome">
          <h2>欢迎来到校园购物商城</h2>
          <p>这是一个基于Vue 3 + Spring Boot的校园购物平台</p>
          <el-button type="primary" size="large" @click="$router.push({ name: 'ProductList' })">开始购物</el-button>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出登录')
  location.reload()
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.el-header {
  background-color: #409eff;
  color: white;
  line-height: 60px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.user-info {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-main {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
}

.welcome {
  text-align: center;
}

.welcome h2 {
  font-size: 32px;
  margin-bottom: 20px;
}

.welcome p {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}
</style>

