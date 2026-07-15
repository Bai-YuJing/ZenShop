<template>
  <div class="page">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>个人信息</h2>
      <el-button v-if="!editing" text type="primary" @click="startEdit">编辑</el-button>
      <el-button v-else text @click="cancelEdit">取消</el-button>
    </div>

    <!-- 头像 -->
    <div class="avatar-wrap">
      <el-upload
        class="avatar-uploader"
        action="/api/user/info/avatar"
        :show-file-list="false"
        :headers="uploadHeaders"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
      >
        <div class="avatar">
          <img v-if="user.avatar" :src="user.avatar" />
          <el-icon v-else :size="40"><User /></el-icon>
          <div class="avatar-overlay">
            <el-icon :size="20"><Camera /></el-icon>
          </div>
        </div>
      </el-upload>
      <span class="avatar-tip">点击更换头像</span>
    </div>

    <!-- 信息列表 -->
    <div class="info-card">
      <div class="info-row">
        <span class="info-label">用户名</span>
        <el-input
          v-if="editing"
          v-model="form.username"
          class="info-input"
          maxlength="20"
        />
        <span v-else class="info-value">{{ user.username || '未设置' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">手机号</span>
        <span class="info-value">{{ user.phone || '未绑定' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">邮箱</span>
        <el-input
          v-if="editing"
          v-model="form.email"
          class="info-input"
          placeholder="请输入邮箱"
        />
        <span v-else class="info-value">{{ user.email || '未设置' }}</span>
      </div>
    </div>

    <el-button
      v-if="editing"
      type="primary"
      class="save-btn"
      :loading="saving"
      @click="handleSave"
    >保存</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Camera } from '@element-plus/icons-vue'
import { userApi } from '@/api'

const router = useRouter()
const editing = ref(false)
const saving = ref(false)

const uploadHeaders = {
  token: localStorage.getItem('token') || '',
}

function handleAvatarSuccess() {
  ElMessage.success('头像上传成功，正在审核中')
}

function beforeAvatarUpload(rawFile: File) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像仅支持 JPG/PNG 格式！')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('文件大小不能超过 2MB！')
    return false
  }
  return true
}
const user = reactive({
  id: null as number | null,
  username: '',
  phone: '',
  email: '',
  avatar: '',
  status: 1,
})

const form = reactive({
  username: '',
  email: '',
})

async function fetchUser() {
  try {
    const res: any = await userApi.getUser()
    if (res) {
      user.id = res.id
      user.username = res.username || ''
      user.phone = res.phone || ''
      user.email = res.email || ''
      user.avatar = res.avatar || ''
      user.status = res.status ?? 1
    }
  } catch {
    // 错误已在拦截器中处理
  }
}

onMounted(fetchUser)

function startEdit() {
  form.username = user.username
  form.email = user.email
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function handleSave() {
  saving.value = true
  try {
    await userApi.updateUser({
      id: user.id,
      username: form.username,
      email: form.email,
    })
    user.username = form.username
    user.email = form.email
    ElMessage.success('保存成功')
    editing.value = false
  } catch {
    // 错误已在拦截器中处理
  } finally {
    saving.value = false
  }
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

/* 头像 */
.avatar-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 0 24px;
  background: #fff;
  margin-bottom: 12px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f2ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  position: relative;
  cursor: pointer;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-tip {
  display: block;
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-top: 10px;
}

.avatar-uploader {
  display: inline-block;
}

/* 信息列表 */
.info-card {
  background: #fff;
  margin: 0 16px;
  border-radius: 12px;
  overflow: hidden;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
  min-height: 48px;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 15px;
  color: #606266;
  flex-shrink: 0;
  margin-right: 16px;
}

.info-value {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  text-align: right;
}

.info-input {
  max-width: 200px;
}

.info-input :deep(.el-input__inner) {
  text-align: right;
  height: 36px;
  font-size: 15px;
}

.save-btn {
  display: block;
  width: calc(100% - 32px);
  margin: 24px auto 0;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
}
</style>
