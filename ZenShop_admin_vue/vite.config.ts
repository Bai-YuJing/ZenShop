import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '127.0.0.1',
    port: 2001,
    // ========== 新增代理配置 ==========
    proxy: {
      '/api': {  // 匹配所有以/api开头的请求
        target: 'http://localhost:8888',  // 比如 http://localhost:8080
        changeOrigin: true,  // 必须开启
        rewrite: (path) => path.replace(/^\/api/, '')  // 如果后端没有/api前缀就启用这行
      }
    }
  },
})