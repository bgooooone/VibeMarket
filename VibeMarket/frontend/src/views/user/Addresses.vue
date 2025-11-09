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
      <el-table v-else :data="addressList" style="width: 100%">
        <el-table-column prop="consignee" label="收货人" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="address" label="地址" />
        <el-table-column label="操作" width="200">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const addressList = ref([])

const handleAdd = () => {
  // TODO: 实现添加地址功能
}
</script>

<style scoped>
.addresses {
  max-width: 1000px;
}
</style>

