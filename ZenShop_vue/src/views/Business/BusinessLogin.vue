<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="../../assets/logo.svg" alt="logo" class="logo" />
        <h2>商家管理平台</h2>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs" :stretch="true">
        <!-- 登录 -->
        <el-tab-pane label="登录" name="login">
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

            <el-form-item label="验证码" prop="captcha">
              <div class="code-row">
                <el-input
                  v-model="loginForm.captcha"
                  placeholder="请输入验证码"
                />
                <el-button
                  :disabled="loginCodeSending"
                  class="code-btn"
                  @click="sendLoginCode"
                >
                  {{ loginCodeSending ? `${loginCodeCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleLogin">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册 -->
        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
            size="large"
          >
            <el-form-item label="商家名称" prop="shopName">
              <el-input
                v-model="registerForm.shopName"
                placeholder="请输入商家名称"
                :prefix-icon="Shop"
              />
            </el-form-item>

            <el-form-item label="经营分类" prop="category">
              <el-select
                v-model="registerForm.category"
                placeholder="请选择经营分类"
                style="width: 100%"
              >
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="所在地区" prop="region">
              <el-cascader
                v-model="registerForm.region"
                :options="regionData"
                placeholder="请选择省/市/区"
                style="width: 100%"
                clearable
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
                  :disabled="codeSending"
                  class="code-btn"
                  @click="sendCode"
                >
                  {{ codeSending ? `${codeCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleRegister">
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
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { Phone, Shop } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { regionData } from '@/data/region-data'
import {businessApi} from "@/api"
import router from '@/router'

const activeTab = ref('login')
const codeSending = ref(false)
const codeCountdown = ref(60)
const loginCodeSending = ref(false)
const loginCodeCountdown = ref(60)

// --- 登录表单 ---
const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
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
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位字母或数字', trigger: 'blur' },
    {
      pattern: /^[A-Za-z\d]{6}$/,
      message: '验证码只能包含字母和数字',
      trigger: 'blur',
    },
  ],
}

const sendLoginCode = () => {
  if (!/^1[3-9]\d{9}$/.test(loginForm.phone)) {
    ElMessage.warning('请先输入正确的手机号')
    return
  }
  loginCodeSending.value = true
  loginCodeCountdown.value = 60
  const timer = setInterval(() => {
    loginCodeCountdown.value--
    if (loginCodeCountdown.value <= 0) {
      clearInterval(timer)
      loginCodeSending.value = false
    }
  }, 1000)
    businessApi.getcaptcha(loginForm.phone)
    ElMessage.success("发送成功")
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  const res=await businessApi.login(loginForm) as string
  if(res){
    localStorage.setItem('authentication', res)
    location.href = '/business/center'
  }
}

// --- 注册表单 ---
const registerFormRef = ref<FormInstance>()
interface CategoryOption {
  value: number
  label: string
}
const categoryOptions = ref<CategoryOption[]>([])

async function getCategory() {
  type CategoryItem = { id: number; name: string }
  const res = await businessApi.getCategory() as CategoryItem[]
  if (res) {
    categoryOptions.value = res.map(item => ({ value: item.id, label: item.name }))
  }
}

onMounted(() => {
  getCategory()
})

const registerForm = reactive({
  shopName: '',
  phone: '',
  captcha: '',
  category: '',
  region: [],
})

const registerRules: FormRules = {
  shopName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { max: 50, message: '商家名称不超过50字', trigger: 'blur' },
  ],
  category: [
    { required: true, message: '请选择经营分类', trigger: 'change' },
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' },
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位字母或数字', trigger: 'blur' },
    {
      pattern: /^[A-Za-z\d]{6}$/,
      message: '验证码只能包含字母和数字',
      trigger: 'blur',
    },
  ],
}

async function sendCode()  {
  if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
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
  const res=await businessApi.getcaptcha(registerForm.phone)
  if(res){
    ElMessage.success("发送成功")
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const [province_id, city_id, district_id] = registerForm.region as string[]
  const register={
    name:registerForm.shopName,
    categoryId:registerForm.category,
    phone:registerForm.phone,
    captcha:registerForm.captcha,
    provinceId:province_id,
    cityId:city_id,
    districtId:district_id
  }
  const res=await businessApi.register(register) as string
  if(res){
   localStorage.setItem('authentication', res)
    router.push("/business/register")
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header .logo {
  width: 60px;
  height: 60px;
  margin-bottom: 10px;
}

.login-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}

.login-tabs {
  --el-tabs-header-height: 44px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
}

.code-row {
  display: flex;
  gap: 12px;
}

.code-btn {
  width: 120px;
  flex-shrink: 0;
}
</style>