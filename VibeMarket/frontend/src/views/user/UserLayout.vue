<template>
  <el-container>
    <el-header class="user-header">
      <div class="header-content">
        <el-button type="primary" link @click="$router.push('/')" class="back-home-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </el-button>
        <h2 class="page-title">个人中心</h2>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          class="user-menu"
        >
          <el-menu-item index="/user/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/user/addresses">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Location, Document, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.user-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-home-btn {
  font-size: 14px;
  padding: 0;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-menu {
  height: calc(100vh - 60px);
}
</style>

