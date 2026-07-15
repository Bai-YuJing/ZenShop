<template>
  <div class="order-page">
    <div class="page-header">
      <h2>已完成订单</h2>
      <div class="filter-group">
        <el-input v-model="productFilter" placeholder="商品名称" clearable class="filter-input" @change="fetchList" />
        <el-select v-model="reviewFilter" placeholder="评价状态" clearable class="filter-rating" @change="fetchList">
          <el-option label="已评价" :value="1" />
          <el-option label="未评价" :value="0" />
        </el-select>
        <el-select v-model="ratingFilter" placeholder="评分" clearable class="filter-rating" @change="fetchList">
          <el-option label="好评 (4-5)" :value="'good'" />
          <el-option label="中评 (2-4)" :value="'mid'" />
          <el-option label="差评 (0-2)" :value="'bad'" />
        </el-select>
      </div>
    </div>
    <div class="table-card">
      <el-table :data="list" style="width:100%" v-loading="loading" stripe header-cell-class-name="table-header">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单编号" min-width="140" />
        <el-table-column label="商品" min-width="180">
          <template #default="{ row }">
            <div v-for="item in (row.items || [])" :key="item.productId" class="order-product">
              <div class="product-img-wrap">
                <img v-if="item.productImage" :src="item.productImage" class="order-img" />
              </div>
              <span class="order-name">{{ item.productName }} × {{ item.quantity }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="金额" width="100" align="center" sortable />
        <el-table-column prop="createdTime" label="下单时间" width="180" />
        <el-table-column label="评价" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasRating(row)" text type="primary" size="small" @click="viewRating(row)">查看评价</el-button>
            <span v-else class="no-rating">未评价</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" @current-change="onPageChange" />
      </div>
      <p v-if="!loading && !list.length" class="empty">暂无已完成订单</p>
    </div>

    <el-dialog v-model="dialogVisible" title="评价详情" width="500px">
      <div v-for="item in (dialogData?.items || [])" :key="item.productId" class="rating-item">
        <div class="rating-header">
          <span class="rating-product">{{ item.productName }}</span>
          <el-tag v-if="item.score != null" size="small" type="success">{{ item.score }}分</el-tag>
          <el-tag v-else size="small" type="info">未评价</el-tag>
        </div>
        <p v-if="item.ratingContent" class="rating-content">{{ item.ratingContent }}</p>
        <p v-else class="rating-empty">暂无评价内容</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { businessOrderApi } from '@/api'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = 15
const productFilter = ref('')
const ratingFilter = ref<string | null>(null)
const reviewFilter = ref<number | null>(null)

const dialogVisible = ref(false)
const dialogData = ref<any>(null)

function hasRating(row: any) {
  return (row.items || []).some((i: any) => i.score != null)
}

function viewRating(row: any) {
  dialogData.value = row
  dialogVisible.value = true
}

function onPageChange(p: number) {
  page.value = p
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const params: any = { page: page.value, size }
    if (productFilter.value) params.productName = productFilter.value
    if (ratingFilter.value === 'good') { params.minScore = 4; params.maxScore = 5 }
    if (ratingFilter.value === 'mid') { params.minScore = 2; params.maxScore = 4 }
    if (ratingFilter.value === 'bad') { params.minScore = 0; params.maxScore = 2 }
    if (reviewFilter.value != null) params.rated = reviewFilter.value
    const res: any = await businessOrderApi.completed(params)
    const data = res?.data || res
    list.value = (data?.records || []) as any[]
    total.value = data?.total || 0
  } catch { list.value = []; total.value = 0 }
  finally { loading.value = false }
}

onMounted(fetchList)
</script>

<style scoped>
.order-page { padding: 24px; height:100%; display:flex; flex-direction:column; box-sizing:border-box; }
.page-header { margin-bottom:20px; flex-shrink:0; }
.page-header h2 { font-size:22px; font-weight:600; color:#303133; margin:0; }
.table-card { flex:1; background:#fff; border-radius:8px; padding:16px 0; box-shadow:0 2px 12px rgba(0,0,0,0.06); overflow-y:auto; }
.table-card :deep(.table-header) { background:#fff; color:#303133; font-weight:600; }

.filter-group { display:flex; gap:8px; }
.filter-input { width:160px; }
.filter-rating { width:110px; }
.order-product { display:flex; align-items:center; gap:8px; padding:3px 0; }
.product-img-wrap { width:36px; height:36px; border-radius:4px; overflow:hidden; background:#f5f5f5; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
.order-img { width:100%; height:100%; object-fit:cover; }
.order-name { font-size:13px; color:#303133; }
.no-rating { font-size:13px; color:#909399; }
.empty { text-align:center; color:#909399; font-size:14px; margin-top:60px; }
.pagination-wrap { display:flex; justify-content:center; padding:16px 0; }

.rating-item { padding:12px 0; border-bottom:1px solid #f0f0f0; }
.rating-item:last-child { border-bottom:none; }
.rating-header { display:flex; align-items:center; gap:8px; margin-bottom:4px; }
.rating-product { font-size:14px; font-weight:500; color:#303133; }
.rating-content { font-size:13px; color:#606266; margin:0; line-height:1.5; }
.rating-empty { font-size:13px; color:#909399; margin:0; }
</style>
