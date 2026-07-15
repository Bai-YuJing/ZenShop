<template>
  <div class="edit-profile-page">
    <div class="page-header">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2>编辑资料</h2>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="form" class="edit-body">
      <div class="edit-card">
        <div class="card-header">
          <h1>编辑资料</h1>
          <p class="subtitle">修改商家信息后保存</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="edit-form"
        >
          <el-form-item label="商家名称">
            <el-input v-model="form.name" disabled />
          </el-form-item>

          <el-form-item label="手机号">
            <el-input v-model="form.phone" disabled />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱地址" />
          </el-form-item>

          <el-form-item label="经营分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择经营分类" style="width: 100%">
              <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="所在地区" prop="region">
            <el-cascader
              v-model="form.region"
              :options="regionData"
              placeholder="请选择省/市/区"
              style="width: 100%"
              clearable
            />
          </el-form-item>

          <el-form-item label="详细地址" prop="detailedAddress">
            <el-input v-model="form.detailedAddress" placeholder="请输入详细地址（街道、门牌号等）" />
          </el-form-item>

          <el-form-item label="商家logo">
            <el-upload
              class="avatar-uploader"
              action="/api/business/shop/logo"
              :show-file-list="false"
              :headers="uploadHeaders"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <span class="upload-tip">支持 JPG/PNG 格式，文件不超过 2MB</span>
          </el-form-item>

          <el-form-item label="店铺描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请填写商家详细介绍" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" class="submit-btn" :loading="saving" @click="handleSave">
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { businessShopApi, businessApi } from '@/api'
import type { ShopStatus } from '@/api'
import { regionData } from '@/data/region-data'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(true)
const saving = ref(false)

interface EditForm {
  name: string
  phone: string
  email: string
  categoryId: number | null
  region: string[]
  detailedAddress: string
  description: string
  id: string
  logo: string
}

const form = ref<EditForm | null>(null)
const categoryList = ref<{ id: number; name: string }[]>([])
const imageUrl = ref('')

const uploadHeaders = {
  authentication: localStorage.getItem('authentication') || '',
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  const url = response.data
  imageUrl.value = url
  if (form.value) form.value.logo = url
  ElMessage.success('上传成功')
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('logo仅支持 JPG/PNG 格式！')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('文件大小不能超过 2MB！')
    return false
  }
  return true
}

const rules: FormRules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' },
  ],
}

function goBack() {
  router.push('/business/center/profile')
}

onMounted(async () => {
  try {
    const [shopRes, cats] = await Promise.all([
      businessShopApi.getShop(),
      businessApi.getCategory() as Promise<{ id: number; name: string }[]>,
    ])
    categoryList.value = cats || []
    const s = shopRes as any as ShopStatus
    const region: string[] = []
    if (s.provinceId) region.push(String(s.provinceId))
    if (s.cityId) region.push(String(s.cityId))
    if (s.districtId) region.push(String(s.districtId))
    imageUrl.value = s.logo || ''
    form.value = {
      id: s.id,
      name: s.name || '',
      phone: s.phone,
      email: s.email || '',
      categoryId: s.categoryId,
      region,
      detailedAddress: s.detailedAddress || '',
      description: s.description || '',
      logo: s.logo || '',
    }
  } catch {
    ElMessage.error('获取商家信息失败')
    goBack()
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid || !form.value) return

  saving.value = true
  try {
    const [provinceId, cityId, districtId] = form.value.region || []
    await businessShopApi.updateBusiness({
      id: form.value.id,
      name: form.value.name,
      phone: form.value.phone,
      email: form.value.email,
      categoryId: form.value.categoryId,
      logo: imageUrl.value,
      provinceId: Number(provinceId),
      cityId: Number(cityId),
      districtId: Number(districtId),
      detailedAddress: form.value.detailedAddress,
      description: form.value.description,
    })
    ElMessage.success('保存成功')
    router.push('/business/center/profile')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.edit-profile-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.page-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.loading-wrap {
  padding: 40px;
  max-width: 720px;
  margin: 24px auto;
  width: 100%;
  box-sizing: border-box;
}

.edit-body {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 24px 20px;
  box-sizing: border-box;
  overflow-y: auto;
}

.edit-card {
  width: 100%;
  max-width: 720px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 40px 48px;
  box-sizing: border-box;
  height: fit-content;
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-header h1 {
  font-size: 26px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 8px;
}

.card-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.edit-form {
  width: 100%;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.25s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
