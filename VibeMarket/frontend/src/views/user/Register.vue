<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="background-elements">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <el-card class="register-card">
      <!-- 卡片顶部装饰 -->
      <div class="card-decoration">
        <div class="decoration-bar"></div>
      </div>
      
      <template #header>
        <div class="card-header">
          <div class="header-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="header-title">用户注册</span>
          <p class="header-subtitle">创建您的专属账户</p>
        </div>
      </template>
      
      <el-form
        :model="registerForm"
        :rules="rules"
        ref="registerFormRef"
        label-width="80px"
        class="custom-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（4-20位）"
            class="custom-input"
          >
            <template #prefix>
              <i class="icon-user"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            show-password
            class="custom-input"
          >
            <template #prefix>
              <i class="icon-lock"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="请输入手机号" 
            class="custom-input"
          >
            <template #prefix>
              <i class="icon-phone"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱（可选）"
            class="custom-input"
          >
            <template #prefix>
              <i class="icon-email"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button
            type="primary"
            @click="handleRegister"
            :loading="loading"
            class="register-btn"
            >注册账号</el-button
          >
        </el-form-item>
        <el-form-item class="login-section">
          <div class="login-link">
            <span>已有账号？</span>
            <el-link type="primary" @click="$router.push('/login')" class="login-link-btn"
              >立即登录</el-link
            >
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 底部装饰 -->
      <div class="card-footer">
        <p class="footer-text">注册即表示同意我们的服务条款和隐私政策</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

const registerFormRef = ref(null);
const loading = ref(false);

const registerForm = reactive({
  username: "",
  password: "",
  phone: "",
  email: "",
});

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 4, max: 20, message: "用户名长度在4到20个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在6到20个字符", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" },
  ],
  email: [{ type: "email", message: "邮箱格式不正确", trigger: "blur" }],
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await userStore.register(registerForm);
        ElMessage.success("注册成功，请登录");
        router.push("/login");
      } catch (error) {
        ElMessage.error(error.message || "注册失败");
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 背景装饰元素 */
.background-elements {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation: float 8s infinite ease-in-out;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  left: 80%;
  animation: float 10s infinite ease-in-out;
  animation-delay: 1s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 85%;
  animation: float 12s infinite ease-in-out;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

.register-card {
  width: 480px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: none;
  position: relative;
  z-index: 1;
  overflow: hidden;
  background: #fff;
}

.card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #67c23a, #19be6b);
}

.card-header {
  text-align: center;
  padding: 24px 0 16px;
  background: linear-gradient(to right, #f8f9ff, #ffffff);
}

.header-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border-radius: 50%;
  color: white;
}

.header-title {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
}

.header-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0;
}

.custom-form {
  padding: 24px 32px 16px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #4a5568;
  padding-right: 16px;
}

.custom-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__inner) {
  height: 48px;
  border-radius: 8px;
  padding-left: 40px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__inner:focus) {
  border-color: #19be6b;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

:deep(.custom-input .el-input__prefix) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  color: #a0aec0;
}

.icon-user, .icon-lock, .icon-phone, .icon-email {
  width: 18px;
  height: 18px;
  display: inline-block;
  background-size: contain;
  background-repeat: no-repeat;
}

.icon-user {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23a0aec0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'%3E%3C/path%3E%3Ccircle cx='12' cy='7' r='4'%3E%3C/circle%3E%3C/svg%3E");
}

.icon-lock {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23a0aec0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='11' width='18' height='11' rx='2' ry='2'%3E%3C/rect%3E%3Cpath d='M7 11V7a5 5 0 0 1 10 0v4'%3E%3C/path%3E%3C/svg%3E");
}

.icon-phone {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23a0aec0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z'%3E%3C/path%3E%3C/svg%3E");
}

.icon-email {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23a0aec0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'%3E%3C/path%3E%3Cpolyline points='22,6 12,13 2,6'%3E%3C/polyline%3E%3C/svg%3E");
}

.form-actions {
  margin-top: 32px;
  margin-bottom: 16px;
}

.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #67c23a, #19be6b);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.login-section {
  margin-bottom: 0;
}

.login-link {
  text-align: center;
  width: 100%;
  font-size: 14px;
  color: #718096;
}

.login-link-btn {
  font-weight: 600;
}

.card-footer {
  padding: 16px 32px;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}

.footer-text {
  font-size: 12px;
  color: #a0aec0;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 576px) {
  .register-card {
    width: 90%;
    margin: 20px;
  }
  
  .custom-form {
    padding: 16px 20px 12px;
  }
  
  .card-header {
    padding: 20px 0 12px;
  }
  
  .header-title {
    font-size: 20px;
  }
}
</style>