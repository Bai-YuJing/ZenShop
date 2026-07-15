<template>
  <div class="user-page">
    <!-- 搜索 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="form" inline>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="36">
              {{ row.username?.[0] }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="注册时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewRatings(row)">查看评价</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="form.size"
        v-model:current-page="form.page"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 评价列表弹窗 -->
    <el-dialog v-model="ratingsVisible" :title="'用户评价 - ' + ratingUserName" width="900px" top="5vh">
      <el-table :data="ratingsData" stripe v-loading="ratingsLoading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="productName" label="商品" min-width="140" />
        <el-table-column prop="content" label="评价内容" min-width="200" />
        <el-table-column prop="score" label="评分" width="90" align="center">
          <template #default="{ row }">
            <el-rate :model-value="row.score" disabled :max="5" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="评价时间" width="170" />
      </el-table>
      <el-pagination
        v-if="ratingsTotal > 0"
        background
        layout="total, prev, pager, next"
        :total="ratingsTotal"
        :page-size="ratingsPageSize"
        v-model:current-page="ratingsPage"
        @current-change="onRatingsPageChange"
        small
        style="margin-top: 12px; justify-content: flex-end;"
      />
      <el-empty v-if="!ratingsLoading && ratingsData.length === 0" description="暂无评价" />
      <template #footer>
        <el-button @click="ratingsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userManageApi } from '@/api'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const form = reactive({
  username: null,
  phone: null,
  status: null,
  page: 1,
  size: 20,
})

async function loadData() {
  loading.value = true
  try {
    const res = (await userManageApi.page(form)) as any
    tableData.value = (res.records ?? res) as any[]
    total.value = res.total ?? 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

function handleSearch() {
  form.page = 1
  loadData()
}

function handleReset() {
  form.username = null
  form.phone = null
  form.status = null
  form.page = 1
  loadData()
}

// ============ 查看评价 ============
const ratingsVisible = ref(false)
const ratingsLoading = ref(false)
const ratingUserName = ref('')

/** 全量评价数据（接口原始返回） */
const allRatings = ref<any[]>([])
/** 当前分页数据 */
const ratingsData = ref<any[]>([])
const ratingsTotal = ref(0)
const ratingsPageSize = 10
const ratingsPage = ref(1)

function applyRatingsPage() {
  const start = (ratingsPage.value - 1) * ratingsPageSize
  const end = start + ratingsPageSize
  ratingsData.value = allRatings.value.slice(start, end)
}

function onRatingsPageChange(page: number) {
  ratingsPage.value = page
  applyRatingsPage()
}

async function handleViewRatings(row: any) {
  ratingUserName.value = row.username ?? row.id
  ratingsVisible.value = true
  ratingsLoading.value = true
  ratingsPage.value = 1
  try {
    const res = (await userManageApi.ratings(row.id)) as any
    allRatings.value = Array.isArray(res) ? res : []
    ratingsTotal.value = allRatings.value.length
    applyRatingsPage()
  } catch {
    allRatings.value = []
    ratingsData.value = []
    ratingsTotal.value = 0
  } finally {
    ratingsLoading.value = false
  }
}

// ============ 启用/禁用 ============
async function handleToggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const label = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${label}用户「${row.username}」吗？`, '提示')
    await userManageApi.updateStatus(row.id, newStatus)
    ElMessage.success(`已${label}`)
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

// ============ 删除 ============
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？删除后不可恢复。`, '提示', {
      confirmButtonClass: 'el-button--danger',
      type: 'warning',
    })
    await userManageApi.remove(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card,
.table-card {
  border-radius: 8px;
}
</style>
