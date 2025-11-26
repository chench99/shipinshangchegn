<template>
  <div class="page-container">
    <div class="toolbar">
      <el-button type="primary" @click="openCreateDialog">新增轮播</el-button>
      <el-input v-model="query.title" placeholder="标题关键词" style="width: 200px; margin-left: 12px;" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px; margin-left: 8px;">
        <el-option label="启用" value="ENABLED" />
        <el-option label="禁用" value="DISABLED" />
      </el-select>
      <el-button @click="fetchPage" style="margin-left: 8px;">查询</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column label="#" type="index" width="60" />
      <el-table-column label="图片" width="160">
        <template #default="{ row }">
          <img :src="row.imageUrl || ''" alt="" style="width: 120px; height: 60px; object-fit: cover;" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
      <el-table-column prop="jumpType" label="跳转类型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.jumpType === 'URL'" type="primary">URL</el-tag>
          <el-tag v-else-if="row.jumpType === 'PRODUCT'" type="success">商品</el-tag>
          <el-tag v-else type="info">无</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="跳转目标" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.jumpType === 'URL'" style="color: #409EFF;">
            {{ row.jumpTarget || '未设置' }}
          </span>
          <span v-else-if="row.jumpType === 'PRODUCT'" style="color: #67C23A;">
            商品ID: {{ row.jumpTarget || '未设置' }}
          </span>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="primary" @click="openUploadDialog(row)">上传图片</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :page-size="query.size"
        :current-page="query.current"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑轮播' : '新增轮播'" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="跳转类型">
          <el-select v-model="editForm.jumpType" @change="handleJumpTypeChange">
            <el-option label="URL" value="URL" />
            <el-option label="商品" value="PRODUCT" />
            <el-option label="无" value="NONE" />
          </el-select>
        </el-form-item>
        <el-form-item label="跳转目标" v-if="editForm.jumpType !== 'NONE'">
          <el-input 
            v-if="editForm.jumpType === 'URL'" 
            v-model="editForm.jumpTarget" 
            placeholder="请输入完整的URL地址，如：https://www.example.com" 
          />
          <el-input-number 
            v-else-if="editForm.jumpType === 'PRODUCT'" 
            v-model="editForm.jumpTarget" 
            placeholder="请输入商品ID"
            :min="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <div>
            <el-upload
              action="#"
              :http-request="handleDialogUploadRequest"
              :before-upload="beforeUploadValidation"
              :show-file-list="false"
              :limit="1"
              list-type="picture-card"
            >
              <el-icon v-if="!editForm.imageUrl"><Plus /></el-icon>
              <div v-else style="position: relative; width: 100%; height: 100%;">
                <img :src="editForm.imageUrl" style="width: 100%; height: 100%; object-fit: cover;" />
                <div style="position: absolute; bottom: 0; left: 0; right: 0; background: rgba(0,0,0,0.6); color: white; text-align: center; font-size: 12px; padding: 2px;">
                  点击替换
                </div>
              </div>
            </el-upload>
            <div v-if="dialogTempFileId" style="color:#999; font-size:12px; margin-top:4px;">已选择图片，保存后生效</div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 上传图片（策略C直接绑定） -->
    <el-dialog v-model="uploadVisible" title="上传轮播图片" width="520px">
      <el-upload
        action="#"
        :http-request="handleUploadRequest"
        :on-success="handleUploadSuccess"
        :before-upload="beforeUploadValidation"
        :show-file-list="false"
        :limit="1"
        list-type="picture-card"
      >
        <el-icon v-if="!currentRow || !currentRow.imageUrl"><Plus /></el-icon>
        <div v-else style="position: relative; width: 100%; height: 100%;">
          <img :src="currentRow.imageUrl" style="width: 100%; height: 100%; object-fit: cover;" />
          <div style="position: absolute; bottom: 0; left: 0; right: 0; background: rgba(0,0,0,0.6); color: white; text-align: center; font-size: 12px; padding: 2px;">
            点击替换
          </div>
        </div>
      </el-upload>
      <div v-if="currentRow && currentRow.imageUrl" style="margin-top:12px; text-align: center; color: #666; font-size: 14px;">
        当前图片预览
      </div>
      <template #footer>
        <el-button @click="uploadVisible=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getCarouselPage, createCarousel, updateCarousel, deleteCarousel,
  getEnabledCarousels
} from '@/api/carousel'
import { uploadTempBusinessFile, confirmTempFile, uploadBusinessFile } from '@/api/FileApi'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, title: '', status: '' })

const editVisible = ref(false)
const editForm = reactive({ id: null, title: '', jumpType: 'URL', jumpTarget: '', sortOrder: 0, status: 'ENABLED' })
const dialogTempFileId = ref(null)

const uploadVisible = ref(false)
const currentRow = ref(null)
const tempFileId = ref(null)

const fetchPage = () => {
  loading.value = true
  getCarouselPage({ current: query.current, size: query.size, title: query.title, status: query.status }, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      total.value = res.total || 0
      loading.value = false
    },
    onError: () => { loading.value = false }
  })
}

const handlePageChange = (page) => {
  query.current = page
  fetchPage()
}

const openCreateDialog = () => {
  Object.assign(editForm, { id: null, title: '', jumpType: 'URL', jumpTarget: '', sortOrder: 0, status: 'ENABLED', imageUrl: '' })
  dialogTempFileId.value = null
  editVisible.value = true
}

// 跳转类型改变时清空跳转目标
const handleJumpTypeChange = () => {
  editForm.jumpTarget = ''
}

const openEditDialog = (row) => {
  Object.assign(editForm, { ...row })
  dialogTempFileId.value = null
  editVisible.value = true
}

const submitEdit = () => {
  const payload = { title: editForm.title, jumpType: editForm.jumpType, jumpTarget: editForm.jumpTarget, sortOrder: editForm.sortOrder, status: editForm.status }
  if (!editForm.id) {
    createCarousel(payload, {
      successMsg: '创建成功',
      onSuccess: (res) => {
        // 新建后如果已选择临时图片，则确认绑定
        if (dialogTempFileId.value) {
          confirmTempFile(dialogTempFileId.value, { businessType: 'CAROUSEL_IMAGE', businessId: String(res.id), businessField: 'image' }, {
            successMsg: '图片绑定成功',
            onSuccess: () => { dialogTempFileId.value = null; editVisible.value = false; fetchPage() }
          })
        } else {
          editVisible.value = false; fetchPage()
        }
      }
    })
  } else {
    updateCarousel(editForm.id, payload, {
      successMsg: '更新成功',
      onSuccess: () => {
        // 编辑场景下若选择了临时图片（通常立即确认，不走这里），兜底处理
        if (dialogTempFileId.value) {
          confirmTempFile(dialogTempFileId.value, { businessType: 'CAROUSEL_IMAGE', businessId: String(editForm.id), businessField: 'image' }, {
            successMsg: '图片绑定成功',
            onSuccess: () => { dialogTempFileId.value = null; editVisible.value = false; fetchPage() }
          })
        } else {
          editVisible.value = false; fetchPage()
        }
      }
    })
  }
}

const openUploadDialog = (row) => {
  currentRow.value = row
  tempFileId.value = null
  uploadVisible.value = true
}

// 列表“上传图片”：编辑场景统一采用策略C（直接绑定业务ID）
const handleUploadRequest = async ({ file, onSuccess, onError }) => {
  if (!currentRow.value || !currentRow.value.id) {
    ElMessage.error('请先选择要上传图片的轮播项')
    onError && onError(new Error('missing business id'))
    return
  }
  uploadBusinessFile(file, { businessType: 'CAROUSEL_IMAGE', businessId: String(currentRow.value.id), businessField: 'image' }, {
    successMsg: '图片上传成功',
    onSuccess: (res) => {
      // 先触发成功回调（用于回显）
      onSuccess && onSuccess(res)
      // 刷新列表
      fetchPage()
    },
    onError
  })
}

const handleUploadSuccess = (res) => {
  console.log('上传成功', res)
  // res 为后端返回的 FileInfoDTO 对象（已由 request 工具解包）
  if (res && res.filePath && currentRow.value) {
    // 正确提取 filePath 字段
    currentRow.value.imageUrl = res.filePath
  }
}

// 编辑对话框内的上传：新建用策略A（暂存并在保存后确认），编辑用策略C（直接绑定业务ID）
const handleDialogUploadRequest = ({ file, onSuccess, onError }) => {
  // 编辑场景：已有ID，直接业务绑定上传（策略C）
  if (editForm.id) {
    uploadBusinessFile(file, { businessType: 'CAROUSEL_IMAGE', businessId: String(editForm.id), businessField: 'image' }, {
      successMsg: '图片上传成功',
      onSuccess: (res) => {
        // 立即更新对话框内预览
        if (res && res.filePath) {
          editForm.imageUrl = res.filePath
        }
        fetchPage()
        onSuccess && onSuccess(res)
      },
      onError
    })
    return
  }
  // 新建场景：策略A 预上传，待保存后确认
  uploadTempBusinessFile(file, 'CAROUSEL_IMAGE', 'image', {
    onSuccess: (res) => {
      dialogTempFileId.value = res.id
      // 临时文件也可预览，提升用户体验
      if (res && res.filePath) {
        editForm.imageUrl = res.filePath
      }
      ElMessage.success('图片已选择，保存后生效')
      onSuccess && onSuccess(res)
    },
    onError
  })
}

const beforeUploadValidation = (file) => {
  const allow = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  const max = 10 * 1024 * 1024
  if (!allow.includes(file.type)) { ElMessage.error('仅支持 JPG/PNG/GIF/WebP'); return false }
  if (file.size > max) { ElMessage.error('文件不能超过10MB'); return false }
  return true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该轮播吗？', '提示', { type: 'warning' }).then(() => {
    deleteCarousel(row.id, {
      successMsg: '删除成功',
      onSuccess: fetchPage
    })
  }).catch(() => {})
}

onMounted(() => {
  fetchPage()
})
</script>

<style scoped>
.page-container { padding: 16px; }
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
.pagination { margin-top: 12px; text-align: right; }
</style>


