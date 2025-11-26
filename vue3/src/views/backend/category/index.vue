<template>
  <div class="category-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新增分类
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" size="default">
          <el-form-item label="分类名称">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入分类名称" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择状态" 
              clearable
              style="width: 120px"
            >
              <el-option label="启用" value="ACTIVE" />
              <el-option label="禁用" value="INACTIVE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchCategories">
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

      <!-- 分类表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="sortOrder" label="排序值" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.sortOrder }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusDisplayName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.statusDisplayName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="showEditDialog(row)"
            >
              编辑
            </el-button>
            <el-button 
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'" 
              size="small" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
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

    <!-- 新增/编辑分类对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="categoryFormRef" 
        :model="categoryForm" 
        :rules="categoryRules" 
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input 
            v-model="categoryForm.name" 
            placeholder="请输入分类名称" 
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number 
            v-model="categoryForm.sortOrder" 
            :min="0" 
            :max="9999"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">数值越小，排序越靠前</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio value="ACTIVE">启用</el-radio>
            <el-radio value="INACTIVE">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { 
  getCategoryPage, 
  createCategory, 
  updateCategory, 
  deleteCategory, 
  updateCategoryStatus 
} from '@/api/category'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)
const categoryFormRef = ref()

// 分类表单
const categoryForm = reactive({
  id: null,
  name: '',
  sortOrder: 0,
  status: 'ACTIVE'
})

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 100, message: '分类名称长度在1到100个字符之间', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
    { type: 'number', min: 0, max: 9999, message: '排序值必须在0到9999之间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取分类列表
const fetchCategories = () => {
  loading.value = true
  getCategoryPage({
    current: currentPage.value,
    size: pageSize.value,
    name: searchForm.name,
    status: searchForm.status
  }, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      total.value = res.total || 0
      loading.value = false
    },
    onError: (error) => {
      console.error('获取分类列表失败:', error)
      loading.value = false
    }
  })
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchCategories()
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchCategories()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCategories()
}

// 显示新增对话框
const showCreateDialog = () => {
  dialogTitle.value = '新增分类'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  Object.assign(categoryForm, {
    id: row.id,
    name: row.name,
    sortOrder: row.sortOrder,
    status: row.status
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(categoryForm, {
    id: null,
    name: '',
    sortOrder: 0,
    status: 'ACTIVE'
  })
  if (categoryFormRef.value) {
    categoryFormRef.value.clearValidate()
  }
}

// 提交表单
const handleSubmit = () => {
  categoryFormRef.value.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      
      if (isEdit.value) {
        // 编辑分类
        updateCategory(categoryForm.id, {
          name: categoryForm.name,
          sortOrder: categoryForm.sortOrder,
          status: categoryForm.status
        }, {
          successMsg: '分类更新成功',
          onSuccess: () => {
            dialogVisible.value = false
            fetchCategories()
            submitLoading.value = false
          },
          onError: () => {
            submitLoading.value = false
          }
        })
      } else {
        // 新增分类
        createCategory({
          name: categoryForm.name,
          sortOrder: categoryForm.sortOrder,
          status: categoryForm.status
        }, {
          successMsg: '分类创建成功',
          onSuccess: () => {
            dialogVisible.value = false
            fetchCategories()
            submitLoading.value = false
          },
          onError: () => {
            submitLoading.value = false
          }
        })
      }
    }
  })
}

// 切换状态
const toggleStatus = (row) => {
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const actionText = newStatus === 'ACTIVE' ? '启用' : '禁用'
  
  ElMessageBox.confirm(
    `确定要${actionText}分类"${row.name}"吗？`,
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    updateCategoryStatus(row.id, newStatus, {
      successMsg: `分类${actionText}成功`,
      onSuccess: () => {
        fetchCategories()
      }
    })
  }).catch(() => {
    // 取消操作
  })
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除分类"${row.name}"吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    deleteCategory(row.id, {
      successMsg: '分类删除成功',
      onSuccess: () => {
        fetchCategories()
      }
    })
  }).catch(() => {
    // 取消删除
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
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
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
