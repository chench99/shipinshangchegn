<template>
  <div class="inventory-container">
    <el-card class="operation-card" shadow="hover">
      <div class="card-header">
        <div class="left">
          <div class="title-box">
            <span class="icon">📦</span>
            <span class="title">库存管理中心</span>
          </div>
          <span class="subtitle">一站式管理商品库存，支持新品上架与出入库操作</span>
        </div>
        <div class="right">
          <el-button type="success" @click="openCreateDialog" class="add-btn">
            <el-icon style="margin-right: 5px"><Plus /></el-icon> 新增库存
          </el-button>
          <el-button type="primary" @click="loadData" :loading="loading">
            <el-icon style="margin-right: 5px"><Refresh /></el-icon> 刷新列表
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <el-table :data="tableData" v-loading="loading" style="width: 100%" :header-cell-style="{background:'#f8f9fb',color:'#606266'}">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column label="商品信息" min-width="200">
          <template #default="scope">
            <div class="product-info">
              <el-image 
                class="product-img"
                :src="scope.row.coverImage" 
                :preview-src-list="[scope.row.coverImage]"
                fit="cover">
                <template #error>
                  <div class="image-slot"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
              <div class="product-detail">
                <span class="product-name">{{ scope.row.name }}</span>
                <span class="product-desc" :title="scope.row.description">{{ scope.row.description || '暂无描述' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="categoryName" label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag size="small" type="info" effect="plain">{{ scope.row.categoryName || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="价格" width="120" align="center">
          <template #default="scope">
            <span class="price">¥ {{ scope.row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="当前库存" width="180" align="center">
          <template #default="scope">
            <div class="stock-info">
              <span class="stock-num" :class="{ 'low-stock': scope.row.stock < 20 }">{{ scope.row.stock }}</span>
              <el-tag v-if="scope.row.stock < 20" type="danger" size="small" effect="dark" round>库存紧张</el-tag>
              <el-tag v-else type="success" size="small" effect="light" round>充足</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="快捷操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" size="small" plain @click="openStockDialog(scope.row, 'add')">
                入库
              </el-button>
              <el-button type="warning" size="small" plain @click="openStockDialog(scope.row, 'reduce')">
                出库
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :total="total"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </el-card>

    <el-dialog
      v-model="stockDialogVisible"
      :title="stockDialogTitle"
      width="400px"
      center
      destroy-on-close
      class="custom-dialog"
    >
      <div class="dialog-content">
        <p class="dialog-subtitle">当前操作商品：<span class="highlight">{{ currentItem.name }}</span></p>
        <p class="current-stock">当前库存：{{ currentItem.stock }}</p>
        
        <el-form @submit.prevent>
          <el-form-item>
            <el-input-number 
              v-model="stockNum" 
              :min="1" 
              :max="opType === 'reduce' ? currentItem.stock : 99999"
              style="width: 100%;"
              size="large"
              controls-position="right"
              :placeholder="opType === 'add' ? '请输入入库数量' : '请输入出库数量'"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStock" :loading="submitting">确认调整</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="createDialogVisible"
      title="✨ 新增食品"
      width="700px"
      destroy-on-close
      top="5vh"
      class="custom-dialog"
    >
      <el-form :model="createForm" ref="createFormRef" :rules="rules" label-width="100px" class="create-form">
        
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="createForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品状态" prop="status">
              <el-radio-group v-model="createForm.status">
                <el-radio label="ON_SALE">上架</el-radio>
                <el-radio label="OFF_SHELF">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售价格" prop="price">
              <el-input-number 
                v-model="createForm.price" 
                :precision="2" 
                :step="0.1" 
                :min="0" 
                controls-position="right"
                style="width: 100%"
                placeholder="0.00"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始库存" prop="stock">
              <el-input-number 
                v-model="createForm.stock" 
                :min="0" 
                :step="1" 
                controls-position="right"
                style="width: 100%"
                placeholder="0"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="avatar-uploader"
            action="/api/file/upload/temp-business"
            name="file"
            :show-file-list="false"
            :headers="uploadHeaders"
            :data="{ businessType: 'SNACK_COVER', businessField: 'coverImage' }"
            :on-success="handleCoverSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="createForm.coverImage" :src="createForm.coverImage" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸 1:1，支持 jpg/png</div>
        </el-form-item>

        <el-form-item label="详情图片" prop="detailImages">
          <el-upload
            action="/api/file/upload/temp-business"
            name="file"
            list-type="picture-card"
            :headers="uploadHeaders"
            :data="{ businessType: 'SNACK_DETAIL', businessField: 'detailImages' }"
            :on-success="handleDetailSuccess"
            :on-remove="handleDetailRemove"
            :before-upload="beforeUpload"
            :file-list="detailFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="createForm.description" :rows="3" placeholder="请输入商品详细描述..."></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreate" :loading="creating">提交</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Picture } from '@element-plus/icons-vue'
import axios from 'axios'

// --- 状态定义 ---
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 库存调整
const stockDialogVisible = ref(false)
const opType = ref('') 
const currentItem = ref({})
const stockNum = ref(1)
const submitting = ref(false)
const stockDialogTitle = computed(() => opType.value === 'add' ? '📦 商品入库补货' : '📉 商品出库/损耗登记')

// 新增商品
const createDialogVisible = ref(false)
const creating = ref(false)
const categories = ref([])
const createFormRef = ref(null)
const detailFileList = ref([]) 

const createForm = reactive({
  name: '',
  categoryId: null,
  price: undefined, // 关键：初始化为 undefined 解决输入体验问题
  stock: undefined, // 关键：初始化为 undefined
  coverImage: '',
  description: '',
  status: 'ON_SALE',
  detailImages: [] 
})

const uploadHeaders = computed(() => {
  return { 'Authorization': localStorage.getItem('token') || '' }
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图片', trigger: 'change' }]
}

onMounted(() => {
  loadData()
  loadCategories()
})

// --- API 方法 ---
const loadData = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/api/snack/page', {
      params: { current: currentPage.value, size: pageSize.value },
      headers: { 'Authorization': token }
    })
    if (res.data.code === '200') {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.msg || '加载失败')
    }
  } catch (error) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/api/category/active', {
      headers: { 'Authorization': token }
    })
    if (res.data.code === '200') {
      categories.value = res.data.data
    }
  } catch (e) { console.error('分类加载失败', e) }
}

// --- 库存调整 ---
const openStockDialog = (row, type) => {
  currentItem.value = row
  opType.value = type
  stockNum.value = 1
  stockDialogVisible.value = true
}

const submitStock = async () => {
  if (!stockNum.value) return
  submitting.value = true
  const quantity = opType.value === 'reduce' ? -stockNum.value : stockNum.value
  
  try {
    const token = localStorage.getItem('token')
    const res = await axios.put(`/api/snack/${currentItem.value.id}/stock`, null, {
      params: { quantity },
      headers: { 'Authorization': token }
    })
    if (res.data.code === '200') {
      ElMessage.success('库存调整成功')
      stockDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('网络错误')
  } finally {
    submitting.value = false
  }
}

// --- 文件上传 ---
const beforeUpload = (file) => {
  const isImg = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg) ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  if (!isLt2M) ElMessage.error('上传图片大小不能超过 2MB!')
  return isImg && isLt2M
}

const handleCoverSuccess = (res) => {
  if (res.code === '200') {
    createForm.coverImage = res.data.filePath
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error('封面上传失败')
  }
}

const handleDetailSuccess = (res, file) => {
  if (res.code === '200') {
    createForm.detailImages.push(res.data.filePath)
    file.url = res.data.filePath 
  } else {
    ElMessage.error('图片上传失败')
  }
}

const handleDetailRemove = (uploadFile) => {
  const url = uploadFile.response ? uploadFile.response.data.filePath : uploadFile.url
  createForm.detailImages = createForm.detailImages.filter(item => item !== url)
}

// --- 新增商品 ---
const openCreateDialog = () => {
  // 使用 undefined 初始化数字字段，解决输入体验问题
  Object.assign(createForm, {
    name: '', categoryId: null, price: undefined, stock: undefined, coverImage: '', description: '', status: 'ON_SALE', detailImages: []
  })
  detailFileList.value = []
  createDialogVisible.value = true
}

const submitCreate = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      creating.value = true
      try {
        const token = localStorage.getItem('token')
        // 确保提交时如果有 undefined 转为 0，虽然校验规则已限制必填
        const submitData = {
            ...createForm,
            price: createForm.price || 0,
            stock: createForm.stock || 0
        }
        const res = await axios.post('/api/snack', submitData, {
          headers: { 'Authorization': token }
        })
        if (res.data.code === '200') {
          ElMessage.success('新品上架成功！')
          createDialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.data.msg || '创建失败')
        }
      } catch (e) {
        ElMessage.error('网络请求错误')
      } finally {
        creating.value = false
      }
    }
  })
}

const handlePageChange = (val) => {
  currentPage.value = val
  loadData()
}
</script>

<style scoped lang="scss">
.inventory-container {
  padding: 24px;
  background-color: #f6f8fa;
  min-height: 100%;

  .operation-card {
    margin-bottom: 20px;
    border: none;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 5px 0;

      .left {
        display: flex;
        flex-direction: column;
        gap: 6px;

        .title-box {
          display: flex;
          align-items: center;
          gap: 10px;
          .icon { font-size: 24px; }
          .title { font-size: 20px; font-weight: 700; color: #2c3e50; }
        }
        .subtitle { font-size: 13px; color: #95a5a6; margin-left: 40px; }
      }
      
      .right {
        .add-btn {
          background: linear-gradient(135deg, #42d392 0%, #2ecc71 100%);
          border: none;
          &:hover { opacity: 0.9; transform: translateY(-1px); }
        }
      }
    }
  }

  .table-card {
    border: none;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);

    .product-info {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .product-img {
        width: 56px;
        height: 56px;
        border-radius: 8px;
        background: #f8f9fa;
        border: 1px solid #eee;
        display: flex; align-items: center; justify-content: center;
        color: #cbd5e0;
      }
      
      .product-detail {
        display: flex; flex-direction: column; gap: 4px;
        .product-name { font-weight: 600; color: #2d3748; font-size: 15px; }
        .product-desc { font-size: 12px; color: #a0aec0; max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
      }
    }

    .price {
      font-family: 'DIN Alternate', sans-serif;
      color: #e74c3c;
      font-weight: 700;
      font-size: 16px;
    }

    .stock-info {
      display: flex; align-items: center; justify-content: center; gap: 10px;
      .stock-num {
        font-weight: 700; font-size: 16px; color: #2d3748;
        &.low-stock { color: #e74c3c; }
      }
    }

    .action-buttons {
      display: flex; justify-content: center; gap: 8px;
    }

    .pagination-wrapper {
      margin-top: 25px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

.dialog-content {
  text-align: center;
  padding: 10px 20px;
  .dialog-subtitle { font-size: 15px; color: #7f8c8d; margin-bottom: 8px; .highlight { color: #3498db; font-weight: bold; } }
  .current-stock { font-size: 14px; color: #95a5a6; margin-bottom: 25px; }
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.create-form {
  padding: 0 20px;
}

/* 修复上传组件样式 */
.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    width: 120px;
    height: 120px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  :deep(.el-upload:hover) {
    border-color: #409eff;
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}
</style>