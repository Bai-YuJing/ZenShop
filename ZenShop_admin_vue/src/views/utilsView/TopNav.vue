<script setup lang="ts">
import { useRouter } from 'vue-router'
import { InfoFilled, SwitchButton } from '@element-plus/icons-vue'
import { adminApi } from '@/api';

const { user } = defineProps<{ user: any }>()

const router = useRouter()

function handleLogout() {
  // TODO: 清除登录状态 / token
  adminApi.logout()
  router.push('/')
}

function goToProfile() {
  router.push({ name: 'adminInfo' })
}
</script>

<template>
  <el-header class="top-nav" height="60px">
    <div class="nav-left">
      <div class="logo">ZS</div>
      <span class="title">ZenShop 管理</span>
    </div>

    <div class="nav-right">
      <el-dropdown trigger="click">
        <span class="avatar-trigger">
          <img class="avatar-img" :src="user.avatar || 'data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 100 100\'%3e%3crect width=\'100\' height=\'100\' fill=\'%23bdbdbd\' rx=\'50\'/%3e%3ccircle cx=\'50\' cy=\'36\' r=\'18\' fill=\'%239e9e9e\'/%3e%3cellipse cx=\'50\' cy=\'74\' rx=\'32\' ry=\'24\' fill=\'%239e9e9e\'/%3e%3c/svg%3e'" alt="avatar" />
          <span class="nickname">{{ user.nickname }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled style="font-weight: 600; color: #333;">
              {{ user.nickname }}
            </el-dropdown-item>
            <el-dropdown-item divided @click="goToProfile">
              <el-icon><InfoFilled /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<style scoped>
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #304156;
  color: #fff;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo {
  width: 34px;
  height: 34px;
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

.title {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.nav-right {
  display: flex;
  align-items: center;
}

.avatar-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.avatar-trigger:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.avatar-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: block;
}

.nickname {
  margin-left: 8px;
  font-size: 14px;
  color: #fff;
}
</style>
