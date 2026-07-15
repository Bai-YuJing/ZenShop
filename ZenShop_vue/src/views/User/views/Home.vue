<template>
  <div class="home-page">
    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="searchText"
        placeholder="搜索商品"
        class="search-input"
        @keyup.enter="doSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 商家分类 -->
    <div class="section">
      <div class="category-grid">
        <div
          v-for="item in categoryList"
          :key="item.id"
          class="category-item"
          @click="handleCategory(item)"
        >
          <div class="category-icon" :style="{ background: iconMap[item.id]?.bg }">
            <el-icon :size="24" :style="{ color: iconMap[item.id]?.color }">
              <component :is="iconMap[item.id]?.icon" />
            </el-icon>
          </div>
          <span class="category-name">{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 销量TOP10 -->
    <div class="section top-section">
      <h3 class="section-title">销量排行 TOP10</h3>
      <div v-for="(item, idx) in topList" :key="item.id" class="top-item" @click="goShop(item)">
        <span class="top-rank" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</span>
        <div class="top-img">
          <img v-if="item.images?.length" :src="item.images[0]" />
          <el-icon v-else :size="20"><Goods /></el-icon>
        </div>
        <div class="top-info">
          <p class="top-name">{{ item.name }}</p>
          <span class="top-price">¥{{ item.price?.toFixed(2) }}</span>
        </div>
        <span class="top-sales">已售 {{ item.sales }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Goods, Reading, HomeFilled, Monitor, Present, Trophy, Fries, Star, Headset, Coffee } from '@element-plus/icons-vue'
import { userIndexApi } from '@/api'

const router = useRouter()
const searchText = ref('')

function doSearch() {
  if (searchText.value.trim()) {
    router.push(`/user/search?keyword=${encodeURIComponent(searchText.value.trim())}`)
  }
}

const iconMap: Record<number, { icon: any; bg: string; color: string }> = {
  1: { icon: Coffee,     bg: '#fff5e6', color: '#e6a23c' },  // 餐饮美食
  2: { icon: Goods,      bg: '#f0f2ff', color: '#667eea' },  // 服装鞋帽
  3: { icon: Monitor,    bg: '#e6f7ff', color: '#409eff' },  // 数码电器
  4: { icon: Star,       bg: '#fce4ec', color: '#f56c6c' },  // 美妆护肤
  5: { icon: HomeFilled, bg: '#f0f9eb', color: '#67c23a' },  // 家居生活
  6: { icon: Present,    bg: '#fff0f6', color: '#eb2f96' },  // 母婴用品
  7: { icon: Headset,    bg: '#e6f7ff', color: '#409eff' },  // 运动户外
  8: { icon: Reading,    bg: '#fdf6ec', color: '#e6a23c' },  // 图书文具
  9: { icon: Trophy,     bg: '#f0f2ff', color: '#667eea' },  // 珠宝首饰
  10: { icon: Fries,     bg: '#f0f9eb', color: '#67c23a' },  // 生鲜食品
}

const categoryList = ref<{ id: number; name: string }[]>([])
const topList = ref<any[]>([])

onMounted(async () => {
  try {
    const [catRes, topRes] = await Promise.all([
      userIndexApi.getCategory(),
      userIndexApi.getTop10(),
    ])
    categoryList.value = (catRes || []) as any
    topList.value = (topRes as any)?.records || []
  } catch { /* ignore */ }
})

function goShop(item: any) {
  router.push(`/user/product?id=${item.id}`)
}

function handleCategory(item: { id: number; name: string }) {
  router.push(`/user/shop-list?id=${item.id}&name=${encodeURIComponent(item.name)}`)
}
</script>

<style scoped>
.home-page {
  padding: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border-radius: 20px;
  padding: 4px 16px;
  margin: 12px 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  color: #c0c4cc;
}

.search-box :deep(.el-input__wrapper) {
  background: transparent;
  box-shadow: none !important;
  padding: 0;
}

.search-box :deep(.el-input__inner) {
  border: none;
  font-size: 14px;
  height: 36px;
}

/* 分类 */
.section {
  margin: 24px 16px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 4px;
  background: #fff;
  border-radius: 10px;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.category-item:active {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-name {
  font-size: 11px;
  color: #606266;
  text-align: center;
  line-height: 1.3;
}

/* TOP10 */
.top-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
}

.top-rank {
  width: 20px;
  font-size: 14px;
  font-weight: 700;
  color: #909399;
  text-align: center;
}

.top-rank.rank-1 { color: #f56c6c; }
.top-rank.rank-2 { color: #e6a23c; }
.top-rank.rank-3 { color: #409eff; }

.top-img {
  width: 44px;
  height: 44px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  flex-shrink: 0;
}

.top-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.top-info {
  flex: 1;
  min-width: 0;
}

.top-name {
  font-size: 14px;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.top-price {
  font-size: 15px;
  font-weight: 600;
  color: #f56c6c;
}

.top-sales {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
</style>
