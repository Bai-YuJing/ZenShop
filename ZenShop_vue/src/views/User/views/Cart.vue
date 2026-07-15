<template>
  <div class="cart-page">
    <div v-if="groups.length" class="cart-list">
      <div v-for="group in groups" :key="group.businessId" class="cart-group">
        <div class="group-header">
          <el-checkbox :model-value="groupSelected(group)" @change="toggleGroup(group)" />
          <div class="group-logo">
            <img v-if="group.logo" :src="group.logo" />
            <span v-else class="logo-placeholder">{{ group.businessName?.charAt(0) }}</span>
          </div>
          <span class="group-name">{{ group.businessName }}</span>
        </div>

        <div v-for="item in group.items" :key="item.productId" class="cart-item">
          <el-checkbox :model-value="selected.has(item.productId)" @change="toggleItem(item)" />
          <div class="item-img">
            <img v-if="item.image" :src="item.image" />
            <el-icon v-else :size="24"><Goods /></el-icon>
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ item.name }}</h4>
            <span class="item-price">¥{{ (item.price || 0).toFixed(2) }}</span>
          </div>
          <div class="item-qty">
            <el-button text size="small" @click="updateQty(group, item, -1)">−</el-button>
            <span class="qty-num">{{ item.quantity }}</span>
            <el-button text size="small" @click="updateQty(group, item, 1)">+</el-button>
          </div>
        </div>
      </div>
    </div>
    <p v-else class="empty">购物车为空</p>

    <!-- 底部结算栏 -->
    <div v-if="groups.length" class="bottom-bar">
      <el-checkbox v-model="allSelected" :indeterminate="indeterminate" @change="toggleAll">全选</el-checkbox>
      <div class="bottom-right">
        <span class="total-text">合计: <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span></span>
        <el-button type="primary" class="checkout-btn" :disabled="totalPrice <= 0" @click="checkout">结算</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Goods } from '@element-plus/icons-vue'
import { cartApi } from '@/api'

const router = useRouter()
const groups = ref<any[]>([])
const selected = ref<Set<number>>(new Set())

const allSelected = computed({
  get: () => {
    const all = getAllItems()
    return all.length > 0 && all.every(i => selected.value.has(i.productId))
  },
  set: (val: boolean) => {
    getAllItems().forEach(i => {
      if (val) selected.value.add(i.productId)
      else selected.value.delete(i.productId)
    })
    selected.value = new Set(selected.value)
  },
})

const indeterminate = computed(() => {
  const all = getAllItems()
  const count = all.filter(i => selected.value.has(i.productId)).length
  return count > 0 && count < all.length
})

const totalPrice = computed(() => {
  let total = 0
  groups.value.forEach(g => g.items.forEach((i: any) => {
    if (selected.value.has(i.productId)) total += i.price * i.quantity
  }))
  return total
})

function getAllItems() {
  return groups.value.flatMap((g: any) => g.items || [])
}

function groupSelected(group: any) {
  return group.items?.length > 0 && group.items.every((i: any) => selected.value.has(i.productId))
}

function toggleGroup(group: any) {
  const sel = groupSelected(group)
  group.items.forEach((i: any) => {
    if (sel) selected.value.delete(i.productId)
    else selected.value.add(i.productId)
  })
  selected.value = new Set(selected.value)
}

function toggleItem(item: any) {
  if (selected.value.has(item.productId)) selected.value.delete(item.productId)
  else selected.value.add(item.productId)
  selected.value = new Set(selected.value)
}

function toggleAll(val: boolean) {
  allSelected.value = val
}

async function fetchList() {
  try {
    const res: any = await cartApi.list()
    groups.value = (res || []) as any[]
  } catch {
    groups.value = []
  }
}

onMounted(fetchList)

async function updateQty(group: any, item: any, delta: number) {
  const newQty = item.quantity + delta
  if (newQty < 1) {
    try {
      await cartApi.remove(item.productId)
      await fetchList()
      selected.value.delete(item.productId)
      selected.value = new Set(selected.value)
    } catch { /* ignore */ }
    return
  }
  try {
    await cartApi.update({ productId: item.productId, quantity: newQty })
    item.quantity = newQty
  } catch { /* ignore */ }
}

function checkout() {
  const ids = [...selected.value]
  if (ids.length === 0) return
  const items = groups.value.flatMap((g: any) => g.items || [])
  const qtys = ids.map(id => items.find((i: any) => i.productId === id)?.quantity || 1)
  router.push(`/user/order/confirm?ids=${ids.join(',')}&qtys=${qtys.join(',')}`)
}
</script>

<style scoped>
.cart-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.cart-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
}

.cart-group {
  margin-bottom: 12px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px 8px 0 0;
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
}

.group-logo img { width: 100%; height: 100%; object-fit: cover; }
.logo-placeholder { font-size: 12px; font-weight: 600; color: #667eea; }
.group-name { font-size: 14px; font-weight: 600; color: #303133; }

.cart-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fff;
}

.cart-item:last-child { border-radius: 0 0 8px 8px; }

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

.item-price { font-size: 16px; font-weight: 600; color: #f56c6c; }

.item-qty {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.qty-num { font-size: 14px; color: #303133; min-width: 20px; text-align: center; }

.empty { text-align: center; color: #909399; font-size: 14px; margin-top: 80px; flex: 1; }

/* 底部结算栏 */
.bottom-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom, 0));
  background: #fff;
  border-top: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.bottom-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-text { font-size: 14px; color: #303133; }

.total-price { font-size: 18px; font-weight: 700; color: #f56c6c; }

.checkout-btn { height: 40px; border-radius: 20px; padding: 0 24px; }
</style>
