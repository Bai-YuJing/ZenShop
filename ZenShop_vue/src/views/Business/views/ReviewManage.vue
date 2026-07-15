<template>
  <div class="review-page">
    <div class="breadcrumb-bar">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/business/center/profile' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/business/center/product' }">商品管理</el-breadcrumb-item>
        <el-breadcrumb-item>商品评价</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="page-header"><h2>商品评价</h2></div>

    <div class="sort-bar">
      <span :class="['sort-btn', { active: sortBy === 'time' }]" @click="toggleSort('time')">
        按时间 {{ sortBy === 'time' ? (sortOrder === 'DESC' ? '↓' : '↑') : '' }}
      </span>
      <span :class="['sort-btn', { active: sortBy === 'score' }]" @click="toggleSort('score')">
        按评分 {{ sortBy === 'score' ? (sortOrder === 'DESC' ? '↓' : '↑') : '' }}
      </span>
    </div>

    <div class="list-wrap" v-loading="loading">
      <div v-for="r in ratings" :key="r.id" class="rating-item">
        <div class="rating-header">
          <span class="rating-user">{{ r.username || '匿名用户' }}</span>
          <el-rate :model-value="r.score ?? 0" disabled :max="5" size="small" />
          <span class="rating-time">{{ r.createdTime || '' }}</span>
        </div>
        <p class="rating-content">{{ r.content || '暂无评价内容' }}</p>
      </div>
      <p v-if="!loading && !ratings.length" class="empty">暂无评价</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { businessRatingApi } from '@/api'

const route = useRoute()
const loading = ref(false)
const ratings = ref<any[]>([])
const sortBy = ref('time')
const sortOrder = ref('DESC')

async function fetchRatings() {
  const productId = Number(route.query.id)
  if (!productId) return
  loading.value = true
  try {
    const res: any = await businessRatingApi.page({ productId, sortBy: sortBy.value, sortOrder: sortOrder.value, page: 0, size: 999 })
    ratings.value = res?.records || res?.list || []
  } catch { ratings.value = [] }
  finally { loading.value = false }
}

function toggleSort(field: string) {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'DESC' ? 'ASC' : 'DESC'
  } else {
    sortBy.value = field
    sortOrder.value = 'DESC'
  }
  fetchRatings()
}

onMounted(fetchRatings)

onMounted(async () => {
  const productId = Number(route.query.id)
  if (!productId) return
  loading.value = true
  try {
    const res: any = await businessRatingApi.page({ productId, sortBy: 'time', sortOrder: 'DESC', page: 0, size: 999 })
    ratings.value = res?.records || res?.list || []
  } catch { ratings.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.review-page { padding:24px; height:100%; display:flex; flex-direction:column; box-sizing:border-box; }
.breadcrumb-bar { margin-bottom:12px; }
.page-header { margin-bottom:20px; flex-shrink:0; }
.page-header h2 { font-size:22px; font-weight:600; color:#303133; margin:0; }

.list-wrap { flex:1; background:#fff; border-radius:8px; padding:16px; box-shadow:0 2px 12px rgba(0,0,0,0.06); overflow-y:auto; }
.sort-bar { display:flex; align-items:center; gap:10px; margin-bottom:12px; }
.sort-btn { padding:4px 14px; border-radius:14px; font-size:13px; color:#606266; background:#f5f5f5; cursor:pointer; transition:all 0.2s; }
.sort-btn.active { background:#409eff; color:#fff; }
.sort-order { font-size:18px; color:#909399; cursor:pointer; padding:4px; }

.rating-item { padding:16px 0; border-bottom:1px solid #f0f0f0; }
.rating-item:last-child { border-bottom:none; }
.rating-header { display:flex; align-items:center; gap:12px; margin-bottom:8px; }
.rating-user { font-size:14px; font-weight:500; color:#303133; }
.rating-time { font-size:12px; color:#909399; margin-left:auto; }
.rating-content { font-size:13px; color:#606266; margin:0; line-height:1.5; }
.empty { text-align:center; color:#909399; font-size:14px; padding:60px 0; }
</style>
