<template>
  <div class="top-bar">
    <div class="top-bar-left">
      <span class="logo-text">商家管理平台</span>
    </div>
    <div class="top-bar-right">
      <span class="shop-name">{{ shopName }}</span>
      <el-button text size="small" @click="goLogin">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { businessShopApi } from '@/api'

const router = useRouter()
const shopName = ref('')

onMounted(async () => {
  try {
    const shop: any = await businessShopApi.getShop()
    shopName.value = shop?.name || ''
  } catch {
    router.replace('/business/login')
  }
})

function goLogin() {
  businessShopApi.logout().finally(() => {
    localStorage.removeItem('authentication')
    location.href = '/business/login'
  })
}
</script>

<style scoped>
.top-bar {
  height: 56px;
  background: #1a1a2e;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;
  flex-shrink: 0;
}

.top-bar-left {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.shop-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-right: 16px;
}

.top-bar-right :deep(.el-button) {
  color: rgba(255, 255, 255, 0.8);
}
.top-bar-right :deep(.el-button:hover) {
  color: #e6a23c;
}
</style>
