<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>支付</h2>
    </div>
    <div class="body">
      <div class="amount-card">
        <p class="amount-label">实付金额</p>
        <p class="amount-value">¥{{ amount }}</p>
      </div>
      <p class="order-id">订单编号: {{ orderId }}</p>

      <!-- 支付方式 -->
      <div class="payment-section">
        <h4 class="payment-title">选择支付方式</h4>
        <div
          :class="['payment-item', { active: paymentMethod === 1 }]"
          @click="paymentMethod = 1"
        >
          <span class="payment-icon">微</span>
          <span>微信支付</span>
          <el-icon v-if="paymentMethod === 1" class="check-icon"><CircleCheck /></el-icon>
        </div>
        <div
          :class="['payment-item', { active: paymentMethod === 2 }]"
          @click="paymentMethod = 2"
        >
          <span class="payment-icon alipay">支</span>
          <span>支付宝</span>
          <el-icon v-if="paymentMethod === 2" class="check-icon"><CircleCheck /></el-icon>
        </div>
      </div>

      <el-button type="primary" class="pay-btn" :loading="loading" @click="handlePay">确认支付</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, CircleCheck } from '@element-plus/icons-vue'
import { payApi } from '@/api'

const route = useRoute()
const router = useRouter()
const orderId = ref('')
const amount = ref('0.00')
const paymentMethod = ref(1)
const loading = ref(false)

onMounted(() => {
  orderId.value = route.query.id as string || ''
  amount.value = route.query.amount as string || '0.00'
})

async function handlePay() {
  loading.value = true
  try {
    const res: any = await payApi.pay({ orderId: orderId.value, paymentMethod: paymentMethod.value })
    const items = route.query.items || '[]'
    const time = new Date().toLocaleString('zh-CN')
    router.push(`/user/order/detail?id=${orderId.value}&amount=${amount.value}&time=${encodeURIComponent(time)}&items=${encodeURIComponent(items as string)}`)
  } catch { /* ignore */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f5; }
.page-header { display: flex; align-items: center; gap: 4px; padding: 12px 16px; background: #fff; border-bottom: 1px solid #f0f0f0; }
.page-header h2 { font-size: 17px; font-weight: 600; color: #303133; margin: 0; }
.body { padding: 24px 16px; text-align: center; }
.amount-card { background: #fff; border-radius: 12px; padding: 32px; margin-bottom: 12px; }
.amount-label { font-size: 14px; color: #909399; margin: 0 0 8px; }
.amount-value { font-size: 36px; font-weight: 700; color: #f56c6c; margin: 0; }
.order-id { font-size: 13px; color: #909399; margin: 0 0 20px; }

.payment-section { text-align: left; background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 24px; }
.payment-title { font-size: 14px; font-weight: 600; color: #303133; margin: 0 0 12px; }
.payment-item { display: flex; align-items: center; gap: 10px; padding: 14px 12px; border-radius: 8px; border: 1px solid #e4e7ed; margin-bottom: 8px; cursor: pointer; transition: all 0.2s; }
.payment-item.active { border-color: #409eff; background: #f0f7ff; }
.payment-icon { width: 28px; height: 28px; border-radius: 50%; background: #07c160; color: #fff; font-size: 14px; font-weight: 600; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.payment-icon.alipay { background: #1677ff; }
.check-icon { margin-left: auto; color: #409eff; font-size: 18px; }
.pay-btn { width: 100%; height: 44px; border-radius: 22px; font-size: 16px; }
</style>
