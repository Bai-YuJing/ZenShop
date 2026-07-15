<template>
  <div class="page" ref="scrollRef" @scroll="onScroll">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>{{ categoryName }}</h2>
    </div>

    <div class="filter-bar">
      <div class="region-row">
        <el-select v-model="provinceId" placeholder="省" clearable class="region-select" @change="onProvinceChange">
          <el-option v-for="p in regionData" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
        <el-select v-model="cityId" placeholder="市" clearable class="region-select" @change="onCityChange">
          <el-option v-for="c in cityList" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
        <el-select v-model="districtId" placeholder="区" clearable class="region-select">
          <el-option v-for="d in districtList" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </div>
    </div>

    <div class="list-wrap">
      <div v-for="item in businessList" :key="item.id" class="shop-card" @click="goShop(item)">
        <div class="shop-logo">
          <img v-if="item.logo" :src="item.logo" />
          <span v-else class="logo-placeholder">{{ item.name?.charAt(0) || '商' }}</span>
        </div>
        <div class="shop-info">
          <h4 class="shop-name">{{ item.name }}</h4>
          <div class="shop-meta">
            <span class="shop-rating">评分: {{ item.rating ?? '暂无' }}</span>
            <span class="shop-category">{{ item.categoryName }}</span>
            <span class="shop-region">{{ getRegionText(item) }}</span>
          </div>
          <p class="shop-desc">{{ item.description || '暂无简介' }}</p>
        </div>
      </div>
      <div v-if="loading" class="load-status">加载中...</div>
      <div v-if="!loading && noMore" class="load-status">没有更多了</div>
      <p v-if="!loading && businessList.length === 0" class="empty-text">暂无商家数据</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { regionData } from '@/data/region-data'
import { userIndexApi } from '@/api'

const route = useRoute()
const router = useRouter()
const categoryName = ref('')
const categoryId = ref('')
const loading = ref(false)
const businessList = ref<any[]>([])
const page = ref(1)
const noMore = ref(false)
const scrollRef = ref<HTMLDivElement>()

// 省市区
const provinceId = ref('')
const cityId = ref('')
const districtId = ref('')

const cityList = computed(() => {
  if (!provinceId.value) return []
  const p = regionData.find(item => item.value === provinceId.value)
  return p?.children || []
})

const districtList = computed(() => {
  if (!cityId.value) return []
  const c = cityList.value.find(item => item.value === cityId.value)
  return c?.children || []
})

async function fetchList(reset = false) {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const params: any = {
      page: reset ? 1 : page.value,
      size: 10,
      categoryId: Number(categoryId.value),
    }
    if (provinceId.value) params.provinceId = Number(provinceId.value)
    if (cityId.value) params.cityId = Number(cityId.value)
    if (districtId.value) params.districtId = Number(districtId.value)

    const res: any = await userIndexApi.getBusinessPage(params)
    const records = res.records || res.list || []
    if (reset) {
      businessList.value = records
      page.value = 2
    } else {
      businessList.value.push(...records)
      page.value++
    }
    if (records.length < 10) noMore.value = true
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

function resetAndFetch() {
  page.value = 1
  noMore.value = false
  businessList.value = []
  fetchList(true)
}

onMounted(() => {
  categoryId.value = (route.query.id as string) || ''
  categoryName.value = (route.query.name as string) || '商家列表'
  fetchList(true)
})

function onProvinceChange() {
  cityId.value = ''
  districtId.value = ''
  resetAndFetch()
}

function onCityChange() {
  districtId.value = ''
  resetAndFetch()
}

function goShop(item: any) {
  router.push(`/user/business?id=${encodeURIComponent(item.id)}`)
}

function getRegionText(item: any) {
  if (!item.provinceId) return ''
  const p = regionData.find(r => r.value === String(item.provinceId))
  const c = p?.children?.find(r => r.value === String(item.cityId))
  const d = c?.children?.find(r => r.value === String(item.districtId))
  return [p?.label, c?.label, d?.label].filter(Boolean).join(' / ') || ''
}

function onScroll() {
  const el = scrollRef.value
  if (!el || loading.value || noMore.value) return
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
    fetchList()
  }
}
</script>

<style scoped>
.page {
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.filter-bar {
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 48px;
  z-index: 10;
}

.region-row {
  display: flex;
  gap: 8px;
}

.region-select {
  flex: 1;
  min-width: 0;
}

.list-wrap {
  padding: 12px 16px;
}

.shop-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  cursor: pointer;
  background: #fff;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.shop-logo {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f2ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.shop-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-placeholder {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.shop-info {
  flex: 1;
  min-width: 0;
}

.shop-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px;
}

.shop-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.shop-desc {
  font-size: 13px;
  color: #606266;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.load-status {
  text-align: center;
  padding: 16px;
  font-size: 13px;
  color: #909399;
}

.empty-text {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 60px;
}
</style>
