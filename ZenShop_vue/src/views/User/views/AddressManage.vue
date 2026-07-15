<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>地址管理</h2>
      <el-button v-if="!showForm" text :type="list.length >= 20 ? 'info' : 'primary'" :disabled="list.length >= 20" @click="handleAdd">+ 添加</el-button>
      <el-button v-else text @click="cancelForm">取消</el-button>
    </div>

    <!-- 地址列表 -->
    <template v-if="!showForm">
      <div class="list-wrap">
        <div v-for="item in list" :key="item.id" class="address-card">
          <div class="address-info">
            <div class="address-top">
              <span class="address-name">{{ item.name }}</span>
              <span class="address-phone">{{ item.phone }}</span>
              <el-tag v-if="item.isDefault" size="small" type="primary" class="default-tag">默认</el-tag>
            </div>
            <p class="address-detail">{{ item.regionText }} {{ item.detail }}</p>
          </div>
          <div class="address-actions">
            <el-button text size="small" @click="handleEdit(item)">编辑</el-button>
            <el-button text size="small" type="danger" @click="handleDelete(item)">删除</el-button>
            <el-button v-if="!item.isDefault" text size="small" type="primary" @click="setDefault(item)">设为默认</el-button>
          </div>
        </div>
        <p v-if="list.length === 0" class="empty-text">暂无地址</p>
      </div>
    </template>

    <!-- 添加/编辑表单 -->
    <template v-else>
      <div class="form-card">
        <div class="form-title">{{ isEditing ? '编辑地址' : '添加地址' }}</div>
        <el-form label-position="top" size="large">
          <el-form-item label="收货人">
            <el-input v-model="form.name" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="所在地区">
            <div class="region-row">
              <el-select v-model="form.provinceId" placeholder="省" class="region-select" @change="onProvinceChange">
                <el-option v-for="p in regionData" :key="p.value" :label="p.label" :value="p.value" />
              </el-select>
              <el-select v-model="form.cityId" placeholder="市" class="region-select" @change="onCityChange">
                <el-option v-for="c in cityList" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
              <el-select v-model="form.districtId" placeholder="区" class="region-select">
                <el-option v-for="d in districtList" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </div>
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="form.detail" placeholder="街道、门牌号等" />
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
          </el-form-item>
        </el-form>
        <el-button type="primary" class="save-btn" @click="handleSave">保存</el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { regionData } from '@/data/region-data'
import { userAddressApi } from '@/api'

const router = useRouter()
const showForm = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)

interface Address {
  id: number
  name: string
  phone: string
  provinceId: string
  cityId: string
  districtId: string
  detail: string
  isDefault: boolean
  regionText: string
}

const list = ref<Address[]>([])

async function fetchList() {
  try {
    const res: any = await userAddressApi.getList()
    const vos: any[] = res?.userAddressVos || []
    const defaultId = res?.defaultId
    list.value = vos.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      phone: item.phone || '',
      provinceId: String(item.provinceId || ''),
      cityId: String(item.cityId || ''),
      districtId: String(item.districtId || ''),
      detail: item.detailedAddress || '',
      isDefault: item.id === defaultId,
      regionText: '',
    }))
    // 补全地区文字
    list.value.forEach(item => {
      item.regionText = getRegionText(item.provinceId, item.cityId, item.districtId)
    })
  } catch {
    list.value = []
  }
}

onMounted(() => {
  fetchList()
})

const form = ref({
  name: '',
  phone: '',
  provinceId: '',
  cityId: '',
  districtId: '',
  detail: '',
  isDefault: false,
})

const cityList = computed(() => {
  if (!form.value.provinceId) return []
  const p = regionData.find(item => item.value === form.value.provinceId)
  return p?.children || []
})

const districtList = computed(() => {
  if (!form.value.cityId) return []
  const c = cityList.value.find(item => item.value === form.value.cityId)
  return c?.children || []
})

function getRegionText(provinceId: string, cityId: string, districtId: string) {
  const p = regionData.find(r => r.value === provinceId)
  const c = p?.children?.find(r => r.value === cityId)
  const d = c?.children?.find(r => r.value === districtId)
  return [p?.label, c?.label, d?.label].filter(Boolean).join(' / ')
}

function handleAdd() {
  isEditing.value = false
  editingId.value = null
  form.value = { name: '', phone: '', provinceId: '', cityId: '', districtId: '', detail: '', isDefault: false }
  showForm.value = true
}

function handleEdit(item: Address) {
  isEditing.value = true
  editingId.value = item.id
  form.value = {
    name: item.name,
    phone: item.phone,
    provinceId: item.provinceId,
    cityId: item.cityId,
    districtId: item.districtId,
    detail: item.detail,
    isDefault: item.isDefault,
  }
  showForm.value = true
}

function cancelForm() {
  showForm.value = false
}

function handleDelete(item: Address) {
  ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' }).then(async () => {
    try {
      await userAddressApi.remove(item.id)
      ElMessage.success('删除成功')
      await fetchList()
    } catch { /* ignore */ }
  }).catch(() => {})
}

async function setDefault(item: Address) {
  try {
    await userAddressApi.setDefault(item.id)
    ElMessage.success('已设为默认地址')
    await fetchList()
  } catch { /* ignore */ }
}

async function handleSave() {
  if (!form.value.name || !form.value.phone || !form.value.provinceId || !form.value.cityId || !form.value.districtId || !form.value.detail) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const dto: any = {
      name: form.value.name,
      phone: form.value.phone,
      provinceId: Number(form.value.provinceId),
      cityId: Number(form.value.cityId),
      districtId: Number(form.value.districtId),
      detailedAddress: form.value.detail,
      isDefault: form.value.isDefault ? 1 : 0,
    }
    if (isEditing.value && editingId.value != null) {
      dto.id = editingId.value
      await userAddressApi.update(dto)
      ElMessage.success('修改成功')
    } else {
      await userAddressApi.add(dto)
      ElMessage.success('添加成功')
    }
    showForm.value = false
    await fetchList()
  } catch {
    // 错误已在拦截器中处理
  }
}

function onProvinceChange() {
  form.value.cityId = ''
  form.value.districtId = ''
}

function onCityChange() {
  form.value.districtId = ''
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
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.list-wrap {
  padding: 12px 16px;
}

.address-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.address-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.address-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.address-phone {
  font-size: 13px;
  color: #909399;
}

.default-tag {
  font-size: 11px;
}

.address-detail {
  font-size: 13px;
  color: #606266;
  margin: 0;
  line-height: 1.4;
}

.address-actions {
  display: flex;
  gap: 4px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.empty-text {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 60px;
}

/* 表单 */
.form-card {
  background: #fff;
  margin: 12px 16px;
  border-radius: 12px;
  padding: 24px 20px;
}

.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.region-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.region-select {
  flex: 1;
}

.region-select :deep(.el-select__wrapper) {
  width: 100%;
}

.region-select :deep(.el-select--large) {
  width: 100%;
}

.save-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
  margin-top: 8px;
}
</style>
