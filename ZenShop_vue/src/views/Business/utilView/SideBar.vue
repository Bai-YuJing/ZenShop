<template>
  <div class="side-bar">
    <el-menu
      :default-active="activeMenu"
      :default-openeds="['order']"
      class="side-menu"
      background-color="#1a1a2e"
      text-color="rgba(255,255,255,0.7)"
      active-text-color="#fff"
      @select="handleSelect"
    >
      <el-menu-item index="profile">
        <el-icon><User /></el-icon>
        <span>个人信息</span>
      </el-menu-item>
      <el-menu-item index="employee">
        <el-icon><UserFilled /></el-icon>
        <span>员工管理</span>
      </el-menu-item>
      <el-menu-item index="category">
        <el-icon><Grid /></el-icon>
        <span>分类管理</span>
      </el-menu-item>
      <el-menu-item index="product">
        <el-icon><Goods /></el-icon>
        <span>商品管理</span>
      </el-menu-item>
      <el-menu-item index="statistics">
        <el-icon><DataAnalysis /></el-icon>
        <span>统计报表</span>
      </el-menu-item>
      <el-sub-menu index="order">
        <template #title>
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </template>
        <el-menu-item index="order/done">已完成订单</el-menu-item>
        <el-menu-item index="order/pending">未完成订单</el-menu-item>
        <el-menu-item index="order/cancel">取消管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, UserFilled, Grid, Goods, List, DataAnalysis } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('profile')) return 'profile'
  if (path.includes('employee')) return 'employee'
  if (path.includes('category')) return 'category'
  if (path.includes('product')) return 'product'
  if (path.includes('statistics')) return 'statistics'
  if (path.includes('cancel')) return 'cancel'
  if (path.includes('review')) return 'review'
  if (path.includes('order')) return 'order'
  return 'profile'
})

function handleSelect(index: string) {
  if (index.startsWith('order/')) {
    router.push(`/business/center/${index}`)
  } else {
    router.push(`/business/center/${index}`)
  }
}
</script>

<style scoped>
.side-bar {
  width: 200px;
  height: 100%;
  background: #1a1a2e;
  box-sizing: border-box;
  flex-shrink: 0;
  overflow-y: auto;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.side-menu .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.side-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.7);
  height: 44px;
  line-height: 44px;
}

.side-menu :deep(.el-sub-menu__title:hover) {
  background-color: transparent;
}

.side-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.6);
  height: 44px;
  line-height: 44px;
}

.side-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.side-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 64px !important;
}
</style>
