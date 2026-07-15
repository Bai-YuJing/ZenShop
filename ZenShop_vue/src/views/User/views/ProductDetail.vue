<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>商品详情</h2>
    </div>

    <div v-loading="loading" class="detail-wrap">
      <template v-if="product">
        <!-- 图片轮播 -->
        <el-carousel class="image-carousel" height="300px" indicator-position="outside" arrow="always">
          <el-carousel-item v-for="(img, idx) in product.images" :key="idx">
            <img :src="img.url" class="carousel-img" />
          </el-carousel-item>
        </el-carousel>

        <div class="info-section">
          <h3 class="product-name">{{ product.name }}</h3>
          <div class="price-row">
            <span class="price">¥{{ product.price?.toFixed(2) }}</span>
          </div>
          <div class="meta-row">
            <span>评分 {{ product._rating ?? '-' }}</span>
            <span>已售 {{ product.sales || 0 }}</span>
            <span>库存 {{ product.stock || 0 }}</span>
          </div>
          <p class="desc">{{ product.description || '暂无描述' }}</p>
        </div>

        <!-- 商家信息 -->
        <div class="shop-section" v-if="product.businessName">
          <div class="shop-logo">
            <img v-if="product.businessLogo" :src="product.businessLogo" />
            <span v-else class="shop-logo-text">{{ product.businessName?.charAt(0) }}</span>
          </div>
          <div class="shop-info">
            <span class="shop-name">{{ product.businessName }}</span>
            <span class="shop-rating">评分 {{ product.businessRating ?? '-' }}</span>
          </div>
        </div>

        <!-- 评论 -->
        <div class="review-section">
          <h4 class="review-title">商品评价</h4>
          <div v-for="r in ratings" :key="r.id" class="rating-item">
            <div class="rating-header">
              <span class="rating-user">{{ r.username || '匿名用户' }}</span>
              <el-rate :model-value="r.score ?? 0" disabled :max="5" size="small" />
              <span class="rating-time">{{ r.createdTime || '' }}</span>
            </div>
            <p class="rating-content">{{ r.content || '' }}</p>
          </div>
          <p v-if="!loadingRatings && !ratings.length" class="no-review">暂无评论</p>
        </div>
      </template>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <el-button class="btn-cart" @click="addCart">加入购物车</el-button>
      <el-button type="primary" class="btn-buy" @click="buyNow">立即购买</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { userIndexApi, cartApi } from '@/api'

const ratings = ref<any[]>([])
const loadingRatings = ref(false)

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const product = ref<any>(null)

onMounted(async () => {
  const id = Number(route.query.id)
  if (!id) return
  loading.value = true
  try {
    const res: any = await userIndexApi.getProductDetail(id)
    const vo = res?.productIndexVo || res
    product.value = { ...vo, businessName: res?.businessName, businessLogo: res?.businessLogo, businessRating: res?.businessRating }
    product.value._rating = res?.rating
    loadingRatings.value = true
    userIndexApi.getProductRatings({ productId: Number(id), page: 0, size: 10 }).then((r: any) => {
      ratings.value = (r as any)?.records || []
    }).finally(() => { loadingRatings.value = false })
  } catch { /* ignore */ }
  finally { loading.value = false }
})

async function addCart() {
  if (!product.value?.id) return
  try {
    await cartApi.add({ productId: product.value.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch { /* ignore */ }
}

function buyNow() {
  if (!product.value?.id) return
  router.push(`/user/order/confirm?ids=${product.value.id}&qtys=1`)
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header h2 { font-size: 17px; font-weight: 600; color: #303133; margin: 0; }

.image-carousel {
  background: #fff;
}

.carousel-img {
  width: 100%;
  height: 300px;
  object-fit: contain;
  display: block;
  background: #f5f5f5;
}

.info-section {
  background: #fff;
  padding: 20px 16px;
  margin-top: 12px;
}

.product-name {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  line-height: 1.4;
}

.price-row { margin-bottom: 10px; }

.price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.meta-row {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.shop-section {
  display:flex; align-items:center; gap:12px;
  background:#fff; padding:16px; margin-top:12px;
}

.shop-logo {
  width:40px; height:40px; border-radius:50%; overflow:hidden;
  background:#f0f2ff; display:flex; align-items:center; justify-content:center;
  flex-shrink:0; color:#667eea; font-size:16px; font-weight:600;
}

.shop-logo img { width:100%; height:100%; object-fit:cover; }

.shop-info { display:flex; flex-direction:column; gap:2px; }
.shop-name { font-size:14px; font-weight:600; color:#303133; }
.shop-rating { font-size:12px; color:#909399; }

.review-section {
  background: #fff;
  padding: 20px 16px;
  margin-top: 12px;
}

.review-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.no-review {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 24px 0;
  margin: 0;
}

.rating-item { padding:12px 0; border-bottom:1px solid #f5f5f5; }
.rating-item:last-child { border-bottom:none; }
.rating-header { display:flex; align-items:center; gap:8px; margin-bottom:4px; }
.rating-user { font-size:13px; font-weight:500; color:#303133; }
.rating-time { font-size:11px; color:#909399; margin-left:auto; }
.rating-content { font-size:13px; color:#606266; margin:0; line-height:1.5; }

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 10px;
  padding: 10px 16px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom, 0));
  background: #fff;
  border-top: 1px solid #f0f0f0;
  z-index: 100;
}

.btn-cart {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  border-color: #409eff;
  color: #409eff;
}

.btn-buy {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
}

.page {
  padding-bottom: 70px;
}
</style>
