<template>
  <div class="admin-login-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>

    <div class="login-wrapper">
      <!-- 左侧品牌信息区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo">
            <div class="logo-icon">
              <i class="el-icon-lock"></i>
            </div>
            <h1>Admin System</h1>
          </div>
          <div class="brand-description">
            <h2>欢迎回来</h2>
            <p>请登录您的管理员账户以访问系统管理功能</p>
          </div>
          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>安全可靠的身份验证</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>完整的系统管理功能</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>实时数据监控</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <el-card class="login-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-content">
                <h3>管理员登录</h3>
                <p>请输入您的账户信息</p>
              </div>
            </div>
          </template>

          <el-form
            :model="loginForm"
            :rules="rules"
            ref="loginFormRef"
            label-position="top"
            size="large"
            @keyup.enter="handleLogin"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入管理员用户名"
                prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
                prefix-icon="Lock"
                clearable
              />
            </el-form-item>

            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="primary" :underline="false">忘记密码?</el-link>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="handleLogin"
                :loading="loading"
                class="login-button"
                size="large"
              >
                <template #loading>
                  <i class="el-icon-loading"></i>
                  登录中...
                </template>
                <template #default>
                  <i class="el-icon-right"></i>
                  立即登录
                </template>
              </el-button>
            </el-form-item>

            <el-divider content-position="center">或</el-divider>

            <el-form-item>
              <div class="alternative-actions">
                <el-button
                  type="default"
                  class="back-button"
                  @click="$router.push('/')"
                  plain
                >
                  <i class="el-icon-house"></i>
                  返回首页
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 底部版权信息 -->
        <div class="footer">
          <p>&copy; 2024 Admin System. All rights reserved.</p>
          <div class="footer-links">
            <el-link type="info" :underline="false">隐私政策</el-link>
            <el-divider direction="vertical" />
            <el-link type="info" :underline="false">服务条款</el-link>
            <el-divider direction="vertical" />
            <el-link type="info" :underline="false">联系我们</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAdminStore } from "@/stores/admin";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const adminStore = useAdminStore();

const loginFormRef = ref(null);
const loading = ref(false);
const rememberMe = ref(false);

const loginForm = reactive({
  username: "",
  password: "",
});

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, message: "用户名长度不能小于3位", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await adminStore.login(loginForm);
        ElMessage.success("登录成功，正在跳转...");
        setTimeout(() => {
          // 检查是否有重定向路径，默认跳转到首页
          const redirect = route.query.redirect || "/";
          router.push(redirect);
        }, 1000);
      } catch (error) {
        ElMessage.error(error.message || "登录失败，请检查用户名和密码");
      } finally {
        loading.value = false;
      }
    }
  });
};

// 添加页面加载动画
onMounted(() => {
  document.body.style.overflow = "hidden";
});
</script>

<style scoped>
.admin-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
  font-family: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    sans-serif;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

.login-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  overflow: hidden;
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.brand-section {
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  align-items: center;
}

.brand-content {
  width: 100%;
}

.brand-logo {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
}

.brand-logo h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.brand-description h2 {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 15px;
}

.brand-description p {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
}

.feature-list {
  margin-top: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
}

.feature-item i {
  margin-right: 10px;
  font-size: 16px;
}

.form-section {
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
}

.login-card {
  border: none;
  background: transparent;
}

.login-card :deep(.el-card__header) {
  border-bottom: none;
  padding-bottom: 0;
}

.card-header {
  text-align: center;
}

.header-content h3 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.header-content p {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-button {
  width: 100%;
  border-radius: 10px;
  height: 48px;
  font-weight: 600;
  font-size: 16px;
  background: linear-gradient(135deg, #67c23a 0%, #19be6b 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.back-button {
  width: 100%;
  border-radius: 10px;
  height: 48px;
  font-weight: 600;
}

.alternative-actions {
  width: 100%;
}

.footer {
  margin-top: 30px;
  text-align: center;
}

.footer p {
  color: #7f8c8d;
  font-size: 12px;
  margin-bottom: 10px;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    grid-template-columns: 1fr;
    width: 95%;
    height: auto;
    margin: 20px;
  }

  .brand-section {
    display: none;
  }

  .form-section {
    padding: 30px 20px;
  }
}
</style>