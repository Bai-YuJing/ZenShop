<script setup lang="ts">
import { ref, computed, onMounted, provide } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { adminApi } from '@/api'
import {
  User,
  Shop,
  Setting,
  Tickets,
  DataAnalysis,
} from '@element-plus/icons-vue'
import TopNav from './utilsView/TopNav.vue'

const router = useRouter()
const route = useRoute()

const userInfo = ref<any>({})
const userRole = ref<number>()
provide('userInfo', userInfo)

onMounted(async () => {
  try {
    const res: any = await adminApi.getNowUser()
    userInfo.value = res
    userRole.value = res.role
  } catch (e) {
    console.error('获取当前用户失败', e)
  }
})

/** 根据当前路由路径计算默认选中的菜单项 */
const activeIndex = computed(() => {
  const path = route.path
  if (path.includes('/admin/user')) return '/admin/user'
  if (path.includes('/admin/avatar-review')) return '/admin/avatar-review'
  if (path.includes('/admin/business')) return '/admin/business'
  if (path.includes('/admin/coupon')) return '/admin/coupon'
  if (path.includes('/admin/statistics')) return '/admin/statistics'
  if (path.includes('/admin/admin')) return '/admin/admin'
  return '/admin/user'
})

/** 菜单点击跳转 */
function handleSelect(index: string) {
  router.push(index)
}
</script>

<template>
  <el-container class="app-wrapper">
    <!-- 顶栏 -->
    <TopNav :user="userInfo" />

    <!-- 主体区域：侧边栏 + 内容 -->
    <el-container class="body-wrapper">
      <!-- 左侧导航栏 -->
      <el-aside width="220px" class="admin-aside">
        <el-menu
          :default-active="activeIndex"
          class="aside-menu"
          @select="handleSelect"
          router
        >
          <el-sub-menu index="/admin/user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/user">
              <span>用户列表</span>
            </el-menu-item>
            <el-menu-item index="/admin/avatar-review">
              <span>头像审核</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/business">
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/coupon">
            <el-icon><Tickets /></el-icon>
            <span>优惠券管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <span>统计报表</span>
          </el-menu-item>

          <el-menu-item v-if="userRole !== 1" index="/admin/admin">
            <el-icon><Setting /></el-icon>
            <span>管理员管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧主内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-wrapper {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.body-wrapper {
  height: calc(100vh - 60px);
  overflow: hidden;
}

.admin-aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.aside-menu {
  flex: 1;
  border-right: none;
  background-color: transparent;
}

.aside-menu :deep(.el-menu-item),
.aside-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.7) !important;
  font-size: 14px;
}

.aside-menu :deep(.el-menu-item:hover),
.aside-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
}

.aside-menu :deep(.el-menu-item.is-active) {
  color: #409eff !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

/* 子菜单统一黑底白字 */
.aside-menu :deep(.el-sub-menu .el-menu) {
  background-color: #304156 !important;
}

.aside-menu :deep(.el-sub-menu .el-menu .el-menu-item) {
  color: rgba(255, 255, 255, 0.7) !important;
}

.aside-menu :deep(.el-sub-menu .el-menu .el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
}

.aside-menu :deep(.el-sub-menu .el-menu .el-menu-item.is-active) {
  color: #409eff !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

/* 子菜单内的图标 */
.aside-menu :deep(.el-sub-menu .el-menu .el-menu-item .el-icon) {
  color: rgba(255, 255, 255, 0.7) !important;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
}
</style>
