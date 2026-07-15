import { ref } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'business_register_data'

export const useBusinessStore = defineStore('business', () => {
  // 页面刷新后从 sessionStorage 恢复，关闭标签页自动清除
  const saved = sessionStorage.getItem(STORAGE_KEY)
  const registerData = ref<Record<string, any> | null>(saved ? JSON.parse(saved) : null)

  function setRegisterData(data: Record<string, any>) {
    registerData.value = data
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  }

  function clearRegisterData() {
    registerData.value = null
    sessionStorage.removeItem(STORAGE_KEY)
  }

  return { registerData, setRegisterData, clearRegisterData }
})
