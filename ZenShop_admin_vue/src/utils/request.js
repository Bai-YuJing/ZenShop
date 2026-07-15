// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'  // 如果你用了 Element Plus

// ============ 1. 创建实例 ============
const service = axios.create({
  baseURL: import.meta.env.DEV ? '/api' : 'https://你的生产域名.com',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// ============ 2. 请求拦截器 ============
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.token = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ============ 3. 响应拦截器 ============
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端约定：res.code === 1 表示业务成功，返回真实数据
    if (res.code === 1) {
      return res.data
    }
    // 业务失败（code !== 1），显示后端返回的消息并拒绝
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // HTTP 错误
    console.error('网络错误:', error)
    let errorMsg = '网络错误'
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 400: errorMsg = '请求参数错误'; break
        case 401:
          errorMsg = '未授权，请重新登录'
          localStorage.removeItem('token')
          break
        case 403: errorMsg = '拒绝访问'; break
        case 404: errorMsg = '请求资源不存在'; break
        case 500: errorMsg = '服务器内部错误'; break
        default: errorMsg = `请求失败 (${status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      errorMsg = '请求超时，请重试'
    } else if (error.message === 'Network Error') {
      errorMsg = '网络异常，请检查网络连接'
    }
    ElMessage.error(errorMsg)
    return Promise.reject(new Error(errorMsg))
  }
)

// ============ 4. 二次封装 5 种请求方法 ============
const request = {
  get(url, params = {}, config = {}) {
    return service({
      method: 'get',
      url,
      params,
      ...config
    })
  },
  post(url, data = {}, config = {}) {
    return service({
      method: 'post',
      url,
      data,
      ...config
    })
  },
  put(url, data = {}, config = {}) {
    return service({
      method: 'put',
      url,
      data,
      ...config
    })
  },
  patch(url, data = {}, config = {}) {
    return service({
      method: 'patch',
      url,
      data,
      ...config
    })
  },
  delete(url, params = {}, config = {}) {
    return service({
      method: 'delete',
      url,
      params,
      ...config
    })
  }
}

export default request