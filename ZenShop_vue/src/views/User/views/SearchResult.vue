<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <el-input
        v-model="keyword"
        class="search-input"
        placeholder="搜索商品或商家"
        @keyup.enter="resetSearch"
      />
    </div>

        <!-- 排序 & 价格筛选 -->
    <div v-if="activeTab === 'product'" class="filter-bar">
      <div class="filter-row">
        <span
          :class="['sort-btn', { active: sortBy === 'sales' }]"
          @click="toggleSort('sales')"
        >按销量</span>
        <el-popover placement="bottom" :width="280" trigger="click">
          <template #reference>
            <span :class="['sort-btn', { active: isPriceFiltered }]">价格</span>
          </template>
          <div class="price-popover">
            <el-slider
              v-model="priceRange"
              range
              :min="0"
              :max="10000"
              :step="100"
              :format-tooltip="formatPrice"
              class="price-slider"
              @change="onPriceChange"
            />
            <div class="price-text">¥{{ priceRange[0] }} ~ ¥{{ priceRange[1] }}</div>
          </div>
        </el-popover>
      </div>
    </div>

<el-tabs v-model="activeTab" class="result-tabs" :stretch="true" @tab-change="onTabChange">
      <el-tab-pane label="商品" name="product">
        <div class="tab-content" ref="productScrollRef" @scroll="onProductScroll">
          <div v-if="productList.length" class="product-grid">
            <div v-for="item in productList" :key="item.id" class="product-card" @click="goProduct(item.id)">
              <div class="product-img">
                <img v-if="item.images?.length" :src="item.images[0]" />
                <el-icon v-else :size="28"><Goods /></el-icon>
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ item.name }}</h4>
                <div class="product-meta">
                  <span>评分 {{ item.rating ?? '-' }}</span>
                  <span>已售 {{ item.sales || 0 }}</span>
                </div>
                <span class="product-price">¥{{ item.price?.toFixed(2) }}</span>
              </div>
            </div>
          </div>
          <div v-else-if="!productLoading" class="empty-text">暂无商品</div>
          <div class="load-status">{{ productLoading ? '加载中...' : productNoMore ? '没有更多了' : '' }}</div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="商家" name="shop">
        <div class="tab-content" ref="shopScrollRef" @scroll="onShopScroll">
          <div v-if="shopList.length" class="list-wrap">
            <div v-for="item in shopList" :key="item.id" class="result-card">
              <div class="card-img shop-logo">
                <img v-if="item.logo" :src="item.logo" />
                <span v-else class="logo-text">{{ item.name?.charAt(0) }}</span>
              </div>
              <div class="card-info">
                <h4 class="card-name">{{ item.name }}</h4>
                <span class="card-rating">评分: {{ item.rating ?? '暂无' }}</span>
              </div>
            </div>
          </div>
          <div v-else-if="!shopLoading" class="empty-text">暂无商家</div>
          <div class="load-status">{{ shopLoading ? '加载中...' : shopNoMore ? '没有更多了' : '' }}</div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Goods } from '@element-plus/icons-vue'
import { userIndexApi } from '@/api'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const activeTab = ref('product')
const productScrollRef = ref<HTMLDivElement>()
const shopScrollRef = ref<HTMLDivElement>()

// 商品
const productList = ref<any[]>([])
const productPage = ref(1)
const productLoading = ref(false)
const productNoMore = ref(false)
const priceRange = ref([0, 10000])
const sortBy = ref('')
const isPriceFiltered = computed(() => priceRange.value[0] > 0 || priceRange.value[1] < 10000)

function formatPrice(val: number) { return '¥' + val }

function onPriceChange() {
  productPage.value = 1; productNoMore.value = false; productList.value = []
  doSearch(true)
}

function toggleSort(field: string) {
  if (sortBy.value === field) { sortBy.value = ''; return }
  sortBy.value = field
  productPage.value = 1; productNoMore.value = false; productList.value = []
  doSearch(true)
}

// 商家
const shopList = ref<any[]>([])
const shopPage = ref(1)
const shopLoading = ref(false)
const shopNoMore = ref(false)

function resetSearch() {
  productPage.value = 1; productNoMore.value = false; productList.value = []
  shopPage.value = 1; shopNoMore.value = false; shopList.value = []
  doSearch(true)
}

function goProduct(id: number) {
  router.push(`/user/product?id=${id}`)
}

onMounted(() => {
  keyword.value = (route.query.keyword as string) || ''
  if (keyword.value) doSearch()
})

async function doSearch(reset = false) {
  if (!keyword.value.trim()) return
  if (activeTab.value === 'product') {
    if (productLoading.value || productNoMore.value) return
    productLoading.value = true
    try {
      const params: any = {
        name: keyword.value.trim(),
        page: reset ? 1 : productPage.value,
        size: 10,
      }
      if (priceRange.value[0] > 0) params.minPrice = priceRange.value[0]
      if (priceRange.value[1] < 10000) params.maxPrice = priceRange.value[1]
      if (sortBy.value) params.salesSort = 'desc'
      const res: any = await userIndexApi.getProductPage(params)
      const records = res?.records || []
      if (reset) {
        productList.value = records
        productPage.value = 2
      } else {
        productList.value.push(...records)
        productPage.value++
      }
      if (records.length < 10) productNoMore.value = true
    } catch { productList.value = [] }
    finally { productLoading.value = false }
  } else {
    if (shopLoading.value || shopNoMore.value) return
    shopLoading.value = true
    try {
      const res: any = await userIndexApi.getBusinessPage({
        name: keyword.value.trim(),
        page: reset ? 1 : shopPage.value,
        size: 10,
      })
      const records = res?.records || []
      if (reset) {
        shopList.value = records
        shopPage.value = 2
      } else {
        shopList.value.push(...records)
        shopPage.value++
      }
      if (records.length < 10) shopNoMore.value = true
    } catch { shopList.value = [] }
    finally { shopLoading.value = false }
  }
}

function onTabChange() {
  productPage.value = 1; productNoMore.value = false
  shopPage.value = 1; shopNoMore.value = false
  if (keyword.value.trim()) doSearch(true)
}

function onProductScroll() {
  const el = productScrollRef.value
  if (!el || productLoading.value || productNoMore.value) return
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) doSearch()
}

function onShopScroll() {
  const el = shopScrollRef.value
  if (!el || shopLoading.value || shopNoMore.value) return
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) doSearch()
}
</script>

<style scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.search-input { flex: 1; }

.search-input :deep(.el-input__wrapper) {
  background: #f5f5f5;
  border-radius: 20px;
}

.result-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  overflow: hidden;
}

.result-tabs :deep(.el-tabs__header) { margin: 0; flex-shrink: 0; }
.result-tabs :deep(.el-tabs__content) { flex: 1; overflow: hidden; }
.result-tabs :deep(.el-tab-pane) { height: 100%; }

/* 筛选栏 */
.filter-bar {
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.filter-row {
  display: flex;
  gap: 10px;
}

.sort-btn {
  padding: 4px 14px;
  border-radius: 14px;
  font-size: 13px;
  color: #606266;
  background: #f5f5f5;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-btn.active {
  background: #409eff;
  color: #fff;
}

/* 价格弹窗 */
.price-popover {
  padding: 8px 4px;
}

.price-slider {
  padding: 0 4px;
}

.price-text {
  font-size: 12px;
  color: #909399;
  text-align: center;
  margin-top: 4px;
}

.tab-content {
  height: 100%;
  overflow-y: auto;
}

/* 商品两列网格 */
.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  padding: 12px 16px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.product-img {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.product-img img { width: 100%; height: 100%; object-fit: cover; }

.product-info { padding: 8px 10px 10px; }

.product-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: #909399;
  margin-bottom: 4px;
}

.product-price { font-size: 15px; font-weight: 600; color: #f56c6c; }

.list-wrap { padding: 12px 16px; }

.result-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.card-img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  flex-shrink: 0;
}

.card-img img { width: 100%; height: 100%; object-fit: cover; }

.shop-logo { background: #f0f2ff; color: #667eea; }
.logo-text { font-size: 20px; font-weight: 600; }

.card-info { flex: 1; min-width: 0; }

.card-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 12px;
  color: #909399;
  margin: 2px 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta { display: flex; gap: 12px; margin: 2px 0 4px; }
.card-meta-item { font-size: 12px; color: #909399; }
.card-price { font-size: 16px; font-weight: 600; color: #f56c6c; }
.card-rating { font-size: 13px; color: #909399; }

.empty-text { text-align: center; color: #909399; font-size: 14px; padding: 60px 0; }

.load-status { text-align: center; padding: 16px; font-size: 13px; color: #909399; }
</style>
