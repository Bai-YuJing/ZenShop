<template>
  <div class="profile-page">
    <!-- 头像 & 昵称 -->
    <div class="avatar-section">
      <div class="avatar">
        <img v-if="user.avatar" :src="user.avatar" />
        <el-icon v-else :size="36"><User /></el-icon>
      </div>
      <div class="user-info">
        <h3 class="user-name">{{ user.name || '未设置昵称' }}</h3>
        <span class="user-phone">{{ user.phone || '' }}</span>
      </div>
    </div>

    <!-- 订单快捷入口 -->
    <div class="order-grid">
      <div class="order-item" @click="handleOrder()">
        <el-badge :value="counts.pendingPayment || 0" :max="99" class="order-badge">
          <div class="order-icon">
            <el-icon :size="26"><Clock /></el-icon>
          </div>
        </el-badge>
        <span class="order-label">待付款</span>
      </div>
      <div class="order-item" @click="handleOrder()">
        <el-badge :value="(counts.pendingDelivery || 0) + (counts.receiving || 0)" :max="99" class="order-badge">
          <div class="order-icon">
            <el-icon :size="26"><Van /></el-icon>
          </div>
        </el-badge>
        <span class="order-label">待收货</span>
      </div>
      <div class="order-item" @click="handleOrder()">
        <div class="order-icon">
          <el-icon :size="26"><CircleCheck /></el-icon>
        </div>
        <span class="order-label">已完成</span>
      </div>
      <div class="order-item" @click="handleOrder()">
        <div class="order-icon">
          <el-icon :size="26"><Grid /></el-icon>
        </div>
        <span class="order-label">更多</span>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="menu-list">
      <div class="menu-item" @click="handleMenu('info')">
        <span class="menu-icon"><el-icon :size="20"><User /></el-icon></span>
        <span class="menu-label">个人信息</span>
        <el-icon class="menu-arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="handleMenu('address')">
        <span class="menu-icon"><el-icon :size="20"><Location /></el-icon></span>
        <span class="menu-label">地址管理</span>
        <el-icon class="menu-arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="handleMenu('coupon')">
        <span class="menu-icon"><el-icon :size="20"><Ticket /></el-icon></span>
        <span class="menu-label">领券中心</span>
        <el-icon class="menu-arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="handleMenu('password')">
        <span class="menu-icon"><el-icon :size="20"><Lock /></el-icon></span>
        <span class="menu-label">修改密码</span>
        <el-icon class="menu-arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Location, Lock, ArrowRight, Clock, Van, CircleCheck, Grid, Ticket } from '@element-plus/icons-vue'
import { userApi, orderApi } from '@/api'

const router = useRouter()
const counts = ref({ pendingPayment: 0, pendingDelivery: 0, receiving: 0, completed: 0 })

const user = ref({
  name: '',
  phone: '',
  avatar: '',
})

onMounted(async () => {
  try {
    const [userRes, countRes] = await Promise.all([
      userApi.getUser(),
      orderApi.statusCounts(),
    ])
    if (userRes) {
      const u = userRes as any
      user.value = {
        name: u.username || '',
        phone: u.phone || '',
        avatar: u.avatar || '',
      }
    }
    const d = (countRes as any)?.data || countRes || {}
    counts.value = { pendingPayment: d.pendingPayment || 0, pendingDelivery: d.pendingDelivery || 0, receiving: d.receiving || 0, completed: d.completed || 0 }
  } catch {
    // 错误已在拦截器中处理
  }
})

function handleOrder() {
  router.push('/user/orders')
}

function handleMenu(type: string) {
  if (type === 'info') router.push('/user/info')
  if (type === 'address') router.push('/user/address')
  if (type === 'coupon') router.push('/user/coupon')
  if (type === 'password') router.push('/user/password')
}
</script>

<style scoped>
.profile-page {
  padding: 0;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 56px 20px 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #fff;
}

.user-phone {
  font-size: 13px;
  opacity: 0.85;
}

/* 订单快捷入口 */
.order-grid {
  display: flex;
  margin: 12px 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 16px 0;
}

.order-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: color 0.2s;
}

.order-item:active {
  opacity: 0.6;
}

.order-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #f0f2ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
}

.order-label {
  font-size: 12px;
  color: #606266;
}

.order-badge :deep(.el-badge__content) {
  font-size: 10px;
  padding: 0 4px;
  height: 16px;
  line-height: 16px;
  border: none;
}

/* 功能列表 */
.menu-list {
  margin: 12px 16px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: #f5f7fa;
}

.menu-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2ff;
  color: #667eea;
  margin-right: 12px;
  flex-shrink: 0;
}

.menu-label {
  flex: 1;
  font-size: 15px;
  color: #303133;
}

.menu-arrow {
  color: #c0c4cc;
}
</style>
