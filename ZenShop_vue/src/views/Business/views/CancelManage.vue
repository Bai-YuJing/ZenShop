<template>
  <div class="order-page">
    <div class="page-header">
      <h2>取消管理</h2>
      <el-select v-model="statusFilter" placeholder="订单状态" clearable class="filter-select" @change="fetchList">
        <el-option label="已取消" :value="4" />
        <el-option label="取消审核" :value="5" />
        <el-option label="取消失败" :value="6" />
      </el-select>
    </div>
    <div class="table-card">
      <el-table :data="list" style="width:100%" v-loading="loading" stripe header-cell-class-name="table-header">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="取消ID" width="70" align="center" />
        <el-table-column prop="orderId" label="订单ID" width="70" align="center" />
        <el-table-column prop="orderNo" label="订单编号" min-width="140" />
        <el-table-column label="取消原因" min-width="100">
          <template #default="{ row }">
            <span>{{ row.reason }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '待审核' : row.status === 1 ? '已同意' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.orderStatus === 5 ? 'warning' : row.orderStatus === 4 ? 'success' : 'danger'" size="small">
              {{ row.orderStatus === 4 ? '已取消' : row.orderStatus === 5 ? '取消中' : '取消失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="金额" width="100" align="center" />
        <el-table-column prop="createdTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="primary" size="small" @click="handleApprove(row)">同意</el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
            </template>
            <span v-else style="color:#909399;font-size:13px">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" @current-change="onPageChange" />
      </div>
      <p v-if="!loading && !list.length" class="empty">暂无取消申请</p>
    </div>

    <el-dialog v-model="showRejectDialog" title="拒绝取消" width="380px">
      <p style="margin:0 0 12px;font-size:14px;color:#606266">请选择拒绝原因：</p>
      <el-radio-group v-model="rejectReasonType" class="cancel-reason-group">
        <el-radio :value="1" border>不想要了</el-radio>
        <el-radio :value="2" border>地址填错</el-radio>
        <el-radio :value="3" border>质量问题</el-radio>
        <el-radio :value="4" border>其他</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { businessOrderApi } from '@/api'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = 10
const statusFilter = ref<number | null>(null)

async function fetchList() {
  loading.value = true
  try {
    const params: any = {}
    if (statusFilter.value != null) params.orderStatus = statusFilter.value
    const res: any = await businessOrderApi.cancelList(params)
    const data = res?.data || res
    list.value = (data?.records || (Array.isArray(res) ? res : [])) as any[]
    total.value = data?.total || 0
  } catch { list.value = []; total.value = 0 }
  finally { loading.value = false }
}

onMounted(fetchList)

function onPageChange(p: number) {
  page.value = p
  fetchList()
}

async function handleApprove(row: any) {
  try {
    await ElMessageBox.confirm(`确定同意取消订单 ${row.orderNo}？`, '同意取消', { type: 'info' })
    await businessOrderApi.reviewCancel(row.orderId, true)
    ElMessage.success('已同意取消')
    row.status = 1
  } catch { /* ignore */ }
}

const showRejectDialog = ref(false)
const rejectReasonType = ref<number>(1)
const rejectRow = ref<any>(null)

function handleReject(row: any) {
  rejectRow.value = row
  rejectReasonType.value = 1
  showRejectDialog.value = true
}

async function confirmReject() {
  try {
    await businessOrderApi.reviewCancel(rejectRow.value.orderId, false, rejectReasonType.value)
    ElMessage.success('已拒绝取消')
    rejectRow.value.status = 2
    showRejectDialog.value = false
  } catch { /* ignore */ }
}
</script>

<style scoped>
.order-page { padding:24px; height:100%; display:flex; flex-direction:column; box-sizing:border-box; }
.page-header { margin-bottom:20px; flex-shrink:0; display:flex; align-items:center; justify-content:space-between; }
.page-header h2 { font-size:22px; font-weight:600; color:#303133; margin:0; }
.filter-select { width:140px; }
.table-card { flex:1; background:#fff; border-radius:8px; padding:16px 0; box-shadow:0 2px 12px rgba(0,0,0,0.06); overflow-y:auto; }
.table-card :deep(.table-header) { background:#fff; color:#303133; font-weight:600; }
.empty { text-align:center; color:#909399; font-size:14px; margin-top:60px; }
.pagination-wrap { display:flex; justify-content:center; padding:16px 0; }
</style>
