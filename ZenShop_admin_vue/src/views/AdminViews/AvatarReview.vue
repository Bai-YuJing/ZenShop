<template>
  <div class="avatar-review">
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="id" label="用户ID" />
        <el-table-column label="待审核头像" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.avatar"
              style="width: 80px; height: 80px; border-radius: 6px; cursor: pointer;"
              fit="cover"
              :preview-src-list="[row.avatar]"
              hide-on-click-modal
            >
              <template #error>
                <el-avatar shape="square" :size="80">?</el-avatar>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              :loading="auditing === row.id"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              :loading="auditing === row.id"
              @click="handleReject(row)"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api'

const loading = ref(false)
const auditing = ref<number | null>(null)
const tableData = ref<any[]>([])

async function loadData() {
  loading.value = true
  try {
    const res = (await userApi.pendingAvatar()) as any
    tableData.value = Array.isArray(res) ? res : (res?.records ?? res ?? [])
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

async function handleApprove(row: any) {
  auditing.value = row.id
  try {
    await ElMessageBox.confirm(`确定通过用户「${row.id}」的头像修改吗？`, '提示')
    await userApi.reviewAvatar(row.id, 1)
    ElMessage.success('已通过')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  } finally {
    auditing.value = null
  }
}

async function handleReject(row: any) {
  auditing.value = row.id
  try {
    await ElMessageBox.confirm(`确定驳回用户「${row.id}」的头像修改吗？`, '提示')
    await userApi.reviewAvatar(row.id, 0)
    ElMessage.success('已驳回')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  } finally {
    auditing.value = null
  }
}
</script>

<style scoped>
.avatar-review {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.table-card {
  flex: 1;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.table-card :deep(.el-table) {
  flex: 1;
}

</style>
