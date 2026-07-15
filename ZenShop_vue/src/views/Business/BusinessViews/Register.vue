<template>
  <div class="register-wrapper">
    <div class="register-card">
      <div class="card-header">
        <h1>提交审核</h1>
        <p class="subtitle">请填写以下信息完成商家入驻审核</p>
      </div>

      <el-form
        v-if="store.registerData"
        :model="store.registerData"
        label-width="100px"
        class="register-form"
      >
        <el-form-item label="商家名称">
          <el-input v-model="store.registerData.name" placeholder="请输入商家名称" disabled />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="store.registerData.phone" placeholder="请输入手机号" disabled />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="store.registerData.email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-form-item label="经营分类" prop="category">
          <el-select
            v-model="store.registerData.categoryId"
            placeholder="请选择经营分类"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="store.registerData.region"
            :options="regionData"
            placeholder="请选择省/市/区"
            style="width: 100%"
            clearable
          />
        </el-form-item>

        <el-form-item label="详细地址">
          <el-input v-model="store.registerData.detailedAddress" placeholder="请输入详细地址（街道、门牌号等）" />
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

        <el-form-item label="详细信息">
          <el-input
            v-model="store.registerData.description"
            type="textarea"
            :rows="4"
            placeholder="请填写商家详细介绍"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="submit-btn" @click="send">提交审核</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useBusinessStore } from '@/stores/business'
import { businessShopApi, businessApi } from '@/api'
import { regionData } from '@/data/region-data'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'

const imageUrl = ref('')

// 上传携带 token
const uploadHeaders = {
  authentication: localStorage.getItem('authentication') || '',
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  if (!store.registerData) {
    ElMessage.error('商家注册信息不存在')
    return
  }
  store.registerData.logo = response.data
  imageUrl.value = response.data
  console.log(store.registerData.logo)
  ElMessage.success("上传成功")
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('logo仅支持 JPG/PNG 格式！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('文件大小不能超过 2MB！')
    return false
  }
  return true
}

type CategoryItem = { id: number; name: string }
const categoryList = ref<CategoryItem[]>([])

async function getCategory() {
  const res = (await businessApi.getCategory()) as CategoryItem[]
  if (res) {
    categoryList.value = res
  }
}

onMounted(() => {
  getCategory()
})

const router = useRouter()
const store = useBusinessStore()

onMounted(() => {
  if (!store.registerData) {
    ElMessage.error('未知错误')
    return
  }
  // 将省市区ID（数字）转为级联选择器所需的字符串数组
  const region: string[] = []
  if (store.registerData.provinceId) region.push(String(store.registerData.provinceId))
  if (store.registerData.cityId) region.push(String(store.registerData.cityId))
  if (store.registerData.districtId) region.push(String(store.registerData.districtId))
  store.registerData.region = region
  console.log('registerData:', store.registerData)
})
async function send() {
  if (!store.registerData) {
    ElMessage.error('商家注册信息不存在')
    return
  }

  // 表单基本校验
  if (!store.registerData.name) {
    ElMessage.warning('请填写商家名称')
    return
  }
  if (!store.registerData.phone) {
    ElMessage.warning('请填写手机号')
    return
  }

  // 从 cascader 的 region 数组中解出省市区ID
  const region: string[] = store.registerData.region || []
  const [provinceId, cityId, districtId] = region

  if (!provinceId || !cityId || !districtId) {
    ElMessage.warning('请选择完整的省/市/区')
    return
  }

  // 组装提交参数：UI 数组 → 后端独立字段
  const payload = {
    ...store.registerData,
    provinceId: Number(provinceId),
    cityId: Number(cityId),
    districtId: Number(districtId),
    region: undefined, // 清除 UI 数组字段
  }

  try {
    await businessShopApi.register(payload)
    // console.log(payload)
    ElMessage.success('提交审核成功')
    router.push('/business/center') // 跳转到审核状态页
  } catch (e) {
    // 错误已在请求拦截器中统一处理
  }
}
</script>

<style scoped>
.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  width: 100%;
  padding: 40px 20px;
  box-sizing: border-box;
  background: linear-gradient(135deg, #e8f0fe 0%, #f0e6ff 50%, #fce4ec 100%);
}

.register-card {
  width: 100%;
  max-width: 720px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 40px 48px;
  box-sizing: border-box;
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

.register-form {
  width: 100%;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
}

.upload-tip {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.4;
}
</style>

<style scoped>
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
