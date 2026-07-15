<template>
  <!-- 搜索区域 -->
  <div style="display: flex; align-items: center; gap: 16px;">
    <el-form inline label-width="auto">
    <el-form-item label="用户名">
      <el-input v-model="username" />
    </el-form-item>
        <el-form-item label="昵称">
      <el-input v-model="nickname" />
    </el-form-item>
    <el-form-item label="状态">
      <el-select v-model="status" placeholder="状态" style="width: 120px">
        <el-option label="启用" value="1" />
        <el-option label="禁用" value="0" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="fetchList">搜索</el-button>
      <el-button @click="cleanFrom">重置</el-button>
    </el-form-item>
  </el-form>
    <el-button type="primary" @click="add()" style="margin-left: auto;">添加</el-button>
  </div>
  <!-- 表格 -->
  <el-table :data="tableData" stripe style="width: 100%">
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="username" label="用户名" width="150" />
    <el-table-column prop="nickname" label="昵称" width="150" />
    <el-table-column prop="phone" label="电话" width="150" />
    <el-table-column prop="email" label="邮箱" width="250" />
    <el-table-column prop="role" label="角色" width="100">
      <template #default="{ row }">
        {{ row.role === 1 ? '管理员' : row.role === 2 ? '超级管理员' : '用户' }}
      </template>
    </el-table-column>
    <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
    <el-table-column prop="createdTime" label="创建时间" width="180" />
    <el-table-column prop="updatedTime" label="更新时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
      <template #default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column fixed="right" label="操作" min-width="150">
      <template #default="{ row }">
        <el-button link type="primary" size="small" @click="getColumnById(row.id)">
          查看
        </el-button>
        <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页 -->
  <el-pagination
    v-show="total > 0"
    background
    layout="total, prev, pager, next, jumper"
    :current-page="currentPage"
    :page-size="pageSize"
    :total="total"
    @current-change="onPageChange"
    style="margin-top: 20px; justify-content: center"
  />
  <!-- 查看详情弹出框 -->
  <el-dialog v-model="dialogVisible" title="管理员详情" width="500px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="ID">{{ dialogData.id }}</el-descriptions-item>
      <el-descriptions-item label="头像">
        <img :src="dialogData.avatar" alt="头像" width="150" height="150">
      </el-descriptions-item>
      <el-descriptions-item label="用户名">{{ dialogData.username }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ dialogData.nickname }}</el-descriptions-item>
      <el-descriptions-item label="电话">{{ dialogData.phone }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ dialogData.email }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ dialogData.role === 1 ? '管理员' : dialogData.role === 2 ? '超级管理员' : '用户' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="dialogData.status === 1 ? 'success' : 'danger'">
          {{ dialogData.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
  <!-- 添加管理员弹出框 -->
  <el-dialog v-model="addDialogVisible" title="添加管理员" width="400px">
    <el-form :model="addForm" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="addForm.username" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="addForm.nickname" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="addDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAdd">确认</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'

const onSubmit = () => {
  console.log('submit!')
}
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'


const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(15)
const total = ref(0)
const username = ref(null)
const nickname = ref(null)

const status = ref()
const lastLoginTime = ref()

/** 获取管理员列表 */
async function fetchList() {
  const res: any = await adminApi.list({
    page: currentPage.value,
    size: pageSize.value,
    lastLoginTime:lastLoginTime.value,
    nickname:nickname.value,
    username:username.value,
    status:status.value,
  })
  tableData.value = res.records || []
  total.value = res.total ?? 0
}
function cleanFrom() {
    username.value = null
    nickname.value = null
    status.value = null
    fetchList()
}
onMounted(() => {
  fetchList()
})

/** 切换页码 */
function onPageChange(page: number) {
  currentPage.value = page
  fetchList()
}
import { ElMessage, ElMessageBox } from 'element-plus'

const dialogVisible = ref(false)
const dialogData = ref<any>({})

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确认删除?', '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await adminApi.remove(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.info('已取消删除')
  }
}

async function getColumnById(id: number) {
  const res = await adminApi.getByID(id)
  dialogData.value = res
  dialogVisible.value = true
}
async function add() {
  addDialogVisible.value = true
}

const addDialogVisible = ref(false)
const addForm = ref({
  username: '',
  nickname: '',
})

async function submitAdd() {
  await adminApi.add(addForm.value)
  ElMessage.success('添加成功')
  addDialogVisible.value = false
  fetchList()
}
</script>