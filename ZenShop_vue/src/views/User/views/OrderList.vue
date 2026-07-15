<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>我的订单</h2>
    </div>

    <el-tabs v-model="activeTab" class="order-tabs" :stretch="true" @tab-change="onTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane :label="`待付款(${counts[0] || 0})`" name="0" />
      <el-tab-pane :label="`待发货(${counts[1] || 0})`" name="1" />
      <el-tab-pane :label="`待收货(${counts[2] || 0})`" name="2" />
      <el-tab-pane :label="`已完成(${counts[3] || 0})`" name="3" />
      <el-tab-pane label="待评价" name="review" />
    </el-tabs>

    <div class="list-wrap" ref="scrollRef" @scroll="onScroll">
      <div v-for="item in list" :key="item.productId || item.id" class="order-card" @click="goDetail(item)">
        <div class="order-header">
          <span class="order-no">{{ item.orderNo || '待评价' }}</span>
          <el-tag v-if="item.status != null" :type="statusType(item.status)" size="small">{{ statusText(item.status) }}</el-tag>
          <el-tag v-else type="warning" size="small">待评价</el-tag>
        </div>
        <div class="order-body">
          <template v-if="item.productId">
            <div class="order-good">
              <div class="good-img">
                <img v-if="item.productImage || item.image" :src="item.productImage || item.image" />
              </div>
              <div class="good-info">
                <p class="good-name">{{ item.productName || item.name }}</p>
                <span class="good-price">¥{{ (item.price || 0).toFixed(2) }} × {{ item.quantity }}</span>
              </div>
            </div>
          </template>
          <template v-else>
            <div v-for="good in (item.items || [])" :key="good.productId" class="order-good">
              <div class="good-img">
                <img v-if="good.productImage || good.image" :src="good.productImage || good.image" />
              </div>
              <div class="good-info">
                <p class="good-name">{{ good.productName || good.name }}</p>
                <span class="good-price">¥{{ (good.price || 0).toFixed(2) }} × {{ good.quantity }}</span>
              </div>
            </div>
          </template>
        </div>
        <div class="order-footer">
          <span>实付: <span class="amount">¥{{ (item.actualAmount || 0).toFixed(2) }}</span></span>
          <template v-if="activeTab === 'review'">
            <el-button v-if="!item.rated" text type="primary" size="small" @click.stop="goReview(item)">去评价</el-button>
          </template>
        </div>
      </div>
      <p v-if="!loading && !list.length" class="empty">暂无订单</p>
      <div class="load-status">{{ loading ? '加载中...' : noMore ? '没有更多了' : '' }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api'

const router = useRouter()
const activeTab = ref('all')
const list = ref<any[]>([])
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)
const scrollRef = ref<HTMLDivElement>()
const counts = ref<Record<number, number>>({})
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

async function fetchCounts() {
  try {
    const res: any = await orderApi.statusCounts()
    const d = res?.data || res || {}
    counts.value = {
      0: d.pendingPayment || 0,
      1: d.pendingDelivery || 0,
      2: d.receiving || 0,
      3: d.completed || 0,
    }
  } catch {}
}

async function fetchList(reset = false) {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const params: any = { page: reset ? 1 : page.value, size: 10 }
    if (activeTab.value === 'review') {
      const res: any = await orderApi.unreviewed({ page: reset ? 1 : page.value, size: 10 })
      const raw = res?.records || res?.data?.records || (Array.isArray(res) ? res : [])
      const records = raw.map((r: any) => ({ ...r, status: 3, rated: false }))
      if (reset) { list.value = records; page.value = 2 }
      else { list.value.push(...records); page.value++ }
      if (records.length < 10) noMore.value = true
      loading.value = false
      return
    }
    if (activeTab.value !== 'all') params.status = Number(activeTab.value)
    const res: any = await orderApi.list(params)
    const records = res?.records || res?.data?.records || (Array.isArray(res) ? res : [])
    if (reset) { list.value = records; page.value = 2 }
    else { list.value.push(...records); page.value++ }
    if (records.length < 10) noMore.value = true
  } catch { list.value = [] }
  finally { loading.value = false }
}

function onTabChange() { page.value = 0; noMore.value = false; list.value = []; fetchList(true) }

function onScroll() {
  const el = scrollRef.value
  if (!el || loading.value || noMore.value) return
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) fetchList()
}

function goReview(item: any) {
  router.push(`/user/review?id=${item.orderId || item.id}`)
}

function goDetail(item: any) {
  const id = item._orderId || item.orderId || item.id
  if (!id) return
  if (item.status === 0) {
    router.push(`/user/payment?id=${item.id}&amount=${item.actualAmount}`)
  } else {
    router.push(`/user/order/detail?id=${item.id}`)
  }
}

onMounted(() => { fetchCounts(); fetchList(true) })
</script>

<style scoped>
.page { height: 100vh; display:flex; flex-direction:column; background: #f5f5f5; }
.page-header { display:flex; align-items:center; gap:4px; padding:12px 16px; background:#fff; border-bottom:1px solid #f0f0f0; }
.page-header { flex-shrink:0; }
.page-header h2 { font-size:17px; font-weight:600; color:#303133; margin:0; }
.order-tabs { background:#fff; flex-shrink:0; }
.order-tabs :deep(.el-tabs__header) { margin:0; }
.list-wrap { flex:1; padding:12px 16px; overflow-y:auto; }
.order-card { background:#fff; border-radius:8px; margin-bottom:10px; box-shadow:0 1px 4px rgba(0,0,0,0.04); cursor:pointer; }
.order-header { display:flex; justify-content:space-between; align-items:center; padding:12px; border-bottom:1px solid #f5f5f5; }
.order-no { font-size:13px; color:#606266; }
.order-body { padding:8px 12px; }
.order-good { display:flex; gap:10px; padding:6px 0; }
.good-img { width:48px; height:48px; border-radius:4px; flex-shrink:0; background:#f5f5f5; overflow:hidden; display:flex; align-items:center; justify-content:center; }
.good-img img { width:100%; height:100%; object-fit:cover; }
.good-info { flex:1; min-width:0; display:flex; flex-direction:column; justify-content:center; gap:4px; }
.good-name { font-size:13px; color:#303133; margin:0; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.good-price { font-size:13px; color:#909399; }
.order-footer { display:flex; justify-content:space-between; align-items:center; padding:10px 12px; border-top:1px solid #f5f5f5; font-size:13px; color:#303133; }
.footer-right { }
.amount { font-size:16px; font-weight:600; color:#f56c6c; }
.empty { text-align:center; color:#909399; font-size:14px; padding:60px 0; }
.load-status { text-align:center; padding:16px; font-size:13px; color:#909399; }
</style>
