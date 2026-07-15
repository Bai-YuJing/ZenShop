<template>
  <div class="review-wrapper">
    <div class="review-card">
      <!-- 待审核 -->
      <template v-if="store.registerData?.status === 1">
        <div class="clock-face">
          <svg viewBox="0 0 100 100" class="clock-svg">
            <circle cx="50" cy="50" r="46" fill="none" stroke="#409eff" stroke-width="3" />
            <line v-for="tick in ticks" :key="tick.index"
              :x1="50 + 40 * Math.sin(tick.angle)"
              :y1="50 - 40 * Math.cos(tick.angle)"
              :x2="50 + 44 * Math.sin(tick.angle)"
              :y2="50 - 44 * Math.cos(tick.angle)"
              stroke="#409eff" stroke-width="1.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 22 * Math.sin(hourAngle)"
              :y2="50 - 22 * Math.cos(hourAngle)"
              stroke="#303133" stroke-width="3.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 32 * Math.sin(minuteAngle)"
              :y2="50 - 32 * Math.cos(minuteAngle)"
              stroke="#303133" stroke-width="2" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 38 * Math.sin(secondAngle)"
              :y2="50 - 38 * Math.cos(secondAngle)"
              stroke="#e74c3c" stroke-width="1" stroke-linecap="round" />
            <circle cx="50" cy="50" r="3" fill="#303133" />
          </svg>
        </div>
        <p class="review-text">正在审核中<span class="dots"><span>.</span><span>.</span><span>.</span></span></p>
        <div class="pending-btns">
          <el-button type="primary" link size="large" class="back-btn" @click="goLogin">返回登录</el-button>
          <el-button type="primary" link size="large" class="back-btn" @click="goRegister">修改信息</el-button>
        </div>
      </template>

      <!-- 修改审核（status=6） -->
      <template v-else-if="store.registerData?.status === 6">
        <div class="clock-face">
          <svg viewBox="0 0 100 100" class="clock-svg">
            <circle cx="50" cy="50" r="46" fill="none" stroke="#409eff" stroke-width="3" />
            <line v-for="tick in ticks" :key="tick.index"
              :x1="50 + 40 * Math.sin(tick.angle)"
              :y1="50 - 40 * Math.cos(tick.angle)"
              :x2="50 + 44 * Math.sin(tick.angle)"
              :y2="50 - 44 * Math.cos(tick.angle)"
              stroke="#409eff" stroke-width="1.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 22 * Math.sin(hourAngle)"
              :y2="50 - 22 * Math.cos(hourAngle)"
              stroke="#303133" stroke-width="3.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 32 * Math.sin(minuteAngle)"
              :y2="50 - 32 * Math.cos(minuteAngle)"
              stroke="#303133" stroke-width="2" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 38 * Math.sin(secondAngle)"
              :y2="50 - 38 * Math.cos(secondAngle)"
              stroke="#e74c3c" stroke-width="1" stroke-linecap="round" />
            <circle cx="50" cy="50" r="3" fill="#303133" />
          </svg>
        </div>
        <p class="review-text">修改审核中<span class="dots"><span>.</span><span>.</span><span>.</span></span></p>
        <div class="pending-btns">
          <el-button type="primary" link size="large" class="back-btn" @click="goLogin">返回登录</el-button>
          <el-button type="primary" link size="large" class="back-btn" @click="goRegister">修改信息</el-button>
        </div>
      </template>

      <!-- 审核未通过（status=5） -->
      <template v-else-if="store.registerData?.status === 5">
        <div class="clock-face">
          <svg viewBox="0 0 100 100" class="clock-svg">
            <circle cx="50" cy="50" r="46" fill="none" stroke="#e6a23c" stroke-width="3" />
            <line v-for="tick in ticks" :key="tick.index"
              :x1="50 + 40 * Math.sin(tick.angle)"
              :y1="50 - 40 * Math.cos(tick.angle)"
              :x2="50 + 44 * Math.sin(tick.angle)"
              :y2="50 - 44 * Math.cos(tick.angle)"
              stroke="#e6a23c" stroke-width="1.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 22 * Math.sin(hourAngle)"
              :y2="50 - 22 * Math.cos(hourAngle)"
              stroke="#303133" stroke-width="3.5" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 32 * Math.sin(minuteAngle)"
              :y2="50 - 32 * Math.cos(minuteAngle)"
              stroke="#303133" stroke-width="2" stroke-linecap="round" />
            <line x1="50" y1="50"
              :x2="50 + 38 * Math.sin(secondAngle)"
              :y2="50 - 38 * Math.cos(secondAngle)"
              stroke="#e6a23c" stroke-width="1" stroke-linecap="round" />
            <circle cx="50" cy="50" r="3" fill="#303133" />
          </svg>
        </div>
        <p class="review-text review-text--rejected">审核未通过</p>

        <div class="reason-section">
          <label class="reason-label">未通过原因</label>
          <p class="reason-detail">{{ rejectReason || '暂无原因' }}</p>
        </div>

        <div class="pending-btns">
          <el-button type="primary" size="large" class="back-btn" @click="goRegister">修改信息</el-button>
          <el-button size="large" class="back-btn" @click="goLogin">返回登录</el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBusinessStore } from '@/stores/business'
import { businessShopApi } from '@/api'

const router = useRouter()
const store = useBusinessStore()

const rejectReason = ref('')

/** 驳回原因枚举映射 */
const rejectReasonMap: Record<number, string> = {
  1: '信息不完整',
  2: '证件不符',
  3: '经营范围不符',
  4: '资质过期',
  5: '其他',
}

function goLogin() {
  businessShopApi.logout().finally(() => {
    localStorage.removeItem('authentication')
    location.href = '/business/login'
  })
}
function goRegister() {
  router.push('/business/register')
}

onMounted(() => {
  // 驳回状态，从后端获取原因
  if (store.registerData?.status === 5 && store.registerData?.id) {
    businessShopApi.getReason(store.registerData.id, 2).then((res: any) => {
      const code = res
      rejectReason.value = code != null ? (rejectReasonMap[code] || '其他') : ''
    })
  }
})

const now = new Date()

const ticks = Array.from({ length: 12 }, (_, i) => ({
  index: i,
  angle: (i * 30 * Math.PI) / 180,
}))

const secondAngle = (now.getSeconds() * 6 * Math.PI) / 180
const minuteAngle = ((now.getMinutes() * 6 + now.getSeconds() * 0.1) * Math.PI) / 180
const hourAngle = (((now.getHours() % 12) * 30 + now.getMinutes() * 0.5) * Math.PI) / 180
</script>

<style scoped>
.review-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  padding: 40px 20px;
  box-sizing: border-box;
  background: #fff;
}

.review-card {
  text-align: center;
}

.clock-face {
  width: 180px;
  height: 180px;
  margin: 0 auto 32px;
}

.clock-svg {
  width: 100%;
  height: 100%;
}

.review-text {
  font-size: 28px;
  color: #606266;
  margin: 0;
  letter-spacing: 3px;
}

.review-text--rejected {
  color: #e6a23c;
}

.reason-detail {
  font-size: 15px;
  font-weight: 500;
  color: #e6a23c;
  line-height: 1.6;
  margin: 0;
  padding: 12px 16px;
  background: #fdf6ec;
  border-radius: 6px;
}

.dots span {
  display: inline-block;
  animation: dotJump 1.4s ease-in-out infinite;
}

.dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.dots span:nth-child(3) {
  animation-delay: 0.4s;
}

.reason-section {
  text-align: left;
  max-width: 400px;
  margin: 0 auto 36px;
}

.reason-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.pending-btns {
  margin-top: 48px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.pending-btns .back-btn {
  width: 200px;
}

.action-btns {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.action-btn {
  width: 240px;
}

.back-btn {
  width: 200px;
}

@keyframes dotJump {
  0%, 80%, 100% { opacity: 0; transform: translateY(0); }
  40% { opacity: 1; transform: translateY(-4px); }
}
</style>
