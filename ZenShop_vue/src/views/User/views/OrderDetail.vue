<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>订单详情</h2>
    </div>

    <div v-loading="loading" class="body">
      <!-- 状态 -->
      <div v-if="order" class="status-bar" :style="{ background: statusBg }">
        <el-icon :size="28" :color="statusColor"><CircleCheck /></el-icon>
        <div class="status-info">
          <span class="status-text">{{ statusText(order.status) }}</span>
          <span class="status-sub">{{ statusSub }}</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section">
        <div v-for="item in (order?.items || [])" :key="item.productId" class="order-item">
          <div class="item-img">
            <img v-if="item.productImage" :src="item.productImage" />
            <el-icon v-else :size="24"><Goods /></el-icon>
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ item.productName }}</h4>
            <div class="item-bottom">
              <span class="item-price">¥{{ (item.price || 0).toFixed(2) }}</span>
              <span class="item-qty">×{{ item.quantity }}</span>
            </div>
            <div class="item-review" v-if="order?.status === 3">
              <el-button v-if="!item.rated" text type="primary" size="small" @click="goReview(item.productId)">去评价</el-button>
              <el-button v-if="item.rated" text type="primary" size="small" @click="router.push(`/user/review?id=${order.id}&view=1${item.productId ? `&productId=${item.productId}` : ''}`)">查看评价</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="section info-section">
        <div class="info-row"><span class="info-label">订单编号</span><span class="info-value">{{ order?.orderNo }}</span></div>
        <div class="info-row"><span class="info-label">下单时间</span><span class="info-value">{{ order?.createdTime }}</span></div>
        <div class="info-row"><span class="info-label">订单金额</span><span class="info-value">¥{{ (order?.totalAmount || 0).toFixed(2) }}</span></div>
        <div class="info-row"><span class="info-label">实付金额</span><span class="info-value price">¥{{ (order?.actualAmount || 0).toFixed(2) }}</span></div>
      </div>

      <div v-if="order?.status === 0" class="action-row">
        <el-button type="primary" class="pay-btn" @click="goPay">去支付</el-button>
        <el-button class="cancel-btn" @click="showCancelDialog = true">取消订单</el-button>
      </div>
      <div v-if="order?.status === 1 || order?.status === 2" class="action-row">
        <el-button class="cancel-btn" @click="showCancelDialog = true">取消订单</el-button>
        <el-button v-if="order?.status === 2" type="success" class="pay-btn" @click="handleComplete">确认收货</el-button>
      </div>
    </div>

    <el-dialog v-model="showCancelDialog" title="取消订单" width="320px">
      <p style="margin:0 0 12px;font-size:14px;color:#606266">请选择取消原因：</p>
      <el-radio-group v-model="cancelReason" class="cancel-reason-group">
        <el-radio :value="1" border>不想要了</el-radio>
        <el-radio :value="2" border>地址填错</el-radio>
        <el-radio :value="3" border>商品质量问题</el-radio>
        <el-radio :value="4" border>其他</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="showCancelDialog = false">再想想</el-button>
        <el-button type="primary" @click="confirmCancel">确定取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CircleCheck, Goods } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref<any>(null)

const statusMap: Record<number, { text: string; sub: string; color: string; bg: string }> = {
  0: { text: '待付款', sub: '请尽快完成支付', color: '#fff', bg: 'linear-gradient(135deg, #f7b731, #e6a23c)' },
  1: { text: '待发货', sub: '商家正在准备中', color: '#fff', bg: 'linear-gradient(135deg, #74b9ff, #409eff)' },
  2: { text: '待收货', sub: '商品已在路上', color: '#fff', bg: 'linear-gradient(135deg, #81ecec, #00b894)' },
  3: { text: '已完成', sub: '感谢您的购买', color: '#fff', bg: 'linear-gradient(135deg, #55efc4, #00b894)' },
  4: { text: '已取消', sub: '订单已取消', color: '#fff', bg: 'linear-gradient(135deg, #b2bec3, #636e72)' },
  5: { text: '取消审核', sub: '取消请求审核中', color: '#fff', bg: 'linear-gradient(135deg, #f7b731, #e6a23c)' },
  6: { text: '取消失败', sub: '取消失败，请联系客服', color: '#fff', bg: 'linear-gradient(135deg, #f56c6c, #e74c3c)' },
}
function statusText(s: number) { return statusMap[s]?.text || '未知' }
const statusSub = computed(() => statusMap[order.value?.status]?.sub || '')
const statusColor = computed(() => statusMap[order.value?.status]?.color || '#fff')
const statusBg = computed(() => statusMap[order.value?.status]?.bg || 'linear-gradient(135deg, #74b9ff, #409eff)')

onMounted(async () => {
  const id = Number(route.query.id) || Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res: any = await orderApi.detail(id)
    order.value = res?.data || res
  } catch { /* ignore */ }
  finally { loading.value = false }
})

const showCancelDialog = ref(false)
const cancelReason = ref(1)

function confirmCancel() {
  showCancelDialog.value = false
  orderApi.cancel(order.value.id, cancelReason.value).then(() => {
    ElMessage.success('订单已取消')
    order.value.status = 4
  }).catch(() => {})
}

function goPay() {
  router.push(`/user/payment?id=${order.value.id}&amount=${order.value.actualAmount}`)
}

function goReview(productId?: number) {
  router.push(`/user/review?id=${order.value.id}${productId ? `&productId=${productId}` : ''}`)
}

async function handleComplete() {
  try {
    await ElMessageBox.confirm('确认收到商品了吗？', '确认收货', { type: 'info' })
    await orderApi.complete(order.value.id)
    ElMessage.success('已确认收货')
    order.value.status = 3
  } catch { /* ignore */ }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f5; }
.page-header { display:flex; align-items:center; gap:4px; padding:12px 16px; background:#fff; border-bottom:1px solid #f0f0f0; }
.page-header h2 { font-size:17px; font-weight:600; color:#303133; margin:0; }
.body { padding:12px 16px; }

.status-bar { display:flex; align-items:center; gap:12px; padding:20px 16px; border-radius:10px; margin-bottom:12px; }
.status-info { display:flex; flex-direction:column; gap:2px; }
.status-text { font-size:18px; font-weight:600; }
.status-sub { font-size:13px; opacity:0.85; }

.section { background:#fff; border-radius:8px; padding:12px; margin-bottom:12px; }
.order-item { display:flex; gap:12px; padding:8px 0; border-bottom:1px solid #f5f5f5; }
.order-item:last-child { border-bottom:none; }
.item-img { width:60px; height:60px; border-radius:6px; overflow:hidden; background:#f5f5f5; display:flex; align-items:center; justify-content:center; color:#c0c4cc; flex-shrink:0; }
.item-img img { width:100%; height:100%; object-fit:cover; }
.item-info { flex:1; min-width:0; display:flex; flex-direction:column; justify-content:space-between; }
.item-name { font-size:14px; font-weight:500; color:#303133; margin:0; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.item-bottom { display:flex; justify-content:space-between; align-items:center; }
.item-price { font-size:15px; font-weight:600; color:#f56c6c; }
.item-qty { font-size:13px; color:#909399; }
.item-review { margin-top:6px; }

.info-row { display:flex; justify-content:space-between; padding:8px 0; font-size:14px; }
.info-label { color:#909399; }
.info-value { color:#303133; }
.info-value.price { color:#f56c6c; font-weight:600; }

.action-row { display:flex; gap:12px; }
.pay-btn { flex:1; height:44px; border-radius:22px; font-size:16px; }
.cancel-btn { flex:1; height:44px; border-radius:22px; font-size:16px; }
</style>
