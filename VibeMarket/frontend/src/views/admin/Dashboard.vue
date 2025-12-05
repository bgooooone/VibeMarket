<template>
  <div class="admin-dashboard">
    <!-- 顶部数据卡片区域 -->
    <el-row :gutter="24" class="stat-cards-row">
      <el-col 
        v-for="card in overviewCards" 
        :key="card.label"
        :xs="24" 
        :sm="12" 
        :lg="6"
      >
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon-wrapper" :class="card.type">
              <el-icon :size="32"><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-text">
              <p class="card-label">{{ card.label }}</p>
              <p class="card-value">
                {{ formatNumber(overview[card.field], card.prefix) }}
              </p>
              <p v-if="card.extraField" class="card-extra">
                <el-icon :size="12" class="trend-icon">
                  <CaretTop v-if="overview[card.extraField] > 0" />
                  <CaretBottom v-else-if="overview[card.extraField] < 0" />
                  <Minus v-else />
                </el-icon>
                今日 {{ card.extraLabel }} {{ overview[card.extraField] || 0 }}
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 - 调整优化布局 -->
    <el-row :gutter="24" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card trend-chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <div class="header-left">
                <h3 class="chart-title">订单与销售趋势</h3>
                <p class="chart-subtitle">最近 {{ trendDays }} 天订单趋势</p>
              </div>
              <div class="header-right">
                <el-radio-group v-model="trendDays" size="small" @change="handleTrendChange" class="time-selector">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="14">14天</el-radio-button>
                  <el-radio-button :label="30">30天</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-wrapper">
            <div ref="trendChartRef" class="trend-chart"></div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card pie-chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <div class="header-left">
                <h3 class="chart-title">分类商品占比</h3>
                <p class="chart-subtitle">统计各分类商品数量</p>
              </div>
            </div>
          </template>
          <div class="chart-wrapper">
            <div ref="categoryChartRef" class="pie-chart"></div>
          </div>
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
import { 
  UserFilled, 
  Goods, 
  List, 
  TrendCharts,
  CaretTop,
  CaretBottom,
  Minus 
} from '@element-plus/icons-vue'

const overview = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalSales: 0,
  todayOrders: 0,
  yesterdayOrders: 0
})

const overviewCards = [
  { 
    label: '总用户数', 
    field: 'totalUsers', 
    icon: UserFilled, 
    type: 'primary' 
  },
  { 
    label: '商品总数', 
    field: 'totalProducts', 
    icon: Goods, 
    type: 'success' 
  },
  { 
    label: '订单总数', 
    field: 'totalOrders', 
    icon: List, 
    type: 'warning', 
    extraField: 'todayOrders', 
    extraLabel: '订单' 
  },
  { 
    label: '总销售额', 
    field: 'totalSales', 
    icon: TrendCharts, 
    type: 'info', 
    prefix: '¥' 
  }
]

const trendDays = ref(7)
const trendData = ref([])
const categoryData = ref([])

const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

// 保持你原有的数据加载逻辑
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
  if (!trendChart || !trendData.value.length) return

  const dates = trendData.value.map(item => item.date)
  const orderCounts = trendData.value.map(item => item.orderCount)
  const sales = trendData.value.map(item => Number(item.totalAmount || 0))

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: '#dcdfe6',
      borderWidth: 1,
      padding: [12, 20],
      textStyle: {
        color: '#606266',
        fontSize: 13,
        fontWeight: 'normal'
      },
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#409eff'
        }
      }
    },
    legend: {
      data: ['订单数', '销售额'],
      top: 10,
      right: 30,
      textStyle: {
        fontSize: 13,
        color: '#606266'
      },
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 20
    },
    grid: {
      left: 60,
      right: 40,
      top: 60,
      bottom: 60,
      containLabel: false
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed',
          width: 1
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#909399',
        fontSize: 12,
        margin: 12,
        rotate: 0
      },
      splitLine: {
        show: false
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        nameTextStyle: {
          color: '#909399',
          fontSize: 13,
          padding: [0, 0, 0, 10]
        },
        position: 'left',
        offset: 0,
        splitLine: {
          lineStyle: {
            color: '#f0f2f5',
            type: 'dashed'
          }
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          color: '#909399',
          fontSize: 12,
          margin: 8,
          formatter: function(value) {
            if (value >= 1000) {
              return (value / 1000).toFixed(0) + 'K';
            }
            return value;
          }
        }
      },
      {
        type: 'value',
        name: '销售额',
        nameTextStyle: {
          color: '#909399',
          fontSize: 13,
          padding: [0, 0, 0, -10]
        },
        position: 'right',
        offset: 0,
        splitLine: {
          show: false
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          color: '#909399',
          fontSize: 12,
          margin: 8,
          formatter: function(value) {
            if (value >= 10000) {
              return '¥' + (value / 10000).toFixed(1) + '万';
            }
            return '¥' + value;
          }
        }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#409eff'
        },
        itemStyle: {
          color: '#409eff',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        data: orderCounts
      },
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#67c23a'
        },
        itemStyle: {
          color: '#67c23a',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        data: sales
      }
    ]
  })
}

const renderCategoryChart = () => {
  if (!categoryChart && categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
  }
  if (!categoryChart || !categoryData.value.length) return

  const total = categoryData.value.reduce((sum, item) => sum + item.value, 0)
  const colors = [
    '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399',
    '#9e87f5', '#f7ba2a', '#ff7c7c', '#58d9f9', '#05c091'
  ]

  categoryChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: '#dcdfe6',
      borderWidth: 1,
      padding: [12, 20],
      textStyle: {
        color: '#606266',
        fontSize: 13,
        fontWeight: 'normal'
      },
      formatter: function(params) {
        const percent = ((params.value / total) * 100).toFixed(1)
        return `
          <div style="font-weight: 600; margin-bottom: 6px;">${params.name}</div>
          <div style="color: #909399;">
            数量: <span style="color: #303133; font-weight: 500;">${params.value}</span><br/>
            占比: <span style="color: #303133; font-weight: 500;">${percent}%</span>
          </div>
        `
      }
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: {
        fontSize: 12,
        color: '#606266'
      },
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 8,
      pageTextStyle: {
        color: '#909399'
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '75%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: {
          show: false
        },
        labelLine: {
          show: false
        },
        emphasis: {
          scale: true,
          scaleSize: 10,
          itemStyle: {
            shadowBlur: 20,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          }
        },
        data: categoryData.value.map((item, index) => ({
          name: item.categoryName,
          value: item.value,
          itemStyle: {
            color: colors[index % colors.length]
          }
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
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 统计卡片样式 */
.stat-cards-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  border: none;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  background: white;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #36cfc9);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 2;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(64, 158, 255, 0.15) !important;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card:hover .card-icon-wrapper {
  transform: scale(1.15) translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px !important;
}

.card-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.card-icon-wrapper.primary {
  background: linear-gradient(135deg, #409eff, #36cfc9);
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.3);
}

.card-icon-wrapper.success {
  background: linear-gradient(135deg, #67c23a, #95d475);
  box-shadow: 0 8px 16px rgba(103, 194, 58, 0.3);
}

.card-icon-wrapper.warning {
  background: linear-gradient(135deg, #e6a23c, #f3d19e);
  box-shadow: 0 8px 16px rgba(230, 162, 60, 0.3);
}

.card-icon-wrapper.info {
  background: linear-gradient(135deg, #909399, #c0c4cc);
  box-shadow: 0 8px 16px rgba(144, 147, 153, 0.3);
}

.card-text {
  flex: 1;
}

.card-label {
  margin: 0;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.card-value {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-extra {
  margin: 0;
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.trend-icon {
  margin-right: 4px;
}

/* 图表区域样式 */
.charts-row {
  margin-top: 0;
  height: calc(100vh - 200px);
}

.chart-card {
  border-radius: 16px;
  border: none;
  background: white;
  transition: all 0.3s ease;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1) !important;
}

.trend-chart-card {
  height: 100%;
}

.pie-chart-card {
  height: 100%;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f0f2f5;
  padding: 20px 24px;
  background: white;
  border-radius: 16px 16px 0 0;
  flex-shrink: 0;
}

:deep(.el-card__body) {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
}

.header-left {
  flex: 1;
}

.chart-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.2;
}

.chart-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #6b7280;
  font-weight: 400;
}

.header-right {
  flex-shrink: 0;
}

.time-selector {
  display: flex;
  gap: 4px;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 4px;
}

:deep(.el-radio-button__inner) {
  border-radius: 8px !important;
  padding: 6px 16px;
  border: 1px solid #e4e7ed;
  background: white;
  color: #606266;
  transition: all 0.3s ease;
  font-size: 12px;
}

:deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #409eff, #36cfc9);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 图表容器 */
.chart-wrapper {
  flex: 1;
  padding: 20px;
  position: relative;
  min-height: 400px;
}

.trend-chart {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

.pie-chart {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }
  
  .charts-row {
    height: auto;
  }
  
  .chart-card {
    margin-bottom: 20px;
    height: 500px;
  }
  
  .stat-card {
    margin-bottom: 16px;
  }
  
  .card-content {
    padding: 20px !important;
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .card-icon-wrapper {
    width: 56px;
    height: 56px;
  }
  
  .card-value {
    font-size: 28px;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .chart-wrapper {
    padding: 16px;
    min-height: 350px;
  }
  
  .trend-chart,
  .pie-chart {
    min-height: 350px;
  }
}

@media (max-width: 1200px) {
  .charts-row .el-col {
    margin-bottom: 24px;
  }
  
  .trend-chart-card,
  .pie-chart-card {
    height: 500px;
  }
}

@media (min-width: 1201px) {
  .charts-row {
    height: calc(100vh - 180px);
  }
  
  .trend-chart-card {
    height: 100%;
  }
  
  .pie-chart-card {
    height: 100%;
  }
}
</style>