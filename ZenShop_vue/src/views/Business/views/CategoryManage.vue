<template>
  <div class="category-page">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>分类管理</h2>
        <span class="header-count">共 {{ total }} 个分类</span>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchText"
          placeholder="搜索分类名称"
          clearable
          class="search-input"
          @input="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-tooltip v-if="total >= 20" content="分类数量已达上限（20个）" placement="top">
          <el-button type="primary" disabled style="background:#303133;border-color:#303133">
            <el-icon><Plus /></el-icon> 添加分类
          </el-button>
        </el-tooltip>
        <el-button v-else type="primary" @click="dialogVisible = true">
          <el-icon><Plus /></el-icon> 添加分类
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-card">
      <el-table
        :data="categoryList"
        style="width: 100%"
        v-loading="loading"
        stripe
        header-cell-class-name="table-header"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" align="center">
          <template #default="{ row }">
            <span class="sort-value">{{ row.sort }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑分类' : '添加分类'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form label-width="90px" class="dialog-form">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
          <span class="form-tip">数字越小越靠前</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { businessCategoryApi } from '@/api'

const searchText = ref('')
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const loading = ref(false)

const form = ref({
  name: '',
  sort: 0,
})

const categoryList = ref<{ id: number; name: string; sort: number; status: number; createdTime: string }[]>([])

const total = computed(() => categoryList.value.length)

let searchTimer: ReturnType<typeof setTimeout> | null = null
function handleSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchList(searchText.value || undefined)
  }, 300)
}

async function fetchList(name?: string) {
  loading.value = true
  try {
    const res = await businessCategoryApi.getList(name ? { name } : undefined)
    categoryList.value = (res || []) as any
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})

function handleEdit(row: { id: number; name: string; sort: number }) {
  isEditing.value = true
  editingId.value = row.id
  form.value = { name: row.name, sort: row.sort }
  dialogVisible.value = true
}

async function toggleStatus(row: { id: number; status: number; name: string }) {
  const newStatus = row.status === 1 ? 0 : 1
  // 下架时提醒用户
  if (newStatus === 0) {
    try {
      await ElMessageBox.confirm(
        `分类「${row.name}」下架后，该分类下的所有商品也将同步下架，确定要继续吗？`,
        '下架提醒',
        { confirmButtonText: '确定下架', cancelButtonText: '取消', type: 'warning' }
      )
    } catch {
      return // 用户取消
    }
  }
  try {
    await businessCategoryApi.updateStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  } catch {
    // 错误已在拦截器中处理
  }
}

async function handleSubmit() {
  if (!form.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    if (isEditing.value && editingId.value != null) {
      await businessCategoryApi.update({ id: editingId.value, name: form.value.name.trim(), sort: form.value.sort })
      ElMessage.success('修改成功')
    } else {
      await businessCategoryApi.add({ name: form.value.name.trim(), sort: form.value.sort })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    form.value = { name: '', sort: 0 }
    await fetchList()
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.category-page {
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.header-left h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-count {
  font-size: 13px;
  color: #909399;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 220px;
}

.table-card {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 16px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow-y: auto;
}

.table-card :deep(.table-header) {
  background: #fff;
  color: #303133;
  font-weight: 600;
}

.sort-value {
  color: #606266;
}

.dialog-form {
  padding: 8px 0;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 12px;
}
</style>
