<template>
  <div class="page" ref="scrollRef" @scroll="onScroll">
    <div class="page-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon></el-button>
      <h2>商家详情</h2>
    </div>

    <div v-loading="loading" class="body">
      <template v-if="shop">
        <div class="shop-header">
          <div class="shop-logo">
            <img v-if="shop.logo" :src="shop.logo" />
            <span v-else class="logo-text">{{ shop.name?.charAt(0) }}</span>
          </div>
          <div class="shop-info">
            <h3>{{ shop.name }}</h3>
            <span class="shop-rating">评分 {{ shop.ratting ?? '-' }}</span>
            <span class="shop-cate">{{ shop.categoryName }}</span>
          </div>
        </div>

        <div class="tabs">
          <span :class="['tab', { active: activeTab === 'cate' }]" @click="switchTab('cate')">商品分类</span>
          <span :class="['tab', { active: activeTab === 'info' }]" @click="switchTab('info')">商家信息</span>
        </div>

        <div v-if="activeTab === 'info'" class="info-section">
          <div class="info-row"><span class="label">地址</span><span class="value">{{ shop.detailedAddress || '未设置' }}</span></div>
          <div class="info-row"><span class="label">电话</span><span class="value">{{ shop.phone }}</span></div>
          <div class="info-row"><span class="label">简介</span><span class="value">{{ shop.description || '暂无' }}</span></div>
        </div>

        <div v-if="activeTab === 'cate'" class="cate-layout">
          <div class="cate-side">
            <div
              v-for="c in shop.categories"
              :key="c.id"
              :class="['cate-item', { active: selectedCateId === c.id }]"
              @click="selectCategory(c.id)"
            >{{ c.name }}</div>
          </div>
          <div class="product-area" v-if="selectedCateId">
            <div v-for="p in productList" :key="p.id" class="product-item" @click="goProduct(p)">
              <div class="product-img">
                <img v-if="p.images?.length" :src="p.images[0]" />
              </div>
              <div class="product-info">
                <p class="product-name">{{ p.name }}</p>
                <span class="product-price">¥{{ (p.price || 0).toFixed(2) }}</span>
              </div>
            </div>
            <div class="load-status">{{ productLoading ? '加载中...' : productNoMore ? '没有更多了' : '' }}</div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { userIndexApi } from '@/api'

const route = useRoute()
const router = useRouter()
const scrollRef = ref<HTMLDivElement>()
const loading = ref(false)
const shop = ref<any>(null)
const activeTab = ref('cate')
const selectedCateId = ref<number | null>(null)
const productList = ref<any[]>([])
const productPage = ref(1)
const productLoading = ref(false)
const productNoMore = ref(false)

onMounted(async () => {
  const id = route.query.id as string
  if (!id) return
  loading.value = true
  try {
    const res: any = await userIndexApi.getBusinessDetail(id)
    shop.value = res
  } catch { /* ignore */ }
  finally {
    loading.value = false
    if (shop.value?.categories?.length) {
      selectCategory(shop.value.categories[0].id)
    }
  }
})

function switchTab(tab: string) {
  activeTab.value = tab
}

function selectCategory(cateId: number) {
  selectedCateId.value = cateId
  productList.value = []
  productPage.value = 1
  productNoMore.value = false
  loadProducts(true)
}

async function loadProducts(reset = false) {
  if (productLoading.value || productNoMore.value) return
  productLoading.value = true
  try {
    const res: any = await userIndexApi.getProductsByBusiness({
      businessId: route.query.id,
      categoryId: selectedCateId.value,
      page: reset ? 0 : productPage.value,
      size: 15,
    })
    const records = res?.records || []
    if (reset) { productList.value = records; productPage.value = 1 }
    else { productList.value.push(...records); productPage.value++ }
    if (records.length < 15) productNoMore.value = true
  } catch { productList.value = [] }
  finally { productLoading.value = false }
}

function onScroll() {
  const el = scrollRef.value
  if (!el || productLoading.value || productNoMore.value || activeTab.value !== 'cate') return
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) loadProducts()
}

function goProduct(p: any) {
  router.push(`/user/product?id=${p.id}`)
}
</script>

<style scoped>
.page { height:100%; display:flex; flex-direction:column; }
.page-header { display:flex; align-items:center; gap:4px; padding:12px 16px; background:#fff; border-bottom:1px solid #f0f0f0; position:sticky; top:0; z-index:10; }
.page-header h2 { font-size:17px; font-weight:600; color:#303133; margin:0; }
.body { padding:16px; display:flex; flex-direction:column; flex:1; min-height:0; }

.shop-header { display:flex; align-items:center; gap:12px; background:linear-gradient(135deg, #667eea, #764ba2); padding:24px 20px; border-radius:10px; margin-bottom:12px; }
.shop-logo { width:56px; height:56px; border-radius:50%; overflow:hidden; background:#f0f2ff; display:flex; align-items:center; justify-content:center; flex-shrink:0; font-size:22px; font-weight:600; color:#667eea; }
.shop-logo img { width:100%; height:100%; object-fit:cover; }
.shop-info { display:flex; flex-direction:column; gap:4px; }
.shop-info h3 { font-size:18px; font-weight:600; color:#fff; margin:0; }
.shop-rating { font-size:13px; color:rgba(255,255,255,0.9); }
.shop-cate { font-size:12px; color:rgba(255,255,255,0.7); }
.shop-logo { border:2px solid rgba(255,255,255,0.5); }

.tabs { display:flex; background:#fff; border-radius:10px; overflow:hidden; margin-bottom:12px; }
.tab { flex:1; text-align:center; padding:12px 0; font-size:14px; color:#606266; cursor:pointer; transition:all 0.2s; }
.tab.active { background:#409eff; color:#fff; font-weight:500; }

.info-section { background:#fff; border-radius:10px; padding:16px; margin-bottom:12px; }
.info-row { display:flex; padding:8px 0; border-bottom:1px solid #f5f5f5; font-size:14px; }
.info-row:last-child { border-bottom:none; }
.label { color:#909399; width:60px; flex-shrink:0; }
.value { color:#303133; flex:1; }

.cate-layout { display:flex; gap:0; background:#fff; border-radius:10px; overflow:hidden; flex:1; min-height:0; }
.cate-side { width:90px; flex-shrink:0; background:#f8f8f8; overflow-y:auto; height:100%; }
.cate-item { padding:14px 12px; font-size:13px; color:#606266; cursor:pointer; border-bottom:1px solid #f0f0f0; transition:all 0.2s; text-align:center; }
.cate-item.active { background:#fff; color:#409eff; font-weight:500; }
.cate-item:last-child { border-bottom:none; }
.product-area { flex:1; padding:12px; overflow-y:auto; }

.product-item { display:flex; gap:12px; padding:12px; background:#fff; border-radius:8px; margin-bottom:10px; cursor:pointer; box-shadow:0 1px 4px rgba(0,0,0,0.04); }
.product-img { width:60px; height:60px; border-radius:6px; background:#f5f5f5; flex-shrink:0; overflow:hidden; display:flex; align-items:center; justify-content:center; }
.product-img img { width:100%; height:100%; object-fit:cover; }
.product-info { flex:1; min-width:0; display:flex; flex-direction:column; justify-content:center; gap:4px; }
.product-name { font-size:14px; color:#303133; margin:0; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.product-price { font-size:16px; font-weight:600; color:#f56c6c; }
.load-status { text-align:center; padding:16px; font-size:13px; color:#909399; }
</style>
