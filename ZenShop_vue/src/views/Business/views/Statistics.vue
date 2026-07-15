<template>
  <div class="stats-page">
    <div class="page-header"><h2>统计报表</h2></div>

    <div class="charts-row">
      <!-- 订单统计 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">订单统计</h3>
          <el-radio-group v-model="orderRange" size="small" @change="fetchData">
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="orderChartRef" class="chart-box"></div>
      </div>

      <!-- 评论统计 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">评论统计</h3>
          <el-radio-group v-model="ratingRange" size="small" @change="fetchData">
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="ratingChartRef" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { businessStatsApi } from '@/api'

const orderChartRef = ref<HTMLDivElement>()
const ratingChartRef = ref<HTMLDivElement>()
const orderRange = ref('7d')
const ratingRange = ref('7d')
const orderData = ref<any[]>([])
const ratingData = ref<any[]>([])
let orderChart: echarts.ECharts | null = null
let ratingChart: echarts.ECharts | null = null

async function fetchData() {
  try {
    const [oRes, rRes] = await Promise.all([
      businessStatsApi.orderStats({ range: orderRange.value }),
      businessStatsApi.ratingStats({ range: ratingRange.value }),
    ])
    orderData.value = (oRes as any[]) || []
    ratingData.value = (rRes as any[]) || []
  } catch {
    orderData.value = []
    ratingData.value = []
  }
  updateOrderChart()
  updateRatingChart()
}

function getDays(range: string): string[] {
  const days: string[] = []
  const today = new Date()
  let start: Date
  if (range === 'week') {
    const day = today.getDay()
    const diff = day === 0 ? 6 : day - 1
    start = new Date(today)
    start.setDate(today.getDate() - diff)
  } else if (range === 'month') {
    start = new Date(today.getFullYear(), today.getMonth(), 1)
  } else {
    const n = Number(range.replace('d', ''))
    start = new Date(today)
    start.setDate(today.getDate() - (n - 1))
  }
  const end = new Date(today)
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    days.push((d.getMonth() + 1) + '/' + d.getDate())
  }
  return days
}

function initOrderChart() {
  if (!orderChartRef.value) return
  orderChart = echarts.init(orderChartRef.value)
  updateOrderChart()
}

function updateOrderChart() {
  const data = orderData.value.length ? orderData.value : []
  const days = data.length ? data.map((d: any) => d.date.substring(5)) : getDays(orderRange.value)
  const counts = data.length ? data.map((d: any) => d.orderCount) : []
  const revenues = data.length ? data.map((d: any) => d.revenue) : []
  orderChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '金额'], bottom: 0, icon: 'circle', itemWidth: 8 },
    xAxis: { type: 'category', data: days, axisLabel: { fontSize: 11 } },
    yAxis: [{ type: 'value', name: '订单数' }, { type: 'value', name: '金额(元)' }],
    series: [
      { name: '订单数', type: 'line', smooth: true, data: counts, lineStyle: { color: '#409eff', width: 2 }, itemStyle: { color: '#409eff' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.3)' }, { offset: 1, color: 'rgba(64,158,255,0.05)' }]) } },
      { name: '金额', type: 'line', smooth: true, data: revenues, yAxisIndex: 1, lineStyle: { color: '#f56c6c', width: 2 }, itemStyle: { color: '#f56c6c' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(245,108,108,0.3)' }, { offset: 1, color: 'rgba(245,108,108,0.05)' }]) } },
    ],
    grid: { left: '8%', right: '8%', top: '8%', bottom: '18%' },
  }, true)
}

function initRatingChart() {
  if (!ratingChartRef.value) return
  ratingChart = echarts.init(ratingChartRef.value)
  updateRatingChart()
}

function updateRatingChart() {
  const data = ratingData.value.length ? ratingData.value : []
  const days = data.length ? data.map((d: any) => d.date.substring(5)) : getDays(ratingRange.value)
  const good = data.length ? data.map((d: any) => d.goodCount || 0) : []
  const medium = data.length ? data.map((d: any) => d.mediumCount || 0) : []
  const bad = data.length ? data.map((d: any) => d.badCount || 0) : []
  ratingChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['好评', '中评', '差评'], bottom: 0, icon: 'circle', itemWidth: 8 },
    xAxis: { type: 'category', data: days, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value' },
    series: [
      { name: '好评', type: 'line', smooth: true, data: good, lineStyle: { color: '#67c23a', width: 2 }, itemStyle: { color: '#67c23a' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(103,194,58,0.3)' }, { offset: 1, color: 'rgba(103,194,58,0.05)' }]) } },
      { name: '中评', type: 'line', smooth: true, data: medium, lineStyle: { color: '#e6a23c', width: 2 }, itemStyle: { color: '#e6a23c' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(230,162,60,0.3)' }, { offset: 1, color: 'rgba(230,162,60,0.05)' }]) } },
      { name: '差评', type: 'line', smooth: true, data: bad, lineStyle: { color: '#f56c6c', width: 2 }, itemStyle: { color: '#f56c6c' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(245,108,108,0.3)' }, { offset: 1, color: 'rgba(245,108,108,0.05)' }]) } },
    ],
    grid: { left: '8%', right: '6%', top: '8%', bottom: '18%' },
  }, true)
}

onMounted(() => {
  initOrderChart()
  initRatingChart()
  fetchData()
})

onUnmounted(() => {
  orderChart?.dispose()
  ratingChart?.dispose()
})

window.addEventListener('resize', () => {
  orderChart?.resize()
  ratingChart?.resize()
})
</script>

<style scoped>
.stats-page { padding:24px; height:100%; display:flex; flex-direction:column; box-sizing:border-box; overflow-y:auto; }
.page-header { margin-bottom:20px; flex-shrink:0; }
.page-header h2 { font-size:22px; font-weight:600; color:#303133; margin:0; }

.charts-row { display:flex; gap:16px; }
.chart-card { flex:1; background:#fff; border-radius:8px; padding:20px; box-shadow:0 2px 12px rgba(0,0,0,0.06); min-width:0; }
.chart-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.chart-title { font-size:16px; font-weight:600; color:#303133; margin:0; }
.chart-box { width:100%; height:300px; }
</style>
