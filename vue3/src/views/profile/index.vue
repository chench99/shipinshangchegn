<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>

      

      <CandyTabs v-model="activeTab" :items="tabs" />
      <!-- 基本信息 Tab -->
      <template v-if="activeTab === 'basic'">
          <div class="profile-info">
            <div class="avatar-container">
              <el-avatar :size="100" :src="avatarUrl" @error="() => false" />
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="handleUploadAvatar"
                :before-upload="beforeAvatarUpload"
                :disabled="isUploadingAvatar"
              >
                <CandyButton 
                  size="small" 
                  :loading="isUploadingAvatar"
                  :disabled="isUploadingAvatar"
                >
                  {{ isUploadingAvatar ? '上传中...' : '更换头像' }}
                </CandyButton>
              </el-upload>
              
              <!-- 临时文件确认区域 (仅在回退到策略A时显示) -->
              <div v-if="!userForm.id && tempFileId && !isUploadingAvatar" class="temp-file-actions">
                <el-text type="success" size="small">头像已预上传成功</el-text>
                <div class="temp-actions">
                  <CandyButton 
                    size="small" 
                    @click="confirmAvatarUpload"
                  >
                    确认更换
                  </CandyButton>
                  <CandyOutlineButton 
                    size="small" 
                    @click="cancelAvatarUpload"
                  >
                    取消
                  </CandyOutlineButton>
                </div>
              </div>
            </div>

            <div class="info-form">
              <el-form
                ref="userFormRef"
                :model="userForm"
                :rules="rules"
                label-width="100px"
                status-icon
              >
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="userForm.username" disabled />
                </el-form-item>

                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="userForm.nickname" />
                </el-form-item>

                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="userForm.phone" />
                </el-form-item>

                <el-form-item>
                  <CandyButton @click="submitUserInfo">保存修改</CandyButton>
                </el-form-item>
              </el-form>
            </div>
          </div>
      </template>

      <!-- 修改密码 Tab -->
      <template v-else-if="activeTab === 'password'">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            style="max-width: 500px; margin: 0 auto"
            status-icon
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                show-password
                placeholder="请输入旧密码"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <CandyButton @click="submitPassword">修改密码</CandyButton>
            </el-form-item>
          </el-form>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/user";
import { getCurrentUser, updateUser, updatePassword } from "@/api/user";
import { uploadTempBusinessFile, confirmTempFile, uploadBusinessFile } from "@/api/FileApi";
import CandyButton from '@/components/common/CandyButton.vue'
import CandyOutlineButton from '@/components/common/CandyOutlineButton.vue'
import CandyTabs from '@/components/common/CandyTabs.vue'

const userStore = useUserStore();
const activeTab = ref("basic");
const tabs = [
  { label: '基本信息', name: 'basic' },
  { label: '修改密码', name: 'password' }
]

const tempFileId = ref(null);
const isUploadingAvatar = ref(false);

// 表单引用
const userFormRef = ref(null);
const passwordFormRef = ref(null);

// 用户表单数据
const userForm = reactive({
  id: "",
  username: "",
  nickname: "",
  phone: "",
  avatar: "",
});

// 头像地址
const avatarUrl = computed(() => {
  return userForm.avatar;
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 表单校验规则
const rules = {
  nickname: [
    { max: 255, message: "昵称长度不能超过255个字符", trigger: "blur" }
  ],
  phone: [
    { required: false, trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: ["blur", "change"],
    },
  ],
};

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: ["blur", "change"],
    },
  ],
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 如果用户已登录，从 store 中获取用户信息
    if (userStore.isLoggedIn) {
      // 从后端重新获取最新的用户信息
      await getCurrentUser({
        showDefaultMsg: false,
        onSuccess: (data) => {
          // 更新store中的用户信息
          userStore.updateUserInfo(data);

          // 直接更新表单数据
          userForm.id = data.id || "";
          userForm.username = data.username || "";
          userForm.nickname = data.nickname || "";
          userForm.phone = data.phone || "";
          userForm.avatar = data.avatar || "";

          console.log("用户信息加载成功:", userForm);
        },
        onError: (error) => {
          console.error("获取用户信息失败", error);
          ElMessage.error("获取用户信息失败");
        }
      });
    }
  } catch (error) {
    console.error("获取用户信息失败", error);
    ElMessage.error("获取用户信息失败");
  }
};

// 上传头像前的校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error("头像只能是 JPG 或 PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("头像大小不能超过 2MB!");
    return false;
  }
  return true;
};

// 头像上传处理（用户更新使用策略C）
const handleUploadAvatar = async ({ file, onSuccess, onError }) => {
  try {
    isUploadingAvatar.value = true;
    
    if (userForm.id) {
      // 策略C：直接上传并绑定到现有用户ID
      await uploadBusinessFile(file, {
        businessType: 'USER_AVATAR',
        businessId: userForm.id,
        businessField: 'avatar'
      }, {
        onSuccess: (response) => {
          // 直接更新用户头像
          userForm.avatar = response.filePath;
          updateUserAvatar(response.filePath);
          console.log('头像直接上传成功:', response.filePath);
          onSuccess(response);
        },
        onError: (error) => {
          console.error('头像直接上传失败:', error);
          onError(error);
        }
      });
    } else {
      // 如果没有用户ID（理论上不应该出现），回退到策略A
      await uploadTempBusinessFile(file, 'USER_AVATAR', 'avatar', {
        onSuccess: (response) => {
          tempFileId.value = response.id;
          console.log('头像临时业务文件上传成功，临时文件ID:', tempFileId.value);
          onSuccess(response);
        },
        onError: (error) => {
          console.error('头像临时业务文件上传失败:', error);
          onError(error);
        }
      });
    }
  } catch (error) {
    console.error('头像上传过程发生错误:', error);
    onError(error);
  } finally {
    isUploadingAvatar.value = false;
  }
};

// 策略A第二阶段：确认头像上传
const confirmAvatarUpload = async () => {
  if (!tempFileId.value) {
    ElMessage.error('没有待确认的头像文件');
    return;
  }

  try {
    // 确认临时文件并关联到用户头像
    await confirmTempFile(tempFileId.value, {
      businessType: 'USER_AVATAR',
      businessId: userForm.id,
      businessField: 'avatar'
    }, {
      successMsg: '头像更换成功',
      onSuccess: (response) => {
        // 更新用户头像路径
        userForm.avatar = response.filePath;
        
        // 同步更新用户信息到后端
        updateUserAvatar(response.filePath);
        
        // 清除临时文件ID
        tempFileId.value = null;
      },
      onError: (error) => {
        console.error('头像确认失败:', error);
        ElMessage.error('头像确认失败');
      }
    });
  } catch (error) {
    console.error('头像确认过程发生错误:', error);
    ElMessage.error('头像确认过程发生错误');
  }
};

// 取消头像上传
const cancelAvatarUpload = () => {
  tempFileId.value = null;
  ElMessage.info('已取消头像更换');
};

// 更新用户头像信息到数据库
const updateUserAvatar = async (avatarPath) => {
  try {
    await updateUser(userForm.id, { avatar: avatarPath }, {
      showDefaultMsg: false,
      onSuccess: () => {
        // 更新本地用户信息
        const updatedUserInfo = { ...userStore.userInfo, avatar: avatarPath };
        userStore.updateUserInfo(updatedUserInfo);
      },
      onError: (error) => {
        console.error("头像信息保存失败", error);
        ElMessage.error("头像信息保存失败");
      }
    });
  } catch (error) {
    console.error("头像信息保存失败", error);
    ElMessage.error("头像信息保存失败");
    throw error;
  }
};

// 提交用户信息更新
const submitUserInfo = async () => {
  if (!userFormRef.value) return;

  try {
    // 表单验证
    await userFormRef.value.validate();

    await updateUser(userForm.id, {
      nickname: userForm.nickname,
      phone: userForm.phone,
    }, {
      showDefaultMsg: false,
      successMsg: "个人信息更新成功!",
      onSuccess: () => {
        // 更新本地用户信息
        const updatedUserInfo = {
          ...userStore.userInfo,
          nickname: userForm.nickname,
          phone: userForm.phone,
        };
        userStore.updateUserInfo(updatedUserInfo);
      },
      onError: (error) => {
        console.error("用户信息更新失败", error);
        ElMessage.error("用户信息更新失败");
      }
    });

  } catch (error) {
    console.error("保存个人信息失败", error);
    ElMessage.error("保存个人信息失败");
  }
};

// 提交密码修改
const submitPassword = async () => {
  if (!passwordFormRef.value) return;

  try {
    // 表单验证
    await passwordFormRef.value.validate();

    await updatePassword(userForm.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    }, {
      showDefaultMsg: false,
      onSuccess: () => {
        // 清空表单
        passwordForm.oldPassword = "";
        passwordForm.newPassword = "";
        passwordForm.confirmPassword = "";

        // 提示用户重新登录
        ElMessageBox.confirm("密码已修改，需要重新登录", "提示", {
          confirmButtonText: "重新登录",
          cancelButtonText: "取消",
          type: "warning",
        }).then(() => {
          // 清除用户信息并跳转到登录页
          userStore.clearUserInfo();
          window.location.href = "/login";
        });
      },
      onError: (error) => {
        console.error("密码信息保存失败", error);
        ElMessage.error("密码信息保存失败");
      }
    });
  } catch (error) {
    console.error("密码修改失败", error);
    ElMessage.error(error.message || "密码修改失败");
  }
};

// 监听用户表单数据变化
watch(
  userForm,
  (newVal) => {
    console.log("用户表单数据变化:", newVal);
  },
  { deep: true }
);

// 组件挂载时获取用户信息
onMounted(() => {
  getUserInfo();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 1000px;
  margin: 0 auto;
  border-radius: 20px;
  background: linear-gradient(145deg, #FFFFFF, #FFF7E6);
  box-shadow: 12px 12px 32px rgba(255, 171, 64, 0.16),
              -8px -8px 18px rgba(255, 255, 255, 0.85),
              inset 0 0 0 1px rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.16);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

@media (min-width: 768px) {
  .profile-info {
    flex-direction: row;
  }
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-uploader {
  text-align: center;
  margin-top: 10px;
}

.info-form {
  flex: 1;
}

.el-tabs {
  margin-top: 20px;
}

/* 移除快捷功能菜单后，无需相关样式 */

/* 临时文件确认区域样式 */
.temp-file-actions {
  margin-top: 15px;
  padding: 12px;
  background: linear-gradient(145deg, #FFFFFF, #FFFDF6);
  border: 1px solid rgba(255, 193, 7, 0.16);
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 6px 16px rgba(255, 171, 64, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.8);
}

.temp-actions {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style> 