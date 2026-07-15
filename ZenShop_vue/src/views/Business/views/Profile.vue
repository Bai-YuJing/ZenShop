<template>
  <div class="profile-page">
    <!-- 头部 -->
    <div class="page-header">
      <h2>个人信息</h2>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="6" animated />
    </div>

    <template v-else-if="shop">
      <!-- 基本信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <div class="avatar-section">
            <div class="avatar">
              <img v-if="shop.logo" :src="shop.logo" alt="logo" />
              <span v-else class="avatar-placeholder">{{ shop.name?.charAt(0) || '商' }}</span>
            </div>
            <div class="avatar-info">
              <h3 class="shop-name">{{ shop.name }}</h3>
              <span class="shop-status" :class="statusClass">{{ statusText }}</span>
            </div>
          </div>
          <el-button type="primary" plain @click="handleEdit">编辑资料</el-button>
        </div>

        <el-divider />

        <el-form label-width="90px" class="info-form">
          <div class="form-grid">
            <el-form-item label="店铺ID">
              <span>{{ shop.id }}</span>
            </el-form-item>
            <el-form-item label="商家名称">
              <span>{{ shop.name }}</span>
            </el-form-item>
            <el-form-item label="手机号">
              <span>{{ shop.phone }}</span>
            </el-form-item>
            <el-form-item label="邮箱">
              <span>{{ shop.email || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="店铺评分">
              <el-rate :model-value="shop.ratting ?? 0" disabled :max="5" />
              <span class="score-text">{{ shop.ratting ?? 0 }}分</span>
            </el-form-item>
            <el-form-item label="经营分类">
              <span>{{ shop.categoryName || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="所在地区">
              <span>{{ regionText }}</span>
            </el-form-item>
            <el-form-item label="详细地址">
              <span>{{ shop.detailedAddress || '未设置' }}</span>
            </el-form-item>
            <el-form-item label="店铺描述" class="full-width">
              <span>{{ shop.description || '暂无描述' }}</span>
            </el-form-item>
            <el-form-item label="注册时间">
              <span>{{ formatTime(shop.createdTime) }}</span>
            </el-form-item>
            <el-form-item label="上次登录">
              <span>{{ shop.lastLoginTime ? formatTime(shop.lastLoginTime) : '首次登录' }}</span>
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div class="shop-action">
        <el-button v-if="shop.status === 2" type="danger" @click="handleClose">关店</el-button>
        <el-button v-if="shop.status === 4" type="success" @click="handleOpen">开店</el-button>
      </div>
    </template>

    <el-empty v-else description="暂无商家信息" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { businessShopApi } from '@/api'
import type { ShopStatus } from '@/api'
import { regionData } from '@/data/region-data'

const router = useRouter()
const shop = ref<ShopStatus | null>(null)
const loading = ref(true)

/** 根据 provinceId/cityId/districtId 解析地区名称 */
const regionText = computed(() => {
  const s = shop.value
  if (!s?.provinceId) return '未设置'
  const pId = String(s.provinceId)
  const cId = String(s.cityId)
  const dId = String(s.districtId)
  // 在 regionData 树中查找
  const pItem = regionData.find(p => p.value === pId)
  if (!pItem) return `${pId}/${cId}/${dId}`
  const cItem = pItem.children?.find(c => c.value === cId)
  if (!cItem) return `${pItem.label}/${cId}/${dId}`
  const dItem = cItem.children?.find(d => d.value === dId)
  return dItem ? `${pItem.label} / ${cItem.label} / ${dItem.label}` : `${pItem.label} / ${cItem.label} / ${dId}`
})

const statusClass = computed(() => {
  switch (shop.value?.status) {
    case 0: return 'status-pending'
    case 1: return 'status-review'
    case 2: return 'status-normal'
    case 3: return 'status-frozen'
    case 4: return 'status-closed'
    case 5: return 'status-rejected'
    case 6: return 'status-review'
    default: return ''
  }
})

const statusText = computed(() => {
  const map: Record<number, string> = {
    0: '待提交',
    1: '待审核',
    2: '正常',
    3: '冻结',
    4: '已关闭',
    5: '审核未通过',
    6: '修改审核',
  }
  return map[shop.value?.status ?? -1] || '未知'
})

function formatTime(time: string) {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

async function handleClose() {
  try {
    await ElMessageBox.confirm('确定要关闭店铺吗？关闭后用户将无法查看您的商品。', '关店', { type: 'warning', confirmButtonText: '确定关店' })
    await businessShopApi.close()
    if (shop.value) shop.value.status = 4
    ElMessage.success('店铺已关闭')
  } catch { /* ignore */ }
}

async function handleOpen() {
  try {
    await ElMessageBox.confirm('确定要重新开店吗？', '开店', { type: 'info', confirmButtonText: '确定开店' })
    await businessShopApi.open()
    if (shop.value) shop.value.status = 2
    ElMessage.success('店铺已开启')
  } catch { /* ignore */ }
}

function handleEdit() {
  router.push('/business/center/profile/edit')
}

onMounted(async () => {
  try {
    const shopRes = await businessShopApi.getShop()
    shop.value = shopRes as any as ShopStatus
  } catch {
    ElMessage.error('获取商家信息失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-page {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.loading-wrap {
  padding: 40px;
  background: #fff;
  border-radius: 8px;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 两列网格 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 24px;
}

.form-grid .full-width {
  grid-column: 1 / -1;
}

.info-form {
  margin-top: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}

.avatar-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.shop-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.shop-status {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 10px;
  display: inline-block;
  width: fit-content;
}

.status-pending { background: #fdf6ec; color: #e6a23c; }
.status-review { background: #ecf5ff; color: #409eff; }
.status-normal { background: #f0f9eb; color: #67c23a; }
.status-frozen { background: #fef0f0; color: #f56c6c; }
.status-closed { background: #f4f4f5; color: #909399; }
.status-rejected { background: #fef0f0; color: #f56c6c; }

/* 信息表单 */
.info-form {
  margin-top: 8px;
}

.info-form :deep(.el-form-item) {
  margin-bottom: 0;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.form-grid :deep(.el-form-item:nth-last-child(-n+2)) {
  border-bottom: none;
}

.info-form :deep(.el-form-item__label) {
  color: #909399;
  font-weight: 400;
}

.info-form :deep(.el-form-item__content) {
  color: #303133;
}

.score-text {
  margin-left: 8px;
  font-size: 14px;
  color: #e6a23c;
  vertical-align: middle;
}

.shop-action {
  text-align: center;
  margin-top: 24px;
}

.shop-action .el-button {
  width: 200px;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
}
</style>
