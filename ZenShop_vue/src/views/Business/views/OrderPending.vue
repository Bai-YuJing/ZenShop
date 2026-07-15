<template>
  <div class="order-page">
    <div class="page-header">
      <h2>未完成订单</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable class="filter-select">
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
      </el-select>
    </div>
    <div class="table-card">
      <el-table :data="filteredList" style="width:100%" v-loading="loading" stripe header-cell-class-name="table-header" @sort-change="handleSortChange">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单编号" min-width="140" />
        <el-table-column label="商品" min-width="160">
          <template #default="{ row }">
            <div v-for="item in (row.items || [])" :key="item.productId" class="order-product">
              <img v-if="item.productImage" :src="item.productImage" class="order-img" />
              <span class="order-name">{{ item.productName }} × {{ item.quantity }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="金额" width="100" align="center" sortable />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="primary" size="small" @click="handleShip(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" @current-change="onPageChange" />
      </div>
      <p v-if="!loading && !list.length" class="empty">暂无未完成订单</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { businessOrderApi } from '@/api'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = 15
const statusFilter = ref<number | null>(null)

const filteredList = computed(() => {
  if (statusFilter.value == null) return list.value
  return list.value.filter(item => item.status === statusFilter.value)
})

function onPageChange(p: number) {
  page.value = p
  fetchList()
}

const statusMap: Record<number, { text: string; type: string }> = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '待收货', type: 'info' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' },
  5: { text: '取消审核', type: 'warning' },
  6: { text: '取消失败', type: 'danger' },
}
function statusText(s: number) { return statusMap[s]?.text || '未知' }
function statusType(s: number) { return statusMap[s]?.type || 'info' }

async function handleShip(row: any) {
  try {
    await ElMessageBox.confirm(`确认发货订单 ${row.orderNo}？`, '发货', { type: 'info' })
    await businessOrderApi.deliver(row.id)
    ElMessage.success('发货成功')
    row.status = 2
  } catch { /* 取消或错误都不处理 */ }
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await businessOrderApi.unfinished({ page: page.value, size })
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
.page-header { margin-bottom:20px; flex-shrink:0; display:flex; align-items:center; justify-content:space-between; }
.page-header h2 { font-size:22px; font-weight:600; color:#303133; margin:0; }
.filter-select { width:140px; }
.table-card { flex:1; background:#fff; border-radius:8px; padding:16px 0; box-shadow:0 2px 12px rgba(0,0,0,0.06); overflow-y:auto; }
.table-card :deep(.table-header) { background:#fff; color:#303133; font-weight:600; }
.order-product { display:flex; align-items:center; gap:8px; padding:4px 0; }
.order-img { width:36px; height:36px; border-radius:4px; object-fit:cover; }
.order-name { font-size:13px; color:#303133; }
.empty { text-align:center; color:#909399; font-size:14px; margin-top:60px; }
.pagination-wrap { display:flex; justify-content:center; padding:16px 0; }
</style>
