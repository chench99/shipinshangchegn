<template>
  <div class="snack-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>食品管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新增零食
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" size="default">
          <el-form-item label="食品名称">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入食品名称" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="分类">
            <el-select 
              v-model="searchForm.categoryId" 
              placeholder="请选择分类" 
              clearable
              style="width: 150px"
            >
              <el-option 
                v-for="category in categoryList" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择状态" 
              clearable
              style="width: 120px"
            >
              <el-option label="上架" value="ON_SALE" />
              <el-option label="下架" value="OFF_SHELF" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格范围">
            <el-input 
              v-model.number="searchForm.minPrice" 
              placeholder="最低价" 
              style="width: 100px"
            />
            <span style="margin: 0 10px;">-</span>
            <el-input 
              v-model.number="searchForm.maxPrice" 
              placeholder="最高价" 
              style="width: 100px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchSnacks">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 零食表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            >
              <template #error>
                <div class="table-image-error">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </template>
            </el-image>
            <el-text v-else type="info">无图片</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食品名称" width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元)" width="100">
          <template #default="{ row }">
            <el-text type="danger">¥{{ row.price }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stock > 10 ? 'success' : (row.stock > 0 ? 'warning' : 'danger')">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salesCount" label="销量" width="80" />
        <el-table-column prop="statusDisplayName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'danger'">
              {{ row.statusDisplayName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button 
              type="info" 
              size="small" 
              @click="showDetailDialog(row)"
            >
              详情
            </el-button>
            <el-button 
              type="primary" 
              size="small" 
              @click="showEditDialog(row)"
            >
              编辑
            </el-button>
            <el-button 
              :type="row.status === 'ON_SALE' ? 'warning' : 'success'" 
              size="small" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 'ON_SALE' ? '下架' : '上架' }}
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑零食对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="snackFormRef" 
        :model="snackForm" 
        :rules="snackRules" 
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="食品名称" prop="name">
              <el-input 
                v-model="snackForm.name" 
                placeholder="请输入食品名称" 
                maxlength="255"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select 
                v-model="snackForm.categoryId" 
                placeholder="请选择分类"
                style="width: 100%"
              >
                <el-option 
                  v-for="category in categoryList" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格(元)" prop="price">
              <el-input-number 
                v-model="snackForm.price" 
                :min="0" 
                :precision="2"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number 
                v-model="snackForm.stock" 
                :min="0" 
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="零食描述" prop="description">
          <el-input 
            v-model="snackForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入零食描述" 
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <div class="upload-container">
            <!-- 当前封面图片预览 -->
            <div v-if="snackForm.coverImage" class="current-image-preview">
              <el-image
                :src="snackForm.coverImage"
                fit="cover"
                style="width: 100px; height: 100px; border-radius: 8px;"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <el-button 
                size="small" 
                type="danger" 
                @click="removeCoverImage"
                style="margin-left: 10px;"
              >
                移除
              </el-button>
            </div>
            
            <!-- 文件上传组件 -->
            <el-upload
              action="#"
              :auto-upload="true"
              :show-file-list="false"
              :http-request="handleCoverUpload"
              :before-upload="beforeCoverUpload"
              :disabled="isUploadingCover"
              accept="image/*"
            >
              <el-button 
                type="primary" 
                :loading="isUploadingCover"
                :disabled="isUploadingCover"
              >
                {{ isUploadingCover ? '上传中...' : '上传封面图片' }}
              </el-button>
            </el-upload>
            
            <!-- 临时文件确认区域 (仅新增时显示) -->
            <div v-if="!isEdit && coverImageTempId && !isUploadingCover" class="temp-file-actions">
              <el-text type="success" size="small">封面图片已预上传成功</el-text>
              <div class="temp-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="confirmCoverUpload"
                >
                  确认使用
                </el-button>
                <el-button 
                  size="small" 
                  @click="cancelCoverUpload"
                >
                  取消
                </el-button>
              </div>
            </div>
          </div>
          <div class="form-tip">支持 JPG、PNG 格式，文件大小不超过 5MB</div>
        </el-form-item>

        <el-form-item label="详情图片">
          <div class="upload-container">
            <!-- 已上传的详情图片预览 -->
            <div v-if="snackForm.detailImages && snackForm.detailImages.length > 0" class="detail-images-preview">
              <div 
                v-for="(image, index) in snackForm.detailImages" 
                :key="`detail-image-${index}-${image}`" 
                class="detail-image-item"
              >
                <el-image
                  :src="image"
                  fit="cover"
                  style="width: 80px; height: 80px; border-radius: 4px;"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="removeDetailImage(index)"
                  style="margin-top: 5px;"
                >
                  移除
                </el-button>
              </div>
            </div>
            
            <!-- 批量文件上传组件 -->
            <el-upload
              action="#"
              :auto-upload="true"
              :show-file-list="false"
              :http-request="handleDetailUpload"
              :before-upload="beforeDetailUpload"
              :disabled="isUploadingDetail"
              accept="image/*"
              multiple
            >
              <el-button 
                type="primary" 
                :loading="isUploadingDetail"
                :disabled="isUploadingDetail"
              >
                {{ isUploadingDetail ? '上传中...' : '上传详情图片' }}
              </el-button>
            </el-upload>
            
            <!-- 临时文件确认区域 (仅新增时显示) -->
            <div v-if="!isEdit && detailImagesTempIds.length > 0 && !isUploadingDetail" class="temp-file-actions">
              <el-text type="success" size="small">{{ detailImagesTempIds.length }}张详情图片已预上传成功</el-text>
              <div class="temp-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="confirmDetailUpload"
                >
                  确认使用
                </el-button>
                <el-button 
                  size="small" 
                  @click="cancelDetailUpload"
                >
                  取消
                </el-button>
              </div>
            </div>
          </div>
          <div class="form-tip">支持 JPG、PNG 格式，文件大小不超过 5MB，可同时上传多张图片</div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="snackForm.status">
            <el-radio label="ON_SALE">上架</el-radio>
            <el-radio label="OFF_SHELF">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 零食详情对话框 -->
    <el-dialog 
      title="零食详情" 
      v-model="detailDialogVisible" 
      width="700px"
    >
      <div class="snack-detail" v-if="currentSnack">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ currentSnack.id }}</el-descriptions-item>
          <el-descriptions-item label="食品名称">{{ currentSnack.name }}</el-descriptions-item>
          <el-descriptions-item label="所属分类">{{ currentSnack.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ currentSnack.price }}</el-descriptions-item>
          <el-descriptions-item label="库存数量">{{ currentSnack.stock }}</el-descriptions-item>
          <el-descriptions-item label="销售数量">{{ currentSnack.salesCount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentSnack.status === 'ON_SALE' ? 'success' : 'danger'">
              {{ currentSnack.statusDisplayName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentSnack.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">{{ formatDate(currentSnack.updateTime) }}</el-descriptions-item>
          <el-descriptions-item label="零食描述" :span="2">
            {{ currentSnack.description || '无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="image-section" style="margin-top: 20px;">
          <h4>封面图片</h4>
          <el-image
            v-if="currentSnack.coverImage"
            :src="currentSnack.coverImage"
            :preview-src-list="[currentSnack.coverImage]"
            fit="cover"
            style="width: 200px; height: 200px; border-radius: 8px;"
          >
            <template #error>
              <div class="detail-image-error">
                <i class="el-icon-picture-outline"></i>
                <span>图片加载失败</span>
              </div>
            </template>
          </el-image>
          <el-text v-else type="info">无封面图片</el-text>

          <h4 style="margin-top: 20px;">详情图片</h4>
          <div v-if="currentSnack.detailImages && currentSnack.detailImages.length > 0">
            <el-image
              v-for="(image, index) in currentSnack.detailImages"
              :key="index"
              :src="image"
              :preview-src-list="currentSnack.detailImages"
              fit="cover"
              style="width: 150px; height: 150px; border-radius: 8px; margin-right: 10px; margin-bottom: 10px;"
            >
              <template #error>
                <div class="detail-image-error">
                  <i class="el-icon-picture-outline"></i>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
          <el-text v-else type="info">无详情图片</el-text>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Picture } from '@element-plus/icons-vue'
import { getSnackPage, createSnack, updateSnack, deleteSnack, getSnackById, updateSnackStatus } from '@/api/snack'
import { getActiveCategoryList } from '@/api/category'
import { uploadTempBusinessFile, confirmTempFile, uploadBusinessFile } from '@/api/FileApi'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 文件上传相关状态（策略A）
const coverImageTempId = ref(null)
const detailImagesTempIds = ref([])
const isUploadingCover = ref(false)
const uploadingDetailCount = ref(0) // 用计数器管理多文件上传状态

// 搜索表单
const searchForm = reactive({
  name: '',
  categoryId: null,
  status: '',
  minPrice: null,
  maxPrice: null
})

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const currentSnack = ref(null)

// 表单数据
const snackFormRef = ref()
const snackForm = reactive({
  id: null,
  name: '',
  categoryId: null,
  description: '',
  price: 0,
  stock: 0,
  coverImage: '',
  detailImages: [],
  status: 'ON_SALE'
})


// 表单验证规则
const snackRules = {
  name: [
    { required: true, message: '请输入食品名称', trigger: 'blur' },
    { min: 1, max: 255, message: '食品名称长度在 1 到 255 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择所属分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能为负数', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑零食' : '新增零食'
})

const isUploadingDetail = computed(() => {
  return uploadingDetailCount.value > 0
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 文件上传前的校验
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('封面图片只能是图片格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('封面图片大小不能超过 5MB!')
    return false
  }
  return true
}

const beforeDetailUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('详情图片只能是图片格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('详情图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 封面图片上传处理（根据编辑状态选择策略）
const handleCoverUpload = async ({ file, onSuccess, onError }) => {
  try {
    isUploadingCover.value = true
    
    if (isEdit.value && snackForm.id) {
      // 策略C：编辑时直接上传并绑定到现有业务ID
      await uploadBusinessFile(file, {
        businessType: 'SNACK_COVER',
        businessId: snackForm.id,
        businessField: 'coverImage'
      }, {
        onSuccess: (response) => {
          snackForm.coverImage = response.filePath
          console.log('封面图片直接上传成功:', response.filePath)
          onSuccess(response)
        },
        onError: (error) => {
          console.error('封面图片直接上传失败:', error)
          onError(error)
        }
      })
    } else {
      // 策略A第一阶段：新增时临时业务文件上传
      await uploadTempBusinessFile(file, 'SNACK_COVER', 'coverImage', {
        onSuccess: (response) => {
          coverImageTempId.value = response.id
          console.log('封面图片临时业务文件上传成功，临时文件ID:', coverImageTempId.value)
          onSuccess(response)
        },
        onError: (error) => {
          console.error('封面图片临时业务文件上传失败:', error)
          onError(error)
        }
      })
    }
  } catch (error) {
    console.error('封面图片上传过程发生错误:', error)
    onError(error)
  } finally {
    isUploadingCover.value = false
  }
}

// 详情图片上传处理（根据编辑状态选择策略）
const handleDetailUpload = async ({ file, onSuccess, onError }) => {
  try {
    uploadingDetailCount.value++
    console.log('开始上传详情图片:', file.name, '编辑模式:', isEdit.value, '零食ID:', snackForm.id)
    
    if (isEdit.value && snackForm.id) {
      // 策略C：编辑时直接上传并绑定到现有业务ID
      await uploadBusinessFile(file, {
        businessType: 'SNACK_DETAIL',
        businessId: snackForm.id,
        businessField: 'detailImages'
      }, {
        onSuccess: (response) => {
          if (!snackForm.detailImages) {
            snackForm.detailImages = []
          }
          snackForm.detailImages.push(response.filePath)
          console.log('详情图片直接上传成功:', response.filePath)
          onSuccess(response)
        },
        onError: (error) => {
          console.error('详情图片直接上传失败:', error)
          onError(error)
        }
      })
    } else {
      // 策略A第一阶段：新增时临时业务文件上传
      await uploadTempBusinessFile(file, 'SNACK_DETAIL', 'detailImages', {
        onSuccess: (response) => {
          detailImagesTempIds.value.push(response.id)
          console.log('详情图片临时业务文件上传成功，临时文件ID:', response.id)
          onSuccess(response)
        },
        onError: (error) => {
          console.error('详情图片临时业务文件上传失败:', error)
          onError(error)
        }
      })
    }
  } catch (error) {
    console.error('详情图片上传过程发生错误:', error)
    onError(error)
  } finally {
    uploadingDetailCount.value--
  }
}

// 策略A第二阶段：确认封面图片上传
const confirmCoverUpload = async () => {
  if (!coverImageTempId.value) {
    ElMessage.error('没有待确认的封面图片文件')
    return
  }

  try {
    await confirmTempFile(coverImageTempId.value, {
      businessType: 'SNACK_COVER',
      businessId: '0', // 新增时使用0，在保存时会更新为实际ID
      businessField: 'coverImage'
    }, {
      onSuccess: (response) => {
        snackForm.coverImage = response.filePath
        coverImageTempId.value = null
        ElMessage.success('封面图片确认成功')
      },
      onError: (error) => {
        console.error('封面图片确认失败:', error)
        ElMessage.error('封面图片确认失败')
      }
    })
  } catch (error) {
    console.error('封面图片确认过程发生错误:', error)
    ElMessage.error('封面图片确认过程发生错误')
  }
}

// 策略A第二阶段：确认详情图片上传
const confirmDetailUpload = async () => {
  if (detailImagesTempIds.value.length === 0) {
    ElMessage.error('没有待确认的详情图片文件')
    return
  }

  try {
    const confirmPromises = detailImagesTempIds.value.map(tempId => 
      confirmTempFile(tempId, {
        businessType: 'SNACK_DETAIL',
        businessId: '0', // 新增时使用0，在保存时会更新为实际ID
        businessField: 'detailImages'
      }, {
        showDefaultMsg: false
      })
    )

    const results = await Promise.all(confirmPromises)
    const newDetailImages = results.map(result => result.filePath)
    
    snackForm.detailImages = [...(snackForm.detailImages || []), ...newDetailImages]
    detailImagesTempIds.value = []
    ElMessage.success(`${newDetailImages.length}张详情图片确认成功`)
  } catch (error) {
    console.error('详情图片确认失败:', error)
    ElMessage.error('详情图片确认失败')
  }
}

// 取消封面图片上传
const cancelCoverUpload = () => {
  coverImageTempId.value = null
  ElMessage.info('已取消封面图片上传')
}

// 取消详情图片上传
const cancelDetailUpload = () => {
  detailImagesTempIds.value = []
  ElMessage.info('已取消详情图片上传')
}

// 移除封面图片
const removeCoverImage = () => {
  snackForm.coverImage = ''
}

// 移除详情图片
const removeDetailImage = (index) => {
  snackForm.detailImages.splice(index, 1)
}

// 零食创建成功后确认所有临时文件
const confirmTempFilesAfterCreate = async (snackId) => {
  const confirmPromises = []
  
  // 确认封面图片临时文件
  if (coverImageTempId.value) {
    confirmPromises.push(
      confirmTempFile(coverImageTempId.value, {
        businessType: 'SNACK_COVER',
        businessId: snackId,
        businessField: 'coverImage'
      }, {
        showDefaultMsg: false
      })
    )
  }
  
  // 确认详情图片临时文件
  if (detailImagesTempIds.value.length > 0) {
    const detailPromises = detailImagesTempIds.value.map(tempId => 
      confirmTempFile(tempId, {
        businessType: 'SNACK_DETAIL',
        businessId: snackId,
        businessField: 'detailImages'
      }, {
        showDefaultMsg: false
      })
    )
    confirmPromises.push(...detailPromises)
  }
  
  if (confirmPromises.length > 0) {
    const results = await Promise.all(confirmPromises)
    console.log('所有临时文件确认成功:', results)
    
    // 清除临时文件状态
    coverImageTempId.value = null
    detailImagesTempIds.value = []
  }
}

// 获取零食列表
const fetchSnacks = () => {
  loading.value = true
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    ...searchForm
  }
  
  getSnackPage(params, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      total.value = res.total || 0
      loading.value = false
    },
    onError: (error) => {
      console.error('获取零食列表失败:', error)
      loading.value = false
    }
  })
}

// 获取分类列表
const fetchCategories = () => {
  getActiveCategoryList({
    onSuccess: (res) => {
      categoryList.value = res || []
    },
    onError: (error) => {
      console.error('获取分类列表失败:', error)
    }
  })
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    name: '',
    categoryId: null,
    status: '',
    minPrice: null,
    maxPrice: null
  })
  currentPage.value = 1
  fetchSnacks()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchSnacks()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchSnacks()
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  
  // 先获取完整的零食详情数据
  getSnackById(row.id, {
    onSuccess: (snackDetail) => {
      Object.assign(snackForm, {
        id: snackDetail.id,
        name: snackDetail.name,
        categoryId: snackDetail.categoryId,
        description: snackDetail.description,
        price: snackDetail.price,
        stock: snackDetail.stock,
        coverImage: snackDetail.coverImage,
        status: snackDetail.status
      })
      
      // 处理详情图片
      console.log('编辑对话框加载详情图片:', snackDetail.detailImages)
      if (snackDetail.detailImages && Array.isArray(snackDetail.detailImages)) {
        snackForm.detailImages = [...snackDetail.detailImages]
      } else {
        snackForm.detailImages = []
      }
      
      // 重置文件上传状态
      coverImageTempId.value = null
      detailImagesTempIds.value = []
      
      dialogVisible.value = true
    },
    onError: (error) => {
      console.error('获取零食详情失败:', error)
      ElMessage.error('获取零食详情失败')
    }
  })
}

// 显示详情对话框
const showDetailDialog = (row) => {
  getSnackById(row.id, {
    onSuccess: (res) => {
      currentSnack.value = res
      detailDialogVisible.value = true
    },
    onError: (error) => {
      console.error('获取零食详情失败:', error)
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(snackForm, {
    id: null,
    name: '',
    categoryId: null,
    description: '',
    price: 0,
    stock: 0,
    coverImage: '',
    detailImages: [],
    status: 'ON_SALE'
  })
  
  // 重置文件上传状态
  coverImageTempId.value = null
  detailImagesTempIds.value = []
  isUploadingCover.value = false
  uploadingDetailCount.value = 0
  
  snackFormRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = () => {
  snackFormRef.value?.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      
      const formData = {
        ...snackForm
      }
      
      if (isEdit.value) {
        // 更新零食（策略C已在上传时直接处理）
        updateSnack(snackForm.id, formData, {
          successMsg: '零食更新成功',
          onSuccess: () => {
            submitLoading.value = false
            dialogVisible.value = false
            fetchSnacks()
          },
          onError: (error) => {
            console.error('更新失败:', error)
            submitLoading.value = false
          }
        })
      } else {
        // 新增零食（策略A需要先创建零食，再确认临时文件）
        createSnack(formData, {
          successMsg: '零食创建成功',
          onSuccess: async (newSnack) => {
            try {
              // 零食创建成功后，确认临时文件
              await confirmTempFilesAfterCreate(newSnack.id)
              
              submitLoading.value = false
              dialogVisible.value = false
              fetchSnacks()
            } catch (error) {
              console.error('确认临时文件失败:', error)
              submitLoading.value = false
              // 虽然零食创建成功了，但文件确认失败，提示用户
              ElMessage.warning('零食创建成功，但文件关联失败，请重新编辑上传文件')
            }
          },
          onError: (error) => {
            console.error('创建失败:', error)
            submitLoading.value = false
          }
        })
      }
    }
  })
}

// 切换状态
const toggleStatus = (row) => {
  const newStatus = row.status === 'ON_SALE' ? 'OFF_SHELF' : 'ON_SALE'
  const statusText = newStatus === 'ON_SALE' ? '上架' : '下架'
  
  ElMessageBox.confirm(
    `确定要${statusText}零食"${row.name}"吗？`,
    '状态切换确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    updateSnackStatus(row.id, newStatus, {
      successMsg: `零食${statusText}成功`,
      onSuccess: () => {
        fetchSnacks()
      }
    })
  }).catch(() => {
    // 用户取消操作
  })
}

// 删除零食
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除零食"${row.name}"吗？删除后无法恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error',
    }
  ).then(() => {
    deleteSnack(row.id, {
      successMsg: '零食删除成功',
      onSuccess: () => {
        fetchSnacks()
      }
    })
  }).catch(() => {
    // 用户取消操作
  })
}

// 组件挂载时初始化
onMounted(() => {
  fetchSnacks()
  fetchCategories()
})
</script>

<style scoped>
.snack-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.dialog-footer {
  text-align: right;
}

.snack-detail {
  max-height: 600px;
  overflow-y: auto;
}

.image-section h4 {
  margin: 10px 0;
  color: #606266;
}

/* 图片错误占位符样式 */
.table-image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 20px;
}

.detail-image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 12px;
  text-align: center;
}

.detail-image-error i {
  font-size: 24px;
  margin-bottom: 4px;
}

/* 文件上传相关样式 */
.upload-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.current-image-preview {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-images-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-image-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 20px;
}

/* 临时文件确认区域样式 */
.temp-file-actions {
  margin-top: 10px;
  padding: 12px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
  text-align: center;
}

.temp-actions {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
