<template>
  <div class="review-editor">
    <div class="editor-card">
      <div class="editor-header">
        <span class="title">写评价</span>
        <el-rate v-model="form.rating" :max="5" :disabled="submitting || !canWrite" />
      </div>
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="4"
        maxlength="500"
        show-word-limit
        placeholder="分享下你的真实使用体验吧~"
        :disabled="submitting || !canWrite"
      />

      <div class="upload-row">
        <el-upload
          action="#"
          list-type="picture-card"
          :http-request="handleUpload"
          :on-remove="handleRemove"
          :file-list="fileList"
          :auto-upload="true"
          :disabled="submitting || !canWrite"
          accept="image/*"
          :limit="6"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="hint">最多6张图</div>
      </div>

      <div class="action-row">
        <CandyButton :loading="submitting" :disabled="!canWrite" @click="submitReview">提交评价</CandyButton>
        <span v-if="!canWrite" class="forbid-tip">需购买并完成订单后才可评价</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createReview, canReview as apiCanReview } from '@/api/review'
import { uploadTempBusinessFile, confirmTempFile } from '@/api/FileApi'
import CandyButton from '@/components/common/CandyButton.vue'

const props = defineProps({
  snackId: { type: Number, required: true }
})

const emit = defineEmits(['submitted'])

const form = reactive({ rating: 5, content: '' })
const submitting = ref(false)
const tempIds = ref([]) // 策略A：存放临时文件ID
const fileList = ref([])
const canWrite = ref(false)

const handleUpload = async ({ file, onSuccess, onError }) => {
  await uploadTempBusinessFile(file, 'COMMENT_IMAGE', 'images', {
    onSuccess: (res) => {
      tempIds.value.push(res.id)
      fileList.value.push({ name: res.originalName || String(res.id), url: res.filePath, tempId: res.id })
      onSuccess(res)
    },
    onError: (err) => onError(err)
  })
}

const handleRemove = (file) => {
  tempIds.value = tempIds.value.filter(id => id !== file.tempId)
  fileList.value = fileList.value.filter(f => f.tempId !== file.tempId)
}

const submitReview = async () => {
  if (!form.content.trim()) {
    ElMessage.error('请填写评价内容')
    return
  }
  if (!canWrite.value) {
    ElMessage.warning('需购买并完成订单后才可评价')
    return
  }
  submitting.value = true
  // 1. 创建评价
  createReview({ snackId: props.snackId, rating: form.rating, content: form.content }, {
    successMsg: '评价提交成功',
    onSuccess: async (res) => {
      const reviewId = res
      // 2. 确认所有临时图片关联到评价
      if (tempIds.value.length > 0) {
        const tasks = tempIds.value.map(id => confirmTempFile(id, {
          businessType: 'COMMENT_IMAGE',
          businessId: String(reviewId),
          businessField: 'images'
        }, { showDefaultMsg: false }))
        try {
          await Promise.all(tasks)
        } catch (e) {
          // 保持静默，交给全局拦截提示
        }
      }
      // 重置表单
      form.rating = 5
      form.content = ''
      tempIds.value = []
      fileList.value = []
      submitting.value = false
      emit('submitted')
    },
    onError: () => { submitting.value = false }
  })
}

onMounted(() => {
  apiCanReview(props.snackId, {
    showDefaultMsg: false,
    onSuccess: (res) => { canWrite.value = !!res },
    onError: () => { canWrite.value = false }
  })
})
</script>

<style scoped>
.review-editor { padding: 8px; }
.editor-card {
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  border: 1px solid rgba(255, 193, 7, 0.16);
  border-radius: 16px;
  padding: 12px;
  box-shadow: 6px 6px 18px rgba(255, 171, 64, 0.12), -6px -6px 18px rgba(255,255,255,0.8);
}
.editor-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;
}
.editor-header .title { font-weight: 700; color: #D2691E; }
.upload-row { margin: 8px 0 12px; }
.hint { color: #909399; font-size: 12px; margin-top: 6px; }
.action-row { display: flex; align-items: center; gap: 12px; }
.forbid-tip { color: #909399; font-size: 12px; }
</style>


