<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { adminApi } from '@/api'
const router = useRouter()
const route = useRoute()

const userRole = ref<number>()

onMounted(async () => {
  try {
    const res: any = await adminApi.getNowUser()
    userRole.value = res.role
  } catch (e) {
    console.error('获取当前用户失败', e)
  }
})

/** 根据当前路由路径计算默认选中的菜单项 */
const activeIndex = computed(() => {
  const path = route.path
  if (path.includes('/admin/user')) return '/admin/user'
  if (path.includes('/admin/business')) return '/admin/business'
  if (path.includes('/admin/admin')) return '/admin/admin'
  return '/admin/user'
})

/** 菜单点击跳转 */
function handleSelect(index: string) {
  router.push(index)
}
</script>

<template>
  <el-container class="admin-container">
    <!-- 左侧导航栏 -->
    <el-aside width="220px" class="admin-aside">

      <el-menu
        :default-active="activeIndex"
        class="aside-menu"
        @select="handleSelect"
        router
      >
        <el-menu-item index="/admin/user">
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/business">
          <span>商家管理</span>
        </el-menu-item>

        <el-menu-item v-if="userRole !== 1" index="/admin/admin">
          <span>管理员管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主内容区 -->
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.admin-container {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.aside-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.aside-header .title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.aside-menu {
  flex: 1;
  border-right: none;
  background-color: transparent;
}

.aside-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.aside-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.aside-menu .el-menu-item.is-active {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.admin-main {
  background-color: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
}
</style>