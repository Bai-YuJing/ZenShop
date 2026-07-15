<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>评价商品</h2>
    </div>

    <div class="body">
      <div v-for="item in items" :key="item.productId" class="review-card">
        <div class="review-header">
          <div class="review-img">
            <img v-if="item.productImage" :src="item.productImage" />
          </div>
          <span class="review-name">{{ item.productName }}</span>
        </div>
        <el-rate v-model="item._rating" :max="5" class="review-rate" :disabled="item._readonly" />
        <el-input
          v-model="item._comment"
          type="textarea"
          :rows="3"
          :placeholder="item._readonly ? '' : '说说你的使用感受...'"
          :disabled="item._readonly"
          maxlength="200"
          show-word-limit
          class="review-input"
        />
      </div>

      <el-button v-if="!isView" type="primary" class="submit-btn" :loading="submitting" @click="submitReview">提交评价</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api'

const route = useRoute()
const router = useRouter()
const submitting = ref(false)
const items = reactive<any[]>([])

const isView = ref(false)

onMounted(async () => {
  const id = route.query.id
  const viewMode = route.query.view === '1'
  const productId = Number(route.query.productId)
  isView.value = viewMode
  if (!id) return
  try {
    if (viewMode) {
      const res: any = await orderApi.getRate(Number(id))
      let list = (res || []) as any[]
      if (productId) list = list.filter((r: any) => r.productId === productId)
      list.forEach((r: any) => {
        items.push({ productImage: r.productImage, productName: r.productName, _rating: r.score, _comment: r.content, _readonly: true })
      })
    } else {
      const res: any = await orderApi.detail(Number(id))
      const order = res?.data || res
      const allItems = order?.items || []
      const target = productId ? allItems.filter((i: any) => i.productId === productId) : allItems
      target.forEach((i: any) => {
        items.push({ ...i, _rating: 5, _comment: '' })
      })
    }
  } catch { /* ignore */ }
})

async function submitReview() {
  const first = items[0]
  if (!first?._comment.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }
  submitting.value = true
  try {
    const productId = Number(route.query.productId)
    const params: any = {
      orderId: Number(route.query.id),
      score: first._rating,
      content: first._comment,
    }
    if (productId) params.productId = productId
    await orderApi.rate(params)
    ElMessage.success('评价成功')
    router.back()
  } catch { /* ignore */ }
  finally { submitting.value = false }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f5; }
.page-header { display:flex; align-items:center; gap:4px; padding:12px 16px; background:#fff; border-bottom:1px solid #f0f0f0; }
.page-header h2 { font-size:17px; font-weight:600; color:#303133; margin:0; }
.body { padding:12px 16px; }

.review-card { background:#fff; border-radius:10px; padding:16px; margin-bottom:12px; }
.review-header { display:flex; align-items:center; gap:12px; margin-bottom:12px; }
.review-img { width:48px; height:48px; border-radius:6px; background:#f5f5f5; flex-shrink:0; overflow:hidden; display:flex; align-items:center; justify-content:center; }
.review-img img { width:100%; height:100%; object-fit:cover; }
.review-name { font-size:14px; color:#303133; }
.review-rate { margin-bottom:12px; }
.review-input { width:100%; }

.submit-btn { width:100%; height:44px; border-radius:22px; font-size:16px; margin-top:8px; }
</style>
