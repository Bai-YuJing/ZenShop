<template>
  <div class="business-layout">
    <TopBar />
    <div class="layout-body">
      <SideBar />
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useBusinessStore } from '@/stores/business'
import { businessShopApi } from '@/api'
import { ElMessageBox } from 'element-plus'
import TopBar from './utilView/TopBar.vue'
import SideBar from './utilView/SideBar.vue'

const router = useRouter()
const store = useBusinessStore()
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await businessShopApi.getShop()
    // 待提交
    if (res.status == 0) {
      store.setRegisterData(res)
      router.replace('/business/register')
    }
    // 待审核
    if (res.status == 1) {
      store.setRegisterData(res)
      router.replace('/business/review')
    }
    // 冻结
    if (res.status == 3) {
      store.setRegisterData(res)
      ElMessageBox.confirm('您的账号已被冻结，是否前往解冻？', '账号冻结', {
        confirmButtonText: '去解冻',
        cancelButtonText: '退出登录',
        type: 'warning',
      }).then(() => {
        router.replace('/business/thaw')
      }).catch(() => {
        businessShopApi.logout().finally(() => {
          localStorage.removeItem('authentication')
          router.replace('/business/login')
        })
      })
    }
    // 审核未通过
    if (res.status == 5) {
      store.setRegisterData(res)
      router.replace('/business/review')
    }

    // status 2=正常 留在本页显示布局
  } catch (error) {
    router.replace('/business/login')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.business-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background: #f5f7fa;
}

.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>
