<template>
  <el-form :model="form" label-width="80">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-form-item label="商家名称">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 140px">
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="待提交" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="正常" :value="2" />
            <el-option label="冻结" :value="3" />
            <el-option label="已关闭" :value="4" />
            <el-option label="审核未通过" :value="5" />
            <el-option label="修改审核" :value="6" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="4">
        <el-form-item label="省份">
          <el-select
            v-model="form.provinceId"
            placeholder="请选择省份"
            clearable
            style="width: 140px"
            @change="onProvinceChange"
          >
            <el-option
              v-for="p in provinces"
              :key="p.value"
              :label="p.label"
              :value="p.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item label="城市">
          <el-select
            v-model="form.cityId"
            placeholder="请选择城市"
            clearable
            style="width: 140px"
            :disabled="!form.provinceId"
            @change="onCityChange"
          >
            <el-option
              v-for="c in cities"
              :key="c.value"
              :label="c.label"
              :value="c.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item label="区县">
          <el-select
            v-model="form.districtId"
            placeholder="请选择区县"
            clearable
            style="width: 140px"
            :disabled="!form.cityId"
          >
            <el-option
              v-for="d in districts"
              :key="d.value"
              :label="d.label"
              :value="d.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item label="评分区间">
          <el-select v-model="ratingRange" placeholder="全部" clearable style="width: 140px" @change="onRatingRangeChange">
            <el-option label="0 - 2" :value="0" />
            <el-option label="2 - 4" :value="1" />
            <el-option label="4 - 5" :value="2" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col style="text-align: right">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-col>
    </el-row>
  </el-form>
    <el-table :data="tableData" style="width: 100%" stripe @sort-change="handleSortChange">
    <el-table-column type="index" label="序号" width="90" />
	    <el-table-column prop="id" label="ID" width="200" />
    <el-table-column prop="name" label="店名" width="150" />
    <el-table-column prop="phone" label="手机号" width="150" />
    <el-table-column prop="email" label="邮箱" width="200" />
    <el-table-column prop="address" label="省-市-区" width="180" />
    <el-table-column prop="updatedTime" label="更新时间" width="180" />
    <el-table-column prop="categoryName" label="分类" width="160" />
    <el-table-column label="评分" width="100" sortable="custom" prop="ratting">
      <template #default="{ row }">
        <el-tag v-if="row.ratting != null" :type="ratingTagType(row.ratting)" size="small" effect="dark" style="border: none">
          {{ row.ratting }}
        </el-tag>
        <span v-else class="text-muted">-</span>
      </template>
    </el-table-column>
    <el-table-column label="状态" width="110">
      <template #default="{ row }">
        <el-tag :type="statusTagType(row.status)" size="small">
          {{ row.statusLabel }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column fixed="right" label="操作" width="120">
      <template #default="{ row }">
        <el-button link type="primary" size="small" @click="handleClick(row)">
          查看
        </el-button>
      </template>
    </el-table-column>
  </el-table>
  <el-pagination
    background
    layout="total, prev, pager, next"
    :total="total"
    :page-size="form.size"
    v-model:current-page="form.page"
    @current-change="getBusiness"
  />

  <!-- 商家详情弹窗 -->
  <el-dialog
    v-model="detailVisible"
    title="商家详情"
    width="680px"
    top="5vh"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="商家ID" :span="2">{{ detailData.id }}</el-descriptions-item>
      <el-descriptions-item label="商家名称" :span="2">
        <div style="display: flex; align-items: center; gap: 12px">
          <el-image
            v-if="detailData.logo"
            :src="detailData.logo"
            style="width: 48px; height: 48px; border-radius: 6px; flex-shrink: 0"
            fit="cover"
            :preview-src-list="[detailData.logo]"
            hide-on-click-modal
          />
          <span>{{ detailData.name }}</span>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ detailData.email }}</el-descriptions-item>
      <el-descriptions-item label="分类">{{ detailData.categoryName }}</el-descriptions-item>
      <el-descriptions-item label="评分">{{ detailData.ratting ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="所在地区" :span="2">
        {{ detailData.address || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">
        {{ detailData.detailedAddress || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态" :span="2">
        <div style="display: flex; align-items: center; gap: 8px">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ detailData.status != null ? (statusMap[detailData.status] ?? detailData.status) : '-' }}
          </el-tag>
          <el-button
            v-if="detailData.status === 3 || detailData.status === 5"
            link
            type="primary"
            size="small"
            @click="handleViewReason(detailData)"
          >
            查看原因
          </el-button>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label="描述" :span="2">
        {{ detailData.description || '暂无描述' }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData.createdTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detailData.updatedTime }}</el-descriptions-item>
      <el-descriptions-item label="最后登录时间" :span="2">{{ detailData.lastLoginTime ?? '从未登录' }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <div style="display: flex; justify-content: center; gap: 16px" v-if="detailData.status === 1 || detailData.status === 6">
        <el-button type="success" @click="handleApprove(detailData)" :loading="auditing">
          通过
        </el-button>
        <el-button type="danger" @click="handleReject(detailData)" :loading="auditing">
          未通过
        </el-button>
      </div>
      <div style="display: flex; justify-content: center; gap: 16px" v-else-if="detailData.status === 2">
        <el-button type="danger" @click="handleFreeze(detailData)" :loading="auditing">
          冻结
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 驳回原因弹窗 -->
  <el-dialog v-model="rejectDialogVisible" title="选择驳回原因" width="420px" top="30vh">
    <el-select v-model="rejectReason" placeholder="请选择驳回原因" style="width: 100%">
      <el-option
        v-for="r in rejectReasons"
        :key="r.value"
        :label="r.label"
        :value="r.value"
      />
    </el-select>
    <template #footer>
      <el-button @click="rejectDialogVisible = false">取消</el-button>
      <el-button type="danger" :loading="auditing" :disabled="!rejectReason" @click="confirmReject">
        确认驳回
      </el-button>
    </template>
  </el-dialog>

  <!-- 冻结原因弹窗 -->
  <el-dialog v-model="freezeDialogVisible" title="选择冻结原因" width="420px" top="30vh">
    <el-select v-model="freezeReason" placeholder="请选择冻结原因" style="width: 100%">
      <el-option
        v-for="r in freezeReasons"
        :key="r.value"
        :label="r.label"
        :value="r.value"
      />
    </el-select>
    <template #footer>
      <el-button @click="freezeDialogVisible = false">取消</el-button>
      <el-button type="danger" :loading="auditing" :disabled="!freezeReason" @click="confirmFreeze">
        确认冻结
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, reactive } from 'vue'
import { regionData } from '@/data/region-data'
import {businessApi} from "@/api"
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'



interface CategoryOption {
  id: number
  name: string
}

const categories = ref<CategoryOption[]>([])

const ratingRange = ref<number | null>(null)

function onRatingRangeChange(val: number | null) {
  if (val === 0) {
    form.minRating = 0
    form.maxRating = 2
  } else if (val === 1) {
    form.minRating = 2
    form.maxRating = 4
  } else if (val === 2) {
    form.minRating = 4
    form.maxRating = 5
  } else {
    form.minRating = null
    form.maxRating = null
  }
  handleSearch()
}

const form = reactive({
  name: null,           // 商家名称
  provinceId: null,     // 省份 ID
  cityId: null,         // 城市 ID
  districtId: null,     // 区县 ID
  categoryId: null,     // 分类 ID
  status: null,         // 状态
  minRating: null as number | null,     // 评分下限
  maxRating: null as number | null,     // 评分上限
  ratingSort: null as string | null,    // 评分排序 ASC/DESC
  page:1,
  size:20
})


async function getCategory() {
  const res = await businessApi.getCategory() as unknown as CategoryOption[]
  if (res) {
    categories.value = res
  }
}

onMounted(() => {
  getCategory()
  getBusiness()
})
const tableData = ref<any[]>([])
const total = ref(0)

/** 状态映射 */
const statusMap: Record<number, string> = {
  0: '待提交',
  1: '待审核',
  2: '正常',
  3: '冻结',
  4: '已关闭',
  5: '审核未通过',
  6: '修改审核',
}

/** 状态对应 Element Plus tag 类型 */
function statusTagType(status: number | null): string {
  switch (status) {
    case 0: return 'info'      // 待提交 - 灰色
    case 1: return 'warning'   // 待审核 - 橙色
    case 2: return 'success'   // 正常 - 绿色
    case 3: return 'danger'    // 冻结 - 红色
    case 4: return 'info'      // 已关闭 - 灰色
    case 5: return 'danger'    // 审核未通过 - 红色
    case 6: return 'warning'   // 修改审核 - 橙色
    default: return 'info'
  }
}

/** 评分颜色：0-2 红, 2-4 橙, 4-5 绿 */
function ratingTagType(score: number): string {
  if (score <= 2) return 'danger'
  if (score <= 4) return 'warning'
  return 'success'
}

/** 根据 regionData 查名称（value 为 string，后端 ID 为 number） */
function getRegionName(id: number | null): string {
  if (!id) return ''
  for (const p of regionData) {
    if (Number(p.value) === id) return p.label
    for (const c of p.children ?? []) {
      if (Number(c.value) === id) return c.label
      for (const d of c.children ?? []) {
        if (Number(d.value) === id) return d.label
      }
    }
  }
  return ''
}

async function getBusiness() {
  // 后端 page 从 0 开始，分页组件从 1 开始，发请求时减 1
  const params = { ...form, page: form.page - 1 }
  const res = (await businessApi.page(params)) as any
  if (res) {
    // 分页接口返回 { records: [...], total: ... }
    const list = (res.records ?? res) as any[]
    tableData.value = list.map((item: any) => ({
      ...item,
      // 省市区名称拼接
      address: [item.provinceId, item.cityId, item.districtId]
        .map((id: number | null) => getRegionName(id))
        .filter(Boolean)
        .join('-'),
      // 状态文本
      statusLabel: statusMap[item.status] ?? item.status,
    }))
    total.value = res.total ?? 0
  }
}
/** 所有省份 */
const provinces = regionData

/** 当前省份下的城市 */
const cities = computed(() => {
  const p = provinces.find((item) => item.value === form.provinceId)
  return p?.children ?? []
})

/** 当前城市下的区县 */
const districts = computed(() => {
  const p = provinces.find((item) => item.value === form.provinceId)
  if (!p?.children) return []
  const c = p.children.find((item) => item.value === form.cityId)
  return c?.children ?? []
})

function onProvinceChange() {
  form.cityId = null
  form.districtId = null
}

function onCityChange() {
  form.districtId = null
}

/** 搜索 */
function handleSearch() {
  // TODO: 发起搜索请求
    getBusiness()
}

/** 重置 */
function handleReset() {
  form.name = null
  form.provinceId = null
  form.cityId = null
  form.districtId = null
  form.categoryId = null
  form.status = null
  form.minRating = null
  form.maxRating = null
  form.ratingSort = null
  form.page = 1
  getBusiness()
}

/** 评分排序 */
function handleSortChange(sort: { prop: string; order: string | null }) {
  if (sort.prop === 'ratting') {
    form.ratingSort = sort.order === 'ascending' ? 'ASC' : sort.order === 'descending' ? 'DESC' : null
    form.page = 1
    getBusiness()
  }
}
/** 用行数据填充详情弹窗（API 调用失败时的降级方案） */
function fillRowData(row: any) {
  detailData.id = row.id ?? ''
  detailData.name = row.name ?? ''
  detailData.description = row.description ?? ''
  detailData.logo = row.logo ?? ''
  detailData.phone = row.phone ?? ''
  detailData.email = row.email ?? ''
  detailData.categoryName = row.categoryName ?? ''
  detailData.ratting = row.ratting ?? null
  detailData.status = row.status ?? null
  detailData.provinceId = row.provinceId ?? null
  detailData.cityId = row.cityId ?? null
  detailData.districtId = row.districtId ?? null
  detailData.createdTime = row.createdTime ?? ''
  detailData.updatedTime = row.updatedTime ?? ''
  detailData.lastLoginTime = row.lastLoginTime ?? null
  detailData.detailedAddress = row.detailedAddress ?? ''
  detailData.address = [row.provinceId, row.cityId, row.districtId]
    .map((id: number | null) => getRegionName(id))
    .filter(Boolean)
    .join('-')
}

/** 商家详情弹窗 */
const detailVisible = ref(false)
const auditing = ref(false)
const detailData = reactive({
  id: '',
  name: '',
  description: '',
  logo: '',
  phone: '',
  email: '',
  categoryName: '',
  categoryId: null,
  ratting: null,
  status: null,
  provinceId: null,
  cityId: null,
  districtId: null,
  createdTime: '',
  updatedTime: '',
  lastLoginTime: null,
  detailedAddress: '',
  address: '',
})

const handleClick = async (row: any) => {
  detailVisible.value = true
  try {
    const api = row.status === 6 ? businessApi.getUpdateInfo : businessApi.getById
    const res = await api(row.id) as any
    const data = res?.data ?? res ?? row
    detailData.id = data.id ?? ''
    detailData.name = data.name ?? ''
    detailData.description = data.description ?? ''
    detailData.logo = data.logo ?? ''
    detailData.phone = data.phone ?? ''
    detailData.email = data.email ?? ''
    detailData.categoryName = data.categoryName ?? ''
    detailData.ratting = data.ratting ?? null
    detailData.status = data.status ?? null
    detailData.provinceId = data.provinceId ?? null
    detailData.cityId = data.cityId ?? null
    detailData.districtId = data.districtId ?? null
    detailData.createdTime = data.createdTime ?? ''
    detailData.updatedTime = data.updatedTime ?? ''
    detailData.lastLoginTime = data.lastLoginTime ?? null
    detailData.detailedAddress = data.detailedAddress ?? ''
    detailData.address = [data.provinceId, data.cityId, data.districtId]
      .map((id: number | null) => getRegionName(id))
      .filter(Boolean)
      .join('-')
  } catch {
    // 如果接口调用失败，降级使用表格行数据
    fillRowData(row)
  }
}

/** 审核通过 */
const handleApprove = async (row: any) => {
  auditing.value = true
  try {
    await ElMessageBox.confirm(`确定通过商家「${row.name}」的审核吗？`, '提示')
    if (detailData.status === 6) {
      await businessApi.updateBusiness(row.id)
    } else {
      await businessApi.audit(row.id, 2)
    }
    ElMessage.success('已通过')
    detailVisible.value = false
    getBusiness()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  } finally {
    auditing.value = false
  }
}

/** 驳回原因选项 */
const rejectReasons = [
  { value: 1, label: '信息不完整' },
  { value: 2, label: '证件不符' },
  { value: 3, label: '经营范围不符' },
  { value: 4, label: '资质过期' },
  { value: 5, label: '其他' },
]

/** 驳回相关状态 */
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectRow = ref<any>(null)

/** 冻结原因选项 */
const freezeReasons = [
  { value: 1, label: '违规经营' },
  { value: 2, label: '投诉过多' },
  { value: 3, label: '资质异常' },
  { value: 4, label: '店铺长期未运营' },
  { value: 5, label: '其他' },
]

/** 冻结相关状态 */
const freezeDialogVisible = ref(false)
const freezeReason = ref('')
const freezeRow = ref<any>(null)

/** 未通过：打开驳回原因弹窗 */
const handleReject = async (row: any) => {
  // 修改审核驳回不需要原因，直接提交
  if (row.status === 6) {
    auditing.value = true
    try {
      await ElMessageBox.confirm(`确定驳回商家「${row.name}」的修改申请吗？`, '提示')
      await businessApi.audit(row.id, 2)
      ElMessage.success('已驳回')
      detailVisible.value = false
      getBusiness()
    } catch (e: any) {
      if (e !== 'cancel') ElMessage.error('操作失败')
    } finally {
      auditing.value = false
    }
    return
  }
  rejectRow.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

/** 冻结商家：打开原因弹窗 */
const handleFreeze = (row: any) => {
  freezeRow.value = row
  freezeReason.value = ''
  freezeDialogVisible.value = true
}

/** 确认驳回 */
const confirmReject = async () => {
  if (!rejectReason.value) return
  auditing.value = true
  try {
    await businessApi.audit(rejectRow.value.id, 5, rejectReason.value)
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    detailVisible.value = false
    getBusiness()
  } catch (e: any) {
    ElMessage.error('操作失败')
  } finally {
    auditing.value = false
  }
}

/** 确认冻结 */
const confirmFreeze = async () => {
  if (!freezeReason.value) return
  auditing.value = true
  try {
    await businessApi.audit(freezeRow.value.id, 3, freezeReason.value)
    ElMessage.success('已冻结')
    freezeDialogVisible.value = false
    detailVisible.value = false
    getBusiness()
  } catch (e: any) {
    ElMessage.error('操作失败')
  } finally {
    auditing.value = false
  }
}

/** 查看驳回/冻结原因 */
const handleViewReason = async (row: any) => {
  const type = row.status === 5 ? 1 : 2  // 1=驳回, 2=冻结
  try {
    const reasonId = await businessApi.getReason(row.id, type) as any
    // 根据状态选择对应的原因列表
    const list = row.status === 5 ? rejectReasons : freezeReasons
    const item = list.find(r => r.value === reasonId)
    const reason = item ? item.label : '未知原因'
    ElMessageBox.alert(reason, type === 1 ? '驳回原因' : '冻结原因', {
      confirmButtonText: '知道了',
      type: type === 1 ? 'warning' : 'info',
    })
  } catch {
    ElMessage.error('获取原因失败')
  }
}


</script>