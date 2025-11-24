<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in overviewCards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="card-icon" :class="card.type">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="card-content">
            <p class="card-label">{{ card.label }}</p>
            <p class="card-value">
              {{ formatNumber(overview[card.field], card.prefix) }}
            </p>
            <p class="card-extra" v-if="card.extraField">
              今日 {{ card.extraLabel }} {{ overview[card.extraField] || 0 }}
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div>
                <p class="chart-title">订单与销售趋势</p>
                <p class="chart-subtitle">最近 {{ trendDays }} 天订单趋势</p>
              </div>
              <el-radio-group v-model="trendDays" size="small" @change="handleTrendChange">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="14">14天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div>
                <p class="chart-title">分类商品占比</p>
                <p class="chart-subtitle">统计各分类商品数量</p>
              </div>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'
import { UserFilled, Goods, List, TrendCharts } from '@element-plus/icons-vue'

const overview = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalSales: 0,
  todayOrders: 0,
  yesterdayOrders: 0
})

const overviewCards = [
  { label: '总用户数', field: 'totalUsers', icon: UserFilled, type: 'primary', extraField: null },
  { label: '商品总数', field: 'totalProducts', icon: Goods, type: 'success', extraField: null },
  { label: '订单总数', field: 'totalOrders', icon: List, type: 'warning', extraField: 'todayOrders', extraLabel: '订单' },
  { label: '总销售额', field: 'totalSales', icon: TrendCharts, type: 'info', prefix: '¥' }
]

const trendDays = ref(7)
const trendData = ref([])
const categoryData = ref([])

const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

const loadOverview = async () => {
  try {
    overview.value = await adminApi.getDashboardOverview()
  } catch (error) {
    console.error('加载仪表盘概览失败:', error)
    ElMessage.error(error.message || '加载仪表盘概览失败')
  }
}

const loadTrends = async () => {
  try {
    trendData.value = await adminApi.getDashboardTrends({ days: trendDays.value })
    await nextTick()
    renderTrendChart()
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    ElMessage.error(error.message || '加载趋势数据失败')
  }
}

const loadCategoryDistribution = async () => {
  try {
    categoryData.value = await adminApi.getCategoryDistribution()
    await nextTick()
    renderCategoryChart()
  } catch (error) {
    console.error('加载分类分布失败:', error)
    ElMessage.error(error.message || '加载分类分布失败')
  }
}

const initCharts = () => {
  if (trendChartRef.value && !trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  if (categoryChartRef.value && !categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value)
  }
  renderTrendChart()
  renderCategoryChart()
}

const renderTrendChart = () => {
  if (!trendChart && trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
  }
  if (!trendChart) return

  const dates = trendData.value.map(item => item.date)
  const orderCounts = trendData.value.map(item => item.orderCount)
  const sales = trendData.value.map(item => Number(item.totalAmount || 0))

  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['订单数', '销售额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数'
      },
      {
        type: 'value',
        name: '销售额',
        axisLabel: {
          formatter: '¥{value}'
        }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: orderCounts,
        areaStyle: {}
      },
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: sales,
        areaStyle: {}
      }
    ]
  })
}

const renderCategoryChart = () => {
  if (!categoryChart && categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
  }
  if (!categoryChart) return

  categoryChart.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: 0
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%'
        },
        data: categoryData.value.map(item => ({
          name: item.categoryName,
          value: item.value
        }))
      }
    ]
  })
}

const handleTrendChange = () => {
  loadTrends()
}

const formatNumber = (value, prefix = '') => {
  if (value === null || value === undefined) return `${prefix}0`
  return `${prefix}${Number(value).toLocaleString('zh-CN', { maximumFractionDigits: 2 })}`
}

const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  await Promise.all([loadOverview(), loadTrends(), loadCategoryDistribution()])
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-icon.primary {
  background: linear-gradient(135deg, #409eff, #36cfc9);
}
.card-icon.success {
  background: linear-gradient(135deg, #67c23a, #95d475);
}
.card-icon.warning {
  background: linear-gradient(135deg, #e6a23c, #f3d19e);
}
.card-icon.info {
  background: linear-gradient(135deg, #909399, #c0c4cc);
}

.card-content {
  flex: 1;
}

.card-label {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.card-value {
  margin: 4px 0;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.card-extra {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.chart-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.chart {
  width: 100%;
  height: 360px;
}
</style>
