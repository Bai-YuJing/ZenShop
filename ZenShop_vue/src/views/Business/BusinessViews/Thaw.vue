<template>
  <div class="thaw-wrapper">
    <div class="thaw-card">
      <!-- 冻结图标 & 标题 -->
      <div class="status-header">
        <div class="icon-circle">
          <svg viewBox="0 0 48 48" width="56" height="56">
            <path d="M24 4C12.96 4 4 12.96 4 24s8.96 20 20 20 20-8.96 20-20S35.04 4 24 4z" fill="#e6a23c" opacity="0.15" />
            <path d="M24 8C15.16 8 8 15.16 8 24s7.16 16 16 16 16-7.16 16-16S32.84 8 24 8z" fill="none" stroke="#e6a23c" stroke-width="2.5" />
            <path d="M24 14v10l6 6" fill="none" stroke="#e6a23c" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
            <circle cx="24" cy="24" r="2" fill="#e6a23c" />
          </svg>
        </div>
        <h1 class="title">账号已冻结</h1>
        <p class="subtitle">您的商家账号已被冻结，部分功能暂时不可用</p>
      </div>

      <!-- 冻结原因 -->
      <div class="info-section">
        <label class="info-label">冻结原因</label>
        <p class="info-content">{{ freezeReason || '暂无原因' }}</p>
      </div>

      <!-- 联系客服 -->
      <div class="info-section">
        <label class="info-label">联系客服</label>
        <p class="info-content contact-info">
          如需协助解冻，请联系平台客服<br />
          客服热线：400-xxx-xxxx<br />
          服务时间：工作日 9:00 - 18:00
        </p>
      </div>

      <!-- 操作按钮 -->
      <div class="pending-btns">
        <el-button type="warning" size="large" class="back-btn" :loading="thawing" @click="handleThaw">
          申请解冻
        </el-button>
        <el-button size="large" class="back-btn" @click="goLogin">返回登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { businessShopApi } from '@/api'

const router = useRouter()
const freezeReason = ref('')
const shopId = ref('')

/** 冻结原因枚举映射（type=2 冻结） */
const freezeReasonMap: Record<number, string> = {
  1: '违规经营',
  2: '投诉过多',
  3: '资质异常',
  4: '店铺长期未运营',
  5: '其他',
}

const thawing = ref(false)

onMounted(async () => {
  try {
    const shop: any = await businessShopApi.getShop()
    if (!shop) throw new Error('no shop')
    shopId.value = shop.id
    // 获取冻结原因
    if (shop.id) {
      const res: any = await businessShopApi.getReason(shop.id, 2)
      const code = res
      freezeReason.value = code != null ? (freezeReasonMap[code] || '其他') : ''
    }
  } catch {
    router.replace('/business/login')
  }
})

function handleThaw() {
  ElMessageBox.confirm('确认提交解冻申请？我们将尽快处理您的请求。', '申请解冻', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    thawing.value = true
    try {
      await businessShopApi.updateStatus(shopId.value, 1)
      ElMessage.success('解冻申请已提交，请等待审核')
      router.replace('/business/center')
    } catch {
      // 错误已在拦截器中处理
    } finally {
      thawing.value = false
    }
  }).catch(() => {
    // 取消
  })
}

function goLogin() {
  businessShopApi.logout().finally(() => {
    localStorage.removeItem('authentication')
    location.href = '/business/login'
  })
}
</script>

<style scoped>
.thaw-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  padding: 40px 20px;
  box-sizing: border-box;
  background: linear-gradient(135deg, #fdf6ec 0%, #fef0f0 50%, #fdf6ec 100%);
}

.thaw-card {
  width: 100%;
  max-width: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 48px 40px;
  box-sizing: border-box;
  text-align: center;
}

/* 状态头部 */
.status-header {
  margin-bottom: 32px;
}

.icon-circle {
  margin-bottom: 16px;
}

.title {
  font-size: 26px;
  font-weight: 600;
  color: #e6a23c;
  margin: 0 0 8px;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

/* 信息区域 */
.info-section {
  text-align: left;
  margin-bottom: 20px;
}

.info-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.info-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin: 0;
  padding: 12px 16px;
  background: #fdf6ec;
  border-radius: 6px;
  border-left: 3px solid #e6a23c;
}

.contact-info {
  background: #f5f7fa;
  border-left-color: #909399;
  line-height: 2;
}

/* 按钮 */
.pending-btns {
  margin-top: 36px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.pending-btns .back-btn {
  width: 200px;
}
</style>
