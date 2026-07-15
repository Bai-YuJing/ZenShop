<template>
  <div class="stats-page">
    <el-card shadow="never" class="tabs-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="用户统计" name="user">
          <div v-loading="userLoading">
            <div class="toolbar">
              <el-radio-group v-model="userRange" @change="loadUserStats">
                <el-radio-button value="7d">近7天</el-radio-button>
                <el-radio-button value="30d">近30天</el-radio-button>
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
              </el-radio-group>
            </div>
            <el-row :gutter="16" class="stats-row">
              <el-col :span="8">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">用户总数</div>
                  <div class="stat-value">{{ userData.total }}</div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">期间新增</div>
                  <div class="stat-value accent">{{ userData.newTotal }}</div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">日均新增</div>
                  <div class="stat-value accent2">{{ userData.dailyAvg }}</div>
                </el-card>
              </el-col>
            </el-row>
            <el-card shadow="never" class="chart-card">
              <div class="chart-title">用户统计趋势</div>
              <div ref="userChartRef" class="chart-box"></div>
            </el-card>
          </div>
        </el-tab-pane>
        <el-tab-pane label="商家统计" name="business">
          <div v-loading="bizLoading">
            <div class="toolbar">
              <el-radio-group v-model="bizRange" @change="loadBizStats">
                <el-radio-button value="7d">近7天</el-radio-button>
                <el-radio-button value="30d">近30天</el-radio-button>
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
              </el-radio-group>
            </div>
            <el-row :gutter="16" class="stats-row">
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">商家总数</div>
                  <div class="stat-value">{{ bizData.total }}</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">期间新增</div>
                  <div class="stat-value accent">{{ bizData.newTotal }}</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">待审核</div>
                  <div class="stat-value warning">{{ bizData.pendingCount }}</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">正常运营</div>
                  <div class="stat-value success">{{ bizData.activeCount }}</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">已冻结</div>
                  <div class="stat-value danger">{{ bizData.frozenCount }}</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-label">日均新增</div>
                  <div class="stat-value accent2">{{ bizData.dailyAvg }}</div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="14">
                <el-card shadow="never" class="chart-card">
                  <div class="chart-title">商家统计趋势</div>
                  <div ref="bizChartRef" class="chart-box"></div>
                </el-card>
              </el-col>
              <el-col :span="10">
                <el-card shadow="never" class="chart-card">
                  <div class="chart-title">状态分布</div>
                  <div ref="bizPieRef" class="chart-box"></div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { statsApi } from '@/api'

const activeTab = ref('user')
const userRange = ref('7d')
const bizRange = ref('7d')

// ============ 用户统计 ============
const userLoading = ref(false)
const userChartRef = ref<HTMLDivElement>()

const userData = reactive({
  total: 0,
  activeTotal: 0,
  newTotal: 0,
  dailyAvg: 0,
  dates: [] as string[],
  totals: [] as number[],
  actives: [] as number[],
  news: [] as number[],
})

async function loadUserStats() {
  userLoading.value = true
  try {
    const res = (await statsApi.user(userRange.value)) as any
    const list = Array.isArray(res) ? res : []
    userData.dates = list.map((d: any) => d.date ?? '')
    // 支持 totalUsers/totalBusinesses 两种字段名
    userData.totals = list.map((d: any) => d.totalUsers ?? d.totalBusinesses ?? 0)
    userData.actives = list.map((d: any) => d.activeUsers ?? 0)
    userData.news = list.map((d: any) => d.newUsers ?? d.newBusinesses ?? 0)
    userData.total = userData.totals.length ? userData.totals[userData.totals.length - 1] ?? 0 : 0
    userData.activeTotal = userData.actives.length ? userData.actives[userData.actives.length - 1] ?? 0 : 0
    userData.newTotal = userData.news.reduce((a: number, b: number) => a + b, 0)
    userData.dailyAvg = userData.dates.length ? Math.round(userData.newTotal / userData.dates.length) : 0
  } catch {
    // ignore
  } finally {
    userLoading.value = false
    nextTick(renderUserCharts)
  }
}

const userChart = { value: null as echarts.ECharts | null }

function renderUserCharts() {
  if (!userChartRef.value) return
  userChart.value?.dispose()
  userChart.value = echarts.init(userChartRef.value)
  userChart.value.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['用户总数', '活跃用户', '每日新增'], bottom: 0 },
    grid: { left: 50, right: 20, bottom: 40, top: 20 },
    xAxis: { type: 'category', data: userData.dates.length ? userData.dates : ['暂无数据'], axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '用户总数',
        type: 'line',
        smooth: true,
        data: userData.totals.length ? userData.totals : [0],
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' },
      },
      {
        name: '活跃用户',
        type: 'line',
        smooth: true,
        data: userData.actives.length ? userData.actives : [0],
        lineStyle: { color: '#e6a23c', width: 2, type: 'dashed' },
        itemStyle: { color: '#e6a23c' },
      },
      {
        name: '每日新增',
        type: 'line',
        smooth: true,
        data: userData.news.length ? userData.news : [0],
        lineStyle: { color: '#67c23a', width: 2 },
        itemStyle: { color: '#67c23a' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103,194,58,0.25)' },
            { offset: 1, color: 'rgba(103,194,58,0.02)' },
          ]),
        },
      },
    ],
  })
}

// ============ 商家统计 ============
const bizLoading = ref(false)
const bizChartRef = ref<HTMLDivElement>()
const bizPieRef = ref<HTMLDivElement>()
const bizChart = { value: null as echarts.ECharts | null }
const bizPieChart = { value: null as echarts.ECharts | null }
const bizData = reactive({
  total: 0,
  newTotal: 0,
  dailyAvg: 0,
  pendingCount: 0,
  activeCount: 0,
  frozenCount: 0,
  dates: [] as string[],
  totals: [] as number[],
  news: [] as number[],
})

async function loadBizStats() {
  bizLoading.value = true
  try {
    const res = (await statsApi.business(bizRange.value)) as any
    bizData.total = res.currentTotal ?? 0
    bizData.pendingCount = res.pendingCount ?? 0
    bizData.activeCount = res.activeCount ?? 0
    bizData.frozenCount = res.frozenCount ?? 0
    const daily = Array.isArray(res.dailyStats) ? res.dailyStats : []
    bizData.dates = daily.map((d: any) => d.date ?? '')
    bizData.totals = daily.map((d: any) => d.totalBusinesses ?? 0)
    bizData.news = daily.map((d: any) => d.newBusinesses ?? 0)
    bizData.newTotal = bizData.news.reduce((a: number, b: number) => a + b, 0)
    bizData.dailyAvg = bizData.dates.length ? Math.round(bizData.newTotal / bizData.dates.length) : 0
  } catch {
    // ignore
  } finally {
    bizLoading.value = false
    nextTick(renderBizCharts)
  }
}

function renderBizCharts() {
  // 综合趋势图
  if (bizChartRef.value) {
    bizChart.value?.dispose()
    bizChart.value = echarts.init(bizChartRef.value)
    bizChart.value.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['商家总数', '每日新增'], bottom: 0 },
      grid: { left: 50, right: 20, bottom: 40, top: 20 },
      xAxis: { type: 'category', data: bizData.dates.length ? bizData.dates : ['暂无数据'], axisLabel: { fontSize: 11 } },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '商家总数',
          type: 'line', smooth: true,
          data: bizData.totals.length ? bizData.totals : [0],
          lineStyle: { color: '#e6a23c', width: 2 },
          itemStyle: { color: '#e6a23c' },
        },
        {
          name: '每日新增',
          type: 'line', smooth: true,
          data: bizData.news.length ? bizData.news : [0],
          lineStyle: { color: '#67c23a', width: 2 },
          itemStyle: { color: '#67c23a' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103,194,58,0.25)' },
              { offset: 1, color: 'rgba(103,194,58,0.02)' },
            ]),
          },
        },
      ],
    })
  }
  // 状态分布饼图
  if (bizPieRef.value) {
    bizPieChart.value?.dispose()
    bizPieChart.value = echarts.init(bizPieRef.value)
    bizPieChart.value.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['40%', '65%'],
        data: [
          { name: '待审核', value: bizData.pendingCount },
          { name: '正常', value: bizData.activeCount },
          { name: '冻结', value: bizData.frozenCount },
        ].filter(d => d.value > 0),
        label: { formatter: '{b}\n{d}%' },
        color: ['#e6a23c', '#67c23a', '#f56c6c'],
      }],
    })
  }
}

// ============ 生命周期 ============
onMounted(() => {
  loadUserStats()
  loadBizStats()
})

watch(activeTab, () => {
  nextTick(() => {
    if (activeTab.value === 'user') renderUserCharts()
    else renderBizCharts()
  })
})

watch(userRange, () => {
  loadUserStats()
})

onBeforeUnmount(() => {
  userChart.value?.dispose()
  bizChart.value?.dispose()
  bizPieChart.value?.dispose()
})

window.addEventListener('resize', () => {
  userChart.value?.resize()
  bizChart.value?.resize()
  bizPieChart.value?.resize()
})
</script>

<style scoped>
.stats-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.tabs-card {
  border-radius: 8px;
}
.toolbar {
  margin-bottom: 16px;
}
.stats-row {
  margin-bottom: 16px;
}
.stat-card {
  border-radius: 8px;
  text-align: center;
  padding: 4px 0;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}
.stat-value.accent { color: #409eff; }
.stat-value.accent2 { color: #67c23a; }
.stat-value.warning { color: #e6a23c; }
.stat-value.success { color: #67c23a; }
.chart-card {
  border-radius: 8px;
  margin-bottom: 16px;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.chart-box {
  width: 100%;
  height: 280px;
}
</style>
