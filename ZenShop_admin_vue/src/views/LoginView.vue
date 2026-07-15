<script setup lang="ts">

import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// @ts-ignore
import { adminApi } from '@/api'

/** 假设后端返回格式：{ id: string, imageBase64: string } */
interface CaptchaResult {
  id: string
  imageBase64: string
}

const username = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaId = ref('')
const captchaImage = ref('')
const errMessage = ref('')

/** 表单校验信息 */
const errors = reactive({
  username: '',
  password: '',
  captcha: '',
})

/** 清除指定字段的错误 */
function clearError(field: keyof typeof errors) {
  errors[field] = ''
}

/** 校验用户名 */
function validateUsername() {
  if (!username.value.trim()) {
    errors.username = '请输入用户名'
    return false
  }
  errors.username = ''
  return true
}

/** 校验密码 */
function validatePassword() {
  if (!password.value) {
    errors.password = '请输入密码'
    return false
  }
  if (password.value.length < 6) {
    errors.password = '密码长度不能少于6位'
    return false
  }
  errors.password = ''
  return true
}

/** 校验验证码 */
function validateCaptcha() {
  if (!captchaCode.value.trim()) {
    errors.captcha = '请输入验证码'
    return false
  }
  if (captchaCode.value.trim().length !== 4) {
    errors.captcha = '验证码为4位'
    return false
  }
  errors.captcha = ''
  return true
}

/** 校验所有字段，返回是否通过 */
function validate(): boolean {
  const v1 = validateUsername()
  const v2 = validatePassword()
  const v3 = validateCaptcha()
  return v1 && v2 && v3
}

/** 从后端获取验证码（base64） */
async function refreshCaptcha() {
  const res: any = await adminApi.getcaptcha();
  if(res){
    captchaImage.value=res.captchaImage;
    captchaId.value=res.captchaKey;
    // console.log(res.captchaImage);
  }
}

/** 登录提交 */
const router = useRouter()
async function handleSubmit() {

  if (!validate()) return

  const login={
    username:username.value,
    password:password.value,
    captcha:captchaCode.value,
    captchaKey:captchaId.value
  }
  const res: any = await adminApi.login(login)
  if(res){
    localStorage.setItem('token', res)
    router.push('/admin')
  }
}

onMounted(refreshCaptcha)
</script>

<template>
  <div class="login-wrapper">
    <div class="login-card">
      <!-- Logo / 标题 -->
      <div class="login-header">
        <div class="logo-icon">ZS</div>
        <h1 class="title">ZenShop 管理后台</h1>
        <p class="subtitle">请登录您的账号</p>
      </div>


      <!-- 登录表单 -->
      <form class="login-form" @submit.prevent="handleSubmit">
        <!-- 用户名 -->
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper" :class="{ 'input-error': errors.username }">
            <span class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
            </span>
            <input
              id="username"
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
              @input="clearError('username')"
              @blur="validateUsername"
            />
          </div>
          <p v-if="errors.username" class="error-text">{{ errors.username }}</p>
        </div>

        <!-- 密码 -->
        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper" :class="{ 'input-error': errors.password }">
            <span class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </span>
            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
              @input="clearError('password')"
              @blur="validatePassword"
            />
          </div>
          <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        </div>

        <!-- 验证码 -->
        <div class="form-group">
          <label for="captcha">验证码</label>
          <div class="captcha-row">
            <div class="input-wrapper captcha-input-wrapper" :class="{ 'input-error': errors.captcha }">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                  <line x1="3" y1="9" x2="21" y2="9" />
                  <line x1="9" y1="3" x2="9" y2="21" />
                </svg>
              </span>
              <input
                id="captcha"
                v-model="captchaCode"
                type="text"
                placeholder="验证码"
                maxlength="4"
                autocomplete="off"
                @input="clearError('captcha')"
                @blur="validateCaptcha"
              />
            </div>
            <img
              :src="captchaImage"
              alt="验证码"
              class="captcha-img"
              width="150"
              height="45"
              @click="refreshCaptcha"
            />
          </div>
          <p v-if="errors.captcha" class="error-text">{{ errors.captcha }}</p>
          <p v-else class="captcha-hint">点击图片刷新验证码</p>
        </div>

        <!-- 登录按钮 -->
        <button type="submit" class="login-btn">登 录</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.login-card {
  width: 460px;
  padding: 48px 40px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  margin-bottom: 16px;
}

.title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #888;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #444;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  height: 44px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background: #f7f8fc;
  transition: border-color 0.25s, box-shadow 0.25s;
}

.input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
  background: #fff;
}

.input-icon {
  display: flex;
  align-items: center;
  color: #999;
  flex-shrink: 0;
}

.input-wrapper:focus-within .input-icon {
  color: #667eea;
}

.input-wrapper input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #333;
  height: 100%;
}

.input-wrapper input::placeholder {
  color: #bbb;
}

/* 验证码行 */
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.captcha-input-wrapper {
  flex: 1;
}

.captcha-img {
  width: 150px;
  height: 45px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #e0e0e0;
  background: #fff;
  object-fit: contain;
  flex-shrink: 0;
  transition: opacity 0.2s;
  box-sizing: border-box;
}

.captcha-img:hover {
  opacity: 0.85;
}

.captcha-hint {
  margin: 4px 0 0;
  font-size: 12px;
  color: #aaa;
}

/* 错误状态 */
.input-error {
  border-color: #e74c3c !important;
  background: #fff !important;
}

.input-error:focus-within {
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.15) !important;
}

.error-text {
  margin: 2px 0 0;
  font-size: 12px;
  color: #e74c3c;
  line-height: 1.4;
}

/* 登录按钮 */
.login-btn {
  height: 48px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  cursor: pointer;
  transition: opacity 0.25s, transform 0.15s;
  margin-top: 4px;
}

.login-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.login-btn:active {
  transform: translateY(0);
}
</style>
