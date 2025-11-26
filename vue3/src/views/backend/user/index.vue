<template>
  <div class="user-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" size="default">
          <el-form-item label="用户名">
            <el-input 
              v-model="searchForm.username" 
              placeholder="请输入用户名" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input 
              v-model="searchForm.nickname" 
              placeholder="请输入昵称" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="用户类型">
            <el-select 
              v-model="searchForm.userType" 
              placeholder="请选择用户类型" 
              clearable
              style="width: 150px"
            >
              <el-option label="普通用户" value="USER" />
              <el-option label="管理员" value="ADMIN" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择状态" 
              clearable
              style="width: 120px"
            >
              <el-option label="活跃" value="ACTIVE" />
              <el-option label="非活跃" value="INACTIVE" />
              <el-option label="封禁" value="BANNED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchUsers">
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

      <!-- 用户表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="userTypeDisplayName" label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.userType === 'ADMIN' ? 'danger' : 'primary'">
              {{ row.userTypeDisplayName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusDisplayName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 'ACTIVE' ? 'success' : 
                     row.status === 'BANNED' ? 'danger' : 'warning'"
            >
              {{ row.statusDisplayName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              @click="viewUser(row)"
            >
              查看
            </el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="editUser(row)"
              :disabled="row.userType === 'ADMIN' && row.id !== currentUser.id"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="toggleUserStatus(row)"
              :disabled="row.userType === 'ADMIN'"
            >
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog 
      v-model="userDetailVisible" 
      title="用户详情" 
      width="600px"
    >
      <div v-if="selectedUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ selectedUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ selectedUser.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">
            <el-tag :type="selectedUser.userType === 'ADMIN' ? 'danger' : 'primary'">
              {{ selectedUser.userTypeDisplayName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag 
              :type="selectedUser.status === 'ACTIVE' ? 'success' : 
                     selectedUser.status === 'BANNED' ? 'danger' : 'warning'"
            >
              {{ selectedUser.statusDisplayName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(selectedUser.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(selectedUser.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 编辑用户弹窗 -->
    <el-dialog 
      v-model="userEditVisible" 
      title="编辑用户" 
      width="500px"
      @close="resetEditForm"
    >
      <el-form 
        ref="editFormRef"
        :model="editForm" 
        :rules="editRules" 
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="editForm.userType" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="非活跃" value="INACTIVE" />
            <el-option label="封禁" value="BANNED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userEditVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="editLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getUserPage, updateUser } from '@/api/user'

const userStore = useUserStore()

// 获取当前登录用户信息
const currentUser = computed(() => userStore.userInfo || {})

// 搜索表单
const searchForm = reactive({
  username: '',
  nickname: '',
  userType: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 弹窗控制
const userDetailVisible = ref(false)
const userEditVisible = ref(false)
const selectedUser = ref(null)
const editLoading = ref(false)

// 编辑表单
const editFormRef = ref()
const editForm = reactive({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  userType: '',
  status: ''
})

// 编辑表单验证规则
const editRules = {
  nickname: [
    { max: 255, message: '昵称长度不能超过255个字符', trigger: 'blur' }
  ],
  phone: [
    { 
      pattern: /^1[3-9]\d{9}$/, 
      message: '手机号格式不正确', 
      trigger: 'blur' 
    }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      username: searchForm.username || undefined,
      nickname: searchForm.nickname || undefined,
      userType: searchForm.userType || undefined,
      status: searchForm.status || undefined,
      currentPage: currentPage.value,
      size: pageSize.value
    }
    
    await getUserPage(params, {
      onSuccess: (res) => {
        tableData.value = res.records || []
        total.value = res.total || 0
      },
      onError: (error) => {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      }
    })
  } finally {
    loading.value = false
  }
}


// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    nickname: '',
    userType: '',
    status: ''
  })
  currentPage.value = 1
  fetchUsers()
}

// 查看用户详情
const viewUser = (user) => {
  selectedUser.value = user
  userDetailVisible.value = true
}

// 编辑用户
const editUser = (user) => {
  Object.assign(editForm, {
    id: user.id,
    username: user.username,
    nickname: user.nickname || '',
    phone: user.phone || '',
    userType: user.userType,
    status: user.status
  })
  userEditVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    const updateParams = {
      nickname: editForm.nickname,
      phone: editForm.phone,
      userType: editForm.userType,
      status: editForm.status
    }
    
    await updateUser(editForm.id, updateParams, {
      onSuccess: () => {
        ElMessage.success('用户信息更新成功')
        userEditVisible.value = false
        fetchUsers()
      },
      onError: (error) => {
        console.error('更新用户失败:', error)
        ElMessage.error('更新用户失败')
      }
    })
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    editLoading.value = false
  }
}


// 重置编辑表单
const resetEditForm = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
  Object.assign(editForm, {
    id: '',
    username: '',
    nickname: '',
    phone: '',
    userType: '',
    status: ''
  })
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  const action = user.status === 'ACTIVE' ? '禁用' : '启用'
  const newStatus = user.status === 'ACTIVE' ? 'BANNED' : 'ACTIVE'
  
  try {
    await ElMessageBox.confirm(
      `确认要${action}用户 "${user.username}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用更新用户API来切换状态
    const updateParams = {
      nickname: user.nickname,
      phone: user.phone,
      userType: user.userType,
      status: newStatus
    }
    
    await updateUser(user.id, updateParams, {
      onSuccess: () => {
        ElMessage.success(`${action}成功`)
        fetchUsers()
      },
      onError: (error) => {
        console.error('状态切换失败:', error)
        ElMessage.error('状态切换失败')
      }
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作取消或失败:', error)
    }
  }
}


// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchUsers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUsers()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.user-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
  }

  .search-bar {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .user-detail {
    .el-descriptions {
      margin-top: 20px;
    }
  }
}
</style> 