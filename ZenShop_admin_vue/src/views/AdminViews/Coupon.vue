<template>
  <div class="coupon-page">
    <!-- 搜索 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="form" inline>
        <el-form-item label="优惠券名称">
          <el-input v-model="form.name" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="满减" :value="1" />
            <el-option label="折扣" :value="2" />
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
      <div class="table-header">
        <span class="table-title">优惠券列表</span>
        <el-button type="primary" @click="handleAdd">新增优惠券</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%" @sort-change="handleSortChange">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" min-width="130" />
        <el-table-column prop="description" label="描述" min-width="150" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'warning'" size="small">
              {{ row.type === 1 ? '满减' : '折扣' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="150">
          <template #default="{ row }">
            <span v-if="row.type === 1">减 ¥{{ row.discountAmount }}</span>
            <span v-else>{{ Math.round(row.discountRate * 10) }}折</span>
          </template>
        </el-table-column>
        <el-table-column label="最低门槛" width="100">
          <template #default="{ row }">
            {{ row.minAmount != null ? '¥' + row.minAmount : '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="total" label="剩余数量" width="120" sortable="custom" />
        <el-table-column label="有效期" min-width="200">
          <template #default="{ row }">
            {{ formatDate(row.validFrom) }} ~ {{ formatDate(row.validTo) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑优惠券' : '新增优惠券'"
      width="560px"
      top="8vh"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="editForm.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" placeholder="简短说明，如「满100减20」" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-radio-group v-model="editForm.type">
            <el-radio :value="1">满减</el-radio>
            <el-radio :value="2">折扣</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="editForm.type === 1">
          <el-form-item label="减免金额" required>
            <el-input-number v-model="editForm.discountAmount" :min="0.01" :step="1" :precision="2" />
          </el-form-item>
          <el-form-item label="最低门槛">
            <el-input-number v-model="editForm.minAmount" :min="0" :step="1" :precision="2" />
            <span style="margin-left: 8px; color: #909399;">0 表示无门槛</span>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="折扣率" required>
            <el-input-number v-model="editForm.discountRate" :min="0.01" :max="0.99" :step="0.05" :precision="2" />
            <span style="margin-left: 8px; color: #909399;">0.80 = 8折</span>
          </el-form-item>
          <el-form-item label="最高减免">
            <el-input-number v-model="editForm.maxDiscountAmount" :min="0" :step="10" :precision="2" />
            <span style="margin-left: 8px; color: #909399;">不填表示不设上限</span>
          </el-form-item>
          <el-form-item label="最低门槛">
            <el-input-number v-model="editForm.minAmount" :min="0" :step="1" :precision="2" />
          </el-form-item>
        </template>
        <el-form-item label="发放总量">
          <el-input-number v-model="editForm.total" :min="0" :step="10" :precision="0" />
          <span style="margin-left: 8px; color: #909399;">0 表示不限量</span>
        </el-form-item>
        <el-form-item label="有效期" required>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { couponApi } from '@/api'

const loading = ref(false)
const saving = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const editingId = ref<number | null>(null)
const dialogVisible = ref(false)

const form = reactive({
  name: null,
  type: null,
  remainingSort: null as string | null,
  page: 1,
  size: 20,
})

const defaultForm = {
  name: '',
  description: '',
  type: 1,
  discountAmount: null,
  discountRate: null,
  maxDiscountAmount: null,
  minAmount: null,
  total: null,
  validFrom: '',
  validTo: '',
}

const editForm = reactive({ ...defaultForm })
const dateRange = ref<any[]>([])

function formatDate(iso: string) {
  if (!iso) return '-'
  return iso.replace('T', ' ').substring(0, 19)
}

async function loadData() {
  loading.value = true
  try {
    const res = (await couponApi.page(form)) as any
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
  form.name = null
  form.type = null
  form.remainingSort = null
  form.page = 1
  loadData()
}

function handleSortChange(sort: { prop: string; order: string | null }) {
  if (sort.prop === 'total') {
    form.remainingSort = sort.order === 'ascending' ? 'ASC' : sort.order === 'descending' ? 'DESC' : null
    form.page = 1
    loadData()
  }
}

function resetEditForm() {
  Object.assign(editForm, defaultForm)
  dateRange.value = []
}

function handleAdd() {
  editingId.value = null
  resetEditForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  editingId.value = row.id
  editForm.name = row.name
  editForm.description = row.description ?? ''
  editForm.type = row.type
  editForm.discountAmount = row.discountAmount
  editForm.discountRate = row.discountRate
  editForm.maxDiscountAmount = row.maxDiscountAmount
  editForm.minAmount = row.minAmount
  editForm.total = row.total
  editForm.validFrom = row.validFrom ?? ''
  editForm.validTo = row.validTo ?? ''
  dateRange.value = row.validFrom && row.validTo ? [row.validFrom, row.validTo] : []
  dialogVisible.value = true
}

async function handleSave() {
  if (!editForm.name.trim()) {
    ElMessage.warning('请输入优惠券名称')
    return
  }
  if (!dateRange.value?.length) {
    ElMessage.warning('请选择有效期')
    return
  }
  saving.value = true
  try {
    editForm.validFrom = dateRange.value[0]
    editForm.validTo = dateRange.value[1]
    if (editingId.value) {
      await couponApi.update({ id: editingId.value, ...editForm })
      ElMessage.success('更新成功')
    } else {
      await couponApi.create(editForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除优惠券「${row.name}」吗？`, '提示')
    await couponApi.remove(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.coupon-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card,
.table-card {
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
