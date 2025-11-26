<template>
  <div class="page-wrap">
    <el-card class="filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="零食ID">
          <el-input v-model.number="query.snackId" placeholder="输入零食ID" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model.number="query.userId" placeholder="输入用户ID" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="评分">
          <el-select v-model="query.rating" placeholder="全部" clearable style="width: 140px;">
            <el-option v-for="r in [5,4,3,2,1]" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="snackId" label="零食ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled :max="5" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="图片" min-width="200">
          <template #default="{ row }">
            <div class="img-list">
              <el-image
                v-for="(img, idx) in (row.images || [])"
                :key="idx"
                :src="img"
                fit="cover"
                style="width: 60px; height: 60px; margin-right: 6px; border-radius: 4px;"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时间" prop="createTime" width="180" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-popconfirm title="确认删除该评价？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="prev, pager, next, ->, total"
          :total="total"
          :current-page="page"
          :page-size="size"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReviewPage, deleteReview } from '@/api/review'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const query = reactive({ snackId: null, userId: null, rating: null })

const fetchData = () => {
  loading.value = true
  getReviewPage({ current: page.value, size: size.value, ...query }, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      total.value = res.total || 0
      loading.value = false
    },
    onError: () => { loading.value = false }
  })
}

const handleDelete = (row) => {
  deleteReview(row.id, {
    successMsg: '删除成功',
    onSuccess: () => fetchData()
  })
}

const handlePageChange = (p) => {
  page.value = p
  fetchData()
}

const resetFilters = () => {
  query.snackId = null
  query.userId = null
  query.rating = null
  page.value = 1
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.page-wrap { padding: 12px; }
.filter-card { margin-bottom: 12px; }
.img-list { display: flex; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>


