<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>确认订单</h2>
    </div>

    <div v-loading="loading" class="body">
      <!-- 收货地址 -->
      <div class="section address-section" @click="showAddressPicker = true">
        <div v-if="selectedAddress" class="address-info">
          <div class="address-top">
            <span class="address-name">{{ selectedAddress.name }}</span>
            <span class="address-phone">{{ selectedAddress.phone }}</span>
            <el-tag v-if="selectedAddress.isDefault" size="small" type="primary">默认</el-tag>
          </div>
          <p class="address-detail">{{ selectedAddress.regionText }} {{ selectedAddress.detail }}</p>
        </div>
        <div v-else class="address-empty">
          <span>请选择收货地址</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 优惠券 -->
      <div class="section coupon-section" @click="showCouponPicker = true">
        <span class="section-label">优惠券</span>
        <div class="coupon-right">
          <span v-if="selectedCoupon" class="coupon-value">-¥{{ computedDiscount.toFixed(2) }}</span>
          <span v-else class="coupon-placeholder">选择优惠券</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-for="group in groups" :key="group.businessId" class="section">
        <div class="group-header">
          <div class="group-logo">
            <img v-if="group.logo" :src="group.logo" />
            <span v-else>{{ group.businessName?.charAt(0) }}</span>
          </div>
          <span class="group-name">{{ group.businessName }}</span>
        </div>
        <div v-for="item in group.items" :key="item.productId" class="order-item">
          <div class="item-img">
            <img v-if="item.image" :src="item.image" />
            <el-icon v-else :size="24"><Goods /></el-icon>
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ item.name }}</h4>
            <span class="item-price">¥{{ (item.price || 0).toFixed(2) }}</span>
            <span class="item-qty">×{{ item.quantity }}</span>
          </div>
        </div>
      </div>

      <!-- 合计 -->
      <div class="total-row">
        <span>共 {{ itemCount }} 件商品</span>
        <span class="total-price">合计: ¥{{ finalAmount.toFixed(2) }}</span>
      </div>

      <el-button type="primary" class="submit-btn" :loading="submitting" @click="submitOrder">提交订单</el-button>
    </div>

    <!-- 地址选择（展开） -->
    <div v-if="showAddressPicker" class="section addr-select">
      <div class="addr-select-header">
        <span>选择收货地址</span>
        <el-button text @click="showAddressPicker = false">收起</el-button>
      </div>
      <div v-for="addr in addressList" :key="addr.id" class="addr-item" @click="selectAddress(addr)">
        <div class="addr-info">
          <div class="addr-top">
            <span>{{ addr.name }}</span>
            <span>{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="primary">默认</el-tag>
          </div>
          <p>{{ addr.regionText }} {{ addr.detail }}</p>
        </div>
        <el-radio :model-value="selectedAddress?.id === addr.id" :value="addr.id" />
      </div>
      <p v-if="!addressList.length" style="padding:12px;text-align:center;color:#909399;margin:0">暂无地址</p>
    </div>

    <!-- 优惠券选择（展开） -->
    <div v-if="showCouponPicker" class="section coupon-select">
      <div class="addr-select-header">
        <span>选择优惠券</span>
        <el-button text @click="showCouponPicker = false">收起</el-button>
      </div>
      <div v-for="c in couponList" :key="c.id" class="coupon-item" @click="selectCoupon(c)">
        <div class="coupon-left">
          <span class="coupon-amount">{{ c.type === 2 ? (c.discountRate * 10) + '折' : '¥' + (c.discountAmount || 0) }}</span>
          <span class="coupon-condition">{{ c.type === 2 ? (c.maxDiscountAmount ? '最高减¥' + c.maxDiscountAmount : '') : (c.minAmount ? '满¥' + c.minAmount : '无门槛') }}</span>
        </div>
        <div class="coupon-mid">
          <span class="coupon-name">{{ c.name }}</span>
          <span class="coupon-expire">{{ c.validTo ? c.validTo.substring(0, 10) + '到期' : '长期有效' }}</span>
        </div>
        <el-radio :model-value="selectedCoupon?.id === c.id" :value="c.id" />
      </div>
      <p v-if="!couponList.length" style="padding:12px;text-align:center;color:#909399;margin:0">暂无可用优惠券</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Goods } from '@element-plus/icons-vue'
import { orderApi, couponApi, userAddressApi } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const groups = ref<any[]>([])
const totalAmount = ref(0)
const itemCount = computed(() => groups.value.reduce((s, g) => s + (g.items?.length || 0), 0))
const computedDiscount = computed(() => {
  const c = selectedCoupon.value
  if (!c) return 0
  if (c.type === 2) {
    const discount = totalAmount.value * (1 - (c.discountRate || 0))
    return c.maxDiscountAmount ? Math.min(discount, c.maxDiscountAmount) : discount
  }
  return c.discountAmount || 0
})

const finalAmount = computed(() => {
  const c = selectedCoupon.value
  if (!c) return totalAmount.value
  if (c.type === 2) {
    const discount = totalAmount.value * (1 - (c.discountRate || 0))
    const max = c.maxDiscountAmount
    return Math.max(0, totalAmount.value - (max ? Math.min(discount, max) : discount))
  }
  return Math.max(0, totalAmount.value - (c.discountAmount || 0))
})

// 地址
const showAddressPicker = ref(false)
const addressList = ref<any[]>([])
const selectedAddress = ref<any>(null)

// 优惠券
const showCouponPicker = ref(false)
const couponList = ref<any[]>([])
const selectedCoupon = ref<any>(null)

async function fetchAddresses() {
  try {
    const res: any = await userAddressApi.getList()
    const vos = res?.userAddressVos || []
    const defaultId = res?.defaultId
    addressList.value = vos.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      phone: item.phone || '',
      detail: item.detailedAddress || '',
      isDefault: item.id === defaultId,
      regionText: [item.provinceName, item.cityName, item.districtName].filter(Boolean).join(' '),
    }))
    const def = addressList.value.find((a: any) => a.isDefault)
    if (def) selectedAddress.value = def
  } catch { addressList.value = [] }
}

async function fetchCoupons() {
  try {
    const res: any = await couponApi.list()
    couponList.value = (res || []) as any[]
  } catch { couponList.value = [] }
}

function selectAddress(addr: any) {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

function selectCoupon(c: any) {
  selectedCoupon.value = selectedCoupon.value?.id === c.id ? null : c
  showCouponPicker.value = false
}

onMounted(async () => {
  const ids = (route.query.ids as string) || ''
  const qtys = (route.query.qtys as string) || ''
  if (!ids) return
  const productIds = ids.split(',').map(Number)
  const quantities = qtys ? qtys.split(',').map(Number) : productIds.map(() => 1)
  loading.value = true
  try {
    const [res] = await Promise.all([
      orderApi.preview({ productIds, quantities }),
      fetchAddresses(),
      fetchCoupons(),
    ])
    const data = res as any
    groups.value = (data as any[]) || []
    totalAmount.value = groups.value.reduce((s: number, g: any) => s + (g.totalAmount || 0), 0)
  } catch {
    groups.value = []
    router.replace('/user/index/cart')
  }
  finally { loading.value = false }
})

async function submitOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const allItems = groups.value.flatMap((g: any) => g.items || [])
    const productIds = allItems.map((i: any) => i.productId)
    const quantities = allItems.map((i: any) => i.quantity || 1)
    const res: any = await orderApi.submit({
      productIds,
      quantities,
      userAddressId: selectedAddress.value.id,
      couponId: selectedCoupon.value?.id || null,
    })
    const orderId = res?.id || res?.orderId
    ElMessage.success('下单成功')
    router.push(`/user/payment?id=${orderId}&amount=${finalAmount.value}`)
  } catch { /* ignore */ }
  finally { submitting.value = false }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h2 { font-size: 17px; font-weight: 600; color: #303133; margin: 0; }

.body { padding: 12px 16px; }

.section {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.group-logo {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f2ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #667eea;
  font-size: 12px;
  font-weight: 600;
}

.group-logo img { width: 100%; height: 100%; object-fit: cover; }

.group-name { font-size: 14px; font-weight: 600; color: #303133; }

.order-item {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child { border-bottom: none; }

.item-img {
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

.item-img img { width: 100%; height: 100%; object-fit: cover; }

.item-info { flex: 1; min-width: 0; }

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price { font-size: 15px; font-weight: 600; color: #f56c6c; }

.item-qty { font-size: 13px; color: #909399; margin-left: 8px; }

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #303133;
}

.total-price { font-size: 18px; font-weight: 700; color: #f56c6c; }

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 16px;
  margin-bottom: 20px;
}

/* 地址 */
.address-section { cursor: pointer; }
.address-empty { display: flex; justify-content: space-between; align-items: center; color: #909399; font-size: 14px; }
.address-info {}
.address-top { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.address-name { font-size: 15px; font-weight: 600; color: #303133; }
.address-phone { font-size: 13px; color: #909399; }
.address-detail { font-size: 13px; color: #606266; margin: 0; line-height: 1.4; }

/* 优惠券 */
.coupon-section { display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.section-label { font-size: 14px; color: #303133; }
.coupon-right { display: flex; align-items: center; gap: 6px; color: #909399; font-size: 13px; }
.coupon-value { color: #f56c6c; font-weight: 600; }
.coupon-placeholder { color: #909399; }

/* 地址弹窗 */
.addr-item { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.addr-item:last-child { border-bottom: none; }
.addr-info { flex: 1; min-width: 0; }
.addr-info .addr-top { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.addr-info p { font-size: 13px; color: #606266; margin: 0; }

/* 优惠券弹窗 */
.coupon-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.coupon-item:last-child { border-bottom: none; }
.coupon-left { text-align: center; min-width: 70px; }
.coupon-amount { display: block; font-size: 22px; font-weight: 700; color: #f56c6c; }
.coupon-condition { font-size: 11px; color: #909399; }
.coupon-mid { flex: 1; min-width: 0; }
.coupon-name { display: block; font-size: 14px; color: #303133; margin-bottom: 2px; }
.coupon-expire { font-size: 12px; color: #909399; }
</style>
