<script setup lang="ts">
import { inject, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

import type { UploadProps } from 'element-plus'
import { adminApi } from '@/api'

const user = inject<any>('userInfo', {})
const loading = ref(false)
const imageUrl = ref('')
const token = localStorage.getItem('token')

const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!)
  console.log(response.data)
  user.value.avatar=response.data
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('头像格式仅支持 JPG、PNG、WEBP！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}
async function upload() {
  if (!user.value.nickname?.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  loading.value = true
  try {
    console.log(user.value)
    const res = await adminApi.update(user.value)
    if (res?.data) {
      user.value = Object.assign(user.value, res.data)
    }
    ElMessage.success('保存成功')
  } catch (e) {
    console.error('更新失败', e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
    <el-form label-width="70px">
      <div class="profile-top">
        <h2 v-if="user.role==1">管理员</h2>
        <h2 v-if="user.role==2">超级管理员</h2>
        <el-tag type="info" effect="plain" size="small" round>ID: {{ user.id }}</el-tag>
      </div>
  <div class="avatar-section">
    <div class="demo-image__preview">
      <el-image
        style="width: 178px; height: 178px"
        :src="user.avatar"
        :zoom-rate="1.2"
        :preview-src-list="[user.avatar]"
        :max-scale="7"
        :min-scale="0.2"
        show-progress
        :initial-index="4"
        fit="cover"
      />
    </div>
    <el-upload
      class="avatar-uploader"
      action="/api/admin/uploadAvatar"
      :headers="{ token }"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeAvatarUpload"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>
  </div>
    <el-form-item label="用户名">
      <el-input v-model="user.username" disabled/>
    </el-form-item>
    <el-form-item label="昵称">
      <el-input v-model="user.nickname" placeholder="请输入昵称" />
    </el-form-item>
        <el-form-item label="手机">
      <el-input v-model="user.phone" placeholder="请输入手机号" />
    </el-form-item>
        <el-form-item label="邮箱">
      <el-input v-model="user.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" :loading="loading" @click="upload()">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 20px;
}
.demo-image__error .image-slot {
  font-size: 30px;
}
.demo-image__error .image-slot .el-icon {
  font-size: 30px;
}
.demo-image__error .el-image {
  width: 100%;
  height: 200px;
}
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.el-form {
  max-width: 500px;
}
.el-form .el-input {
  width: 320px;
}
.admin-info {
  max-width: 800px;
  margin: 0 auto;
}
.info-card {
  border-radius: 8px;
}
.profile-top {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px 0;
}
.profile-top h2 {
  margin: 0;
  font-size: 22px;
}
.text-muted {
  margin: 4px 0 0;
  color: #909399;
  font-size: 14px;
}
</style>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>