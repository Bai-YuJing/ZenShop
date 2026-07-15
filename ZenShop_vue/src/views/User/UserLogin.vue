<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h2>欢迎登录</h2>
        <p class="subtitle">ZenShop 用户中心</p>
      </div>

      <el-tabs v-model="mainTab" class="main-tabs" :stretch="true">
        <el-tab-pane label="登录" name="login">
          <!-- 登录方式切换 -->
          <div class="login-mode-switch">
            <span
              :class="['mode-link', { active: loginMode === 'password' }]"
              @click="loginMode = 'password'"
            >密码登录</span>
            <span class="mode-sep">|</span>
            <span
              :class="['mode-link', { active: loginMode === 'captcha' }]"
              @click="loginMode = 'captcha'"
            >验证码登录</span>
          </div>

          <!-- 密码登录 -->
          <template v-if="loginMode === 'password'">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              label-position="top"
              size="large"
            >
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="loginForm.phone"
                  placeholder="请输入手机号"
                  :prefix-icon="Phone"
                />
              </el-form-item>

              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  :prefix-icon="Lock"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
                  登 录
                </el-button>
              </el-form-item>
            </el-form>
          </template>

          <!-- 验证码登录 -->
          <template v-else>
            <el-form
              ref="captchaFormRef"
              :model="captchaForm"
              :rules="captchaRules"
              label-position="top"
              size="large"
            >
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="captchaForm.phone"
                  placeholder="请输入手机号"
                  :prefix-icon="Phone"
                />
              </el-form-item>

              <el-form-item label="验证码" prop="captcha">
                <div class="code-row">
                  <el-input
                    v-model="captchaForm.captcha"
                    placeholder="请输入验证码"
                  />
                  <el-button
                    :disabled="codeSending"
                    class="code-btn"
                    @click="sendLoginCode"
                  >
                    {{ codeSending ? `${codeCountdown}s` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" class="submit-btn" :loading="captchaLoading" @click="handleCaptchaLogin">
                  登 录
                </el-button>
              </el-form-item>
            </el-form>
          </template>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
          size="large"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
            />
          </el-form-item>

          <el-form-item label="验证码" prop="captcha">
            <div class="code-row">
              <el-input
                v-model="registerForm.captcha"
                placeholder="请输入验证码"
              />
              <el-button
                :disabled="regCodeSending"
                class="code-btn"
                @click="sendRegCode"
              >
                {{ regCodeSending ? `${regCodeCountdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请设置密码"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" class="submit-btn" :loading="registering" @click="handleRegister">
              注 册
            </el-button>
          </el-form-item>
        </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { Phone, Lock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const mainTab = ref('login')
const loginMode = ref('password')
const loading = ref(false)
const captchaLoading = ref(false)
const registering = ref(false)

// --- 登录验证码 ---
const codeSending = ref(false)
const codeCountdown = ref(60)

// --- 注册验证码 ---
const regCodeSending = ref(false)
const regCodeCountdown = ref(60)

// --- 密码登录表单 ---
const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
  phone: '',
  password: '',
})

// --- 验证码登录表单 ---
const captchaFormRef = ref<FormInstance>()
const captchaForm = reactive({
  phone: '',
  captcha: '',
})

const validatePhone = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const loginRules: FormRules = {
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' },
  ],
}

const captchaRules: FormRules = {
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
}

async function sendLoginCode() {
  if (!/^1[3-9]\d{9}$/.test(captchaForm.phone)) {
    ElMessage.warning('请先输入正确的手机号')
    return
  }
  codeSending.value = true
  codeCountdown.value = 60
  const timer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) {
      clearInterval(timer)
      codeSending.value = false
    }
  }, 1000)
  try {
    await userApi.getCaptcha(captchaForm.phone)
    ElMessage.success('发送成功')
  } catch {
    codeSending.value = false
    clearInterval(timer)
  }
}

async function handleCaptchaLogin() {
  const valid = await captchaFormRef.value?.validate().catch(() => false)
  if (!valid) return
  captchaLoading.value = true
  try {
    const res = await userApi.login({ phone: captchaForm.phone, captcha: captchaForm.captcha }) as string
    if (res) {
      localStorage.setItem('token', res)
      ElMessage.success('登录成功')
      router.push('/user/index')
    }
  } catch {
    // 错误已在拦截器中处理
  } finally {
    captchaLoading.value = false
  }
}

async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!loginForm.password) {
    ElMessage.warning('请输入密码')
    return
  }
  loading.value = true
  try {
    const res = await userApi.login({ phone: loginForm.phone, password: loginForm.password }) as string
    if (res) {
      localStorage.setItem('token', res)
      ElMessage.success('登录成功')
      router.push('/user/index')
    }
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

// --- 注册表单 ---
const registerFormRef = ref<FormInstance>()
const registerForm = reactive({
  username: '',
  phone: '',
  captcha: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20位之间', trigger: 'blur' },
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function sendRegCode() {
  if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    ElMessage.warning('请先输入正确的手机号')
    return
  }
  regCodeSending.value = true
  regCodeCountdown.value = 60
  const timer = setInterval(() => {
    regCodeCountdown.value--
    if (regCodeCountdown.value <= 0) {
      clearInterval(timer)
      regCodeSending.value = false
    }
  }, 1000)
  try {
    await userApi.getCaptcha(registerForm.phone)
    ElMessage.success('发送成功')
  } catch {
    regCodeSending.value = false
    clearInterval(timer)
  }
}

async function handleRegister() {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return
  registering.value = true
  try {
    await userApi.register({
      username: registerForm.username,
      phone: registerForm.phone,
      captcha: registerForm.captcha,
      password: registerForm.password,
    })
    ElMessage.success('注册成功')
    mainTab.value = 'login'
    registerForm.username = ''
    registerForm.phone = ''
    registerForm.captcha = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
  } catch {
    // 错误已在拦截器中处理
  } finally {
    registering.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  box-sizing: border-box;
}

.login-card {
  width: 100%;
  max-width: 100%;
  min-height: 100vh;
  background: #fff;
  border-radius: 0;
  padding: 60px 24px 40px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  margin: 0 0 6px;
  font-size: 26px;
  color: #303133;
  font-weight: 700;
}

.login-header .subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 登录方式切换 */
.login-mode-switch {
  text-align: center;
  margin-bottom: 24px;
}

.mode-link {
  font-size: 15px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
}

.mode-link:hover,
.mode-link.active {
  color: #409eff;
  font-weight: 500;
}

.mode-sep {
  margin: 0 14px;
  color: #dcdfe6;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 17px;
  letter-spacing: 2px;
  border-radius: 24px;
  margin-top: 8px;
}

.code-row {
  display: flex;
  gap: 10px;
}

.code-btn {
  width: 110px;
  flex-shrink: 0;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-input__inner) {
  height: 46px;
  font-size: 15px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  padding-bottom: 6px;
}

.main-tabs :deep(.el-tabs__item) {
  font-size: 17px;
  font-weight: 500;
  height: 48px;
  line-height: 48px;
}

.main-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 2px;
}
</style>
