// src/main.js
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// ===== 引入 Element Plus =====
// Element Plus 依赖尚未安装，已暂时移除。
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)  // ← 使用 Element Plus

app.mount('#app')