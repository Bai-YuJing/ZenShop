<template>
  <div class="product-page">
    <!-- 面包屑 -->
    <div class="breadcrumb-bar">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/business/center/profile' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>商品管理</h2>
        <span class="header-count">共 {{ total }} 个商品</span>
      </div>
      <div class="header-right">
        <el-select
          v-model="categoryFilter"
          placeholder="全部分类"
          clearable
          class="category-select"
          @change="handleSearch"
          @clear="handleSearch"
        >
          <el-option
            v-for="item in categoryList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="statusFilter"
          placeholder="全部状态"
          clearable
          class="status-select"
          @change="handleSearch"
          @clear="handleSearch"
        >
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-input
          v-model="searchText"
          placeholder="搜索商品名称"
          clearable
          class="search-input"
          @input="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 添加商品
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-card">
      <el-table
        :data="productList"
        style="width: 100%"
        stripe
        header-cell-class-name="table-header"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="商品图片" width="80" align="center">
          <template #default="{ row }">
            <div class="product-thumb">
              <img v-if="row.images && row.images.length > 0" :src="row.images[0]" />
              <span v-else class="img-placeholder">图</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" align="center" />
        <el-table-column prop="description" label="描述" min-width="140" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="160" align="center">
          <template #default="{ row }">
            <el-rate :model-value="row.rating ?? 0" disabled :max="5" />
            <span class="score-text">{{ row.rating ?? '-' }}分</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" sortable />
        <el-table-column prop="sales" label="销量" width="80" align="center" sortable />
        <el-table-column prop="updatedTime" label="更新时间" width="160" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button text type="primary" @click="viewReviews(row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && productList.length === 0" description="暂无商品数据" />

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          background
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑商品' : '添加商品'"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form label-width="90px" class="dialog-form">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input-number v-model="form.price" :precision="2" :min="0.01" :max="99999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input-number v-model="form.stock" :min="0" :max="99999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品图片">
          <div class="upload-wrapper">
            <div class="image-list">
              <div v-for="(url, idx) in imageList" :key="idx" class="image-item">
                <img :src="url" class="upload-preview" />
                <el-button class="image-remove" circle size="small" type="danger" @click="removeImage(idx)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
              <el-upload
                v-if="imageList.length < 6"
                class="product-uploader"
                action="/api/business/shop/product/img"
                :show-file-list="false"
                multiple
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :before-upload="beforeUpload"
              >
                <div class="upload-placeholder">
                  <el-icon :size="24"><Plus /></el-icon>
                </div>
              </el-upload>
            </div>
            <span class="upload-tip">支持 JPG/PNG 格式，文件不超过 2MB，最多上传 6 张</span>
          </div>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Close } from '@element-plus/icons-vue'
import { businessCategoryApi, businessProductApi } from '@/api'

const searchText = ref('')
const categoryFilter = ref<number | null>(null)
const statusFilter = ref<number | null>(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const categoryList = ref<{ id: number; name: string }[]>([])
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const loading = ref(false)
const submitting = ref(false)
const imageList = ref<string[]>([])
const productList = ref<any[]>([])

const stockSort = ref('')
const salesSort = ref('')

const uploadHeaders = {
  authentication: localStorage.getItem('authentication') || '',
}

const form = ref({
  name: '',
  categoryId: null as number | null,
  price: 0,
  stock: 0,
  image: '',
  description: '',
})

async function fetchList() {
  loading.value = true
  try {
    const params: any = {
      page: page.value,
      size: pageSize.value,
    }
    if (searchText.value) params.name = searchText.value
    if (categoryFilter.value != null) params.categoryId = categoryFilter.value
    if (statusFilter.value != null) params.status = statusFilter.value
    if (stockSort.value) params.stockSort = stockSort.value
    if (salesSort.value) params.salesSort = salesSort.value

    const res: any = await businessProductApi.page(params)
    productList.value = res.records || res.list || []
    total.value = res.total || 0
  } catch {
    productList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchList()
}

function handleSortChange(sort: { prop: string; order: string }) {
  const v = sort.order === 'ascending' ? 'asc' : sort.order === 'descending' ? 'desc' : ''
  stockSort.value = sort.prop === 'stock' ? v : ''
  salesSort.value = sort.prop === 'sales' ? v : ''
  page.value = 1
  fetchList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  page.value = 1
  fetchList()
}

function handleCurrentChange(p: number) {
  page.value = p
  fetchList()
}

onMounted(async () => {
  try {
    const res = await businessCategoryApi.getList()
    categoryList.value = (res || []) as any
  } catch { /* ignore */ }
  fetchList()
})

function handleAdd() {
  isEditing.value = false
  editingId.value = null
  imageList.value = []
  form.value = { name: '', categoryId: null, price: 0, stock: 0, image: '', description: '' }
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEditing.value = true
  editingId.value = row.id
  imageList.value = row.images ? [...row.images] : []
  form.value = {
    name: row.name,
    categoryId: row.categoryId,
    price: row.price,
    stock: row.stock,
    image: '',
    description: row.description || '',
  }
  dialogVisible.value = true
}

const router = useRouter()

function viewReviews(row: any) {
  router.push(`/business/center/review?id=${row.id}`)
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await businessProductApi.updateStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  } catch {
    // 错误已在拦截器中处理
  }
}

function handleUploadSuccess(response: any) {
  const url = response.data
  imageList.value.push(url)
  ElMessage.success('上传成功')
}

function removeImage(idx: number) {
  imageList.value.splice(idx, 1)
}

function beforeUpload(rawFile: File) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片仅支持 JPG/PNG 格式！')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('文件大小不能超过 2MB！')
    return false
  }
  return true
}

async function handleSubmit() {
  if (!form.value.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  submitting.value = true
  try {
    if (isEditing.value && editingId.value != null) {
      await businessProductApi.update({
        id: editingId.value,
        name: form.value.name,
        categoryId: form.value.categoryId,
        price: form.value.price,
        stock: form.value.stock,
        images: imageList.value,
        description: form.value.description,
      })
      ElMessage.success('修改成功')
    } else {
      await businessProductApi.add({
        name: form.value.name,
        categoryId: form.value.categoryId,
        price: form.value.price,
        stock: form.value.stock,
        images: imageList.value,
        description: form.value.description,
      })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await fetchList()
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.product-page {
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.breadcrumb-bar {
  margin-bottom: 12px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.header-left h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-count {
  font-size: 13px;
  color: #909399;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 220px;
}

.category-select {
  width: 140px;
}

.status-select {
  width: 120px;
}

.table-card {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 16px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow-y: auto;
}

.table-card :deep(.table-header) {
  background: #fff;
  color: #303133;
  font-weight: 600;
}

.product-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.product-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  font-size: 14px;
  color: #c0c4cc;
}

.price {
  color: #f56c6c;
  font-weight: 500;
}

.score-text {
  margin-left: 6px;
  font-size: 12px;
  color: #e6a23c;
  vertical-align: middle;
}

.dialog-form {
  padding: 8px 0;
}

.upload-wrapper {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: flex-start;
  max-width: 340px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.image-item .upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.image-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
}

.product-uploader .upload-placeholder {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  transition: border-color 0.25s;
  background: #fafafa;
}

.product-uploader .upload-placeholder:hover {
  border-color: #409eff;
}

.product-uploader :deep(.el-upload) {
  border: none;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 20px 0 10px;
}

.upload-tip {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.4;
}
</style>