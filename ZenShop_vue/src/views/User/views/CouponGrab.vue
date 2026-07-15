<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>领券中心</h2>
    </div>

    <div class="list-wrap">
      <div v-for="item in list" :key="item.id" class="coupon-card">
        <div class="coupon-left">
          <span class="coupon-amount">{{ item.type === 2 ? (item.discountRate * 10) + '折' : '¥' + (item.discountAmount || 0) }}</span>
          <span class="coupon-condition">{{ item.type === 1 ? '满¥' + (item.minAmount || 0) : (item.maxDiscountAmount ? '最高减¥' + item.maxDiscountAmount : '') }}</span>
        </div>
        <div class="coupon-mid">
          <span class="coupon-name">{{ item.name }}</span>
          <span class="coupon-expire">{{ item.validTo ? item.validTo.substring(0, 10) + '到期' : '长期有效' }}</span>
          <span v-if="item.total != null" class="coupon-total">剩余 {{ item.total }} 张</span>
        </div>
        <el-button
          :type="item.grabbed ? 'default' : 'danger'"
          size="small"
          class="grab-btn"
          :disabled="item.grabbed || item.total === 0"
          @click="grab(item)"
        >{{ item.grabbed ? '已领取' : '抢券' }}</el-button>
      </div>
      <p v-if="!list.length" class="empty">暂无可用优惠券</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { couponApi } from '@/api'

const router = useRouter()
const list = ref<any[]>([])

onMounted(async () => {
  try {
    const res: any = await couponApi.all()
    list.value = (res || []) as any[]
  } catch { list.value = [] }
})

async function grab(item: any) {
  try {
    await couponApi.claim(item.id)
    ElMessage.success('领取成功')
    item.grabbed = true
  } catch { /* ignore */ }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f5; }
.page-header { display:flex; align-items:center; gap:4px; padding:12px 16px; background:#fff; border-bottom:1px solid #f0f0f0; }
.page-header h2 { font-size:17px; font-weight:600; color:#303133; margin:0; }
.list-wrap { padding:12px 16px; }

.coupon-card {
  display:flex; align-items:center; gap:12px;
  background:#fff; border-radius:10px; padding:14px 12px; margin-bottom:12px;
  box-shadow:0 1px 4px rgba(0,0,0,0.04);
}

.coupon-left { text-align:center; min-width:70px; border-right:1px dashed #e4e7ed; padding-right:12px; }
.coupon-amount { display:block; font-size:24px; font-weight:700; color:#f56c6c; }
.coupon-condition { font-size:11px; color:#909399; }
.coupon-mid { flex:1; min-width:0; }
.coupon-name { display:block; font-size:14px; color:#303133; margin-bottom:4px; }
.coupon-expire { font-size:12px; color:#909399; }
.coupon-total { display:block; font-size:11px; color:#f56c6c; margin-top:2px; }
.grab-btn { flex-shrink:0; }
.empty { text-align:center; color:#909399; font-size:14px; margin-top:80px; }
</style>
