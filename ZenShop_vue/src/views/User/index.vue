<template>
  <div class="app-layout">
    <!-- 内容区域 -->
    <div class="content-area">
      <router-view />
    </div>

    <!-- 底部导航 -->
    <div class="bottom-nav">
      <div class="nav-item" :class="{ active: activeTab === 'home' }" @click="goTo('home')">
        <el-icon :size="22"><HomeFilled /></el-icon>
        <span class="nav-label">首页</span>
      </div>
      <div class="nav-item" :class="{ active: activeTab === 'cart' }" @click="goTo('cart')">
        <el-icon :size="22"><ShoppingCart /></el-icon>
        <span class="nav-label">购物车</span>
      </div>
      <div class="nav-item" :class="{ active: activeTab === 'profile' }" @click="goTo('profile')">
        <el-icon :size="22"><User /></el-icon>
        <span class="nav-label">个人中心</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, ShoppingCart, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeTab = computed(() => {
  const path = route.path
  if (path.includes('cart')) return 'cart'
  if (path.includes('profile')) return 'profile'
  return 'home'
})

function goTo(name: string) {
  router.push(`/user/index/${name}`)
}
</script>

<style scoped>
.app-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

/* 内容 */
.content-area {
  flex: 1;
  overflow-y: auto;
}

/* 底部导航 */
.bottom-nav {
  display: flex;
  border-top: 1px solid #e4e7ed;
  background: #fff;
  flex-shrink: 0;
  padding-bottom: env(safe-area-inset-bottom, 0);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 0 6px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-item.active {
  color: #409eff;
}

.nav-label {
  font-size: 11px;
  margin-top: 2px;
}
</style>
