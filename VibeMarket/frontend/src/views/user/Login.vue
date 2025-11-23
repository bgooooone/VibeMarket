<template>
  <div class="login-container">
    <!-- 优化的背景装饰元素 -->
    <div class="background-elements">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 优化的主要内容区域 -->
    <div class="login-content-wrapper">
      <div class="login-content">
        <!-- 左侧欢迎信息 - 优化布局 -->
        <div class="welcome-section">
          <div class="welcome-content">
            <div class="brand-area">
              <div class="brand-logo">
                <div class="logo-icon">
                  <i class="el-icon-user-solid"></i>
                </div>
                <div class="brand-text">
                  <h1>企业服务平台</h1>
                  <p>Professional Service Platform</p>
                </div>
              </div>
            </div>

            <div class="welcome-area">
              <h2>欢迎回来</h2>
              <p class="welcome-subtitle">登录您的账户，继续使用我们的服务</p>

              <div class="feature-cards">
                <div class="feature-card">
                  <div class="feature-icon">
                    <i class="el-icon-lock"></i>
                  </div>
                  <div class="feature-content">
                    <h4>安全可靠</h4>
                    <p>多重加密保护您的账户安全</p>
                  </div>
                </div>

                <div class="feature-card">
                  <div class="feature-icon">
                    <i class="el-icon-setting"></i>
                  </div>
                  <div class="feature-content">
                    <h4>个性化体验</h4>
                    <p>根据您的需求定制服务</p>
                  </div>
                </div>

                <div class="feature-card">
                  <div class="feature-icon">
                    <i class="el-icon-mobile"></i>
                  </div>
                  <div class="feature-content">
                    <h4>多端同步</h4>
                    <p>支持多设备数据实时同步</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧登录表单 - 优化布局 -->
        <div class="form-section">
          <div class="form-container">
            <div class="form-header">
              <h3>用户登录</h3>
              <p>请输入您的账户信息以继续</p>
            </div>

            <el-form
              :model="loginForm"
              :rules="rules"
              ref="loginFormRef"
              label-position="top"
              size="large"
              @keyup.enter="handleLogin"
              class="login-form"
            >
              <el-form-item
                label="用户名"
                prop="username"
                class="form-item-custom"
              >
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  class="custom-input"
                >
                  <template #prefix>
                    <i class="el-input__icon el-icon-user"></i>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item
                label="密码"
                prop="password"
                class="form-item-custom"
              >
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  class="custom-input"
                >
                  <template #prefix>
                    <i class="el-input__icon el-icon-lock"></i>
                  </template>
                </el-input>
              </el-form-item>

              <div class="form-options">
                <el-checkbox v-model="rememberMe" class="remember-checkbox">
                  记住我
                </el-checkbox>
                <el-link
                  type="primary"
                  :underline="false"
                  class="forget-link"
                  @click="showForgetPassword = true"
                >
                  忘记密码?
                </el-link>
              </div>

              <el-button
                type="primary"
                @click="handleLogin"
                :loading="loading"
                class="login-btn"
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

              <div class="form-divider">
                <span class="divider-line"></span>
                <span class="divider-text">或</span>
                <span class="divider-line"></span>
              </div>

              <div class="alternative-actions">
                <div class="register-action">
                  <span class="register-text">还没有账号？</span>
                  <el-button
                    type="success"
                    link
                    @click="$router.push('/register')"
                    class="register-btn"
                  >
                    <i class="el-icon-edit"></i>
                    立即注册
                  </el-button>
                </div>
                <el-button
                  type="default"
                  @click="$router.push('/')"
                  class="home-btn"
                  plain
                >
                  <i class="el-icon-house"></i>
                  返回首页
                </el-button>
              </div>
            </el-form>

            <!-- 底部信息 -->
            <div class="footer-info">
              <div class="footer-content">
                <p>&copy; 2024 企业服务平台 · 让工作更高效</p>
                <div class="footer-links">
                  <el-link type="info" :underline="false">用户协议</el-link>
                  <el-divider direction="vertical" />
                  <el-link type="info" :underline="false">隐私政策</el-link>
                  <el-divider direction="vertical" />
                  <el-link type="info" :underline="false">联系我们</el-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="showForgetPassword"
      title="找回密码"
      width="400px"
      center
      class="forget-dialog"
    >
      <div class="forget-password-content">
        <div class="forget-icon">
          <i class="el-icon-key"></i>
        </div>
        <h4>密码重置</h4>
        <p>请联系系统管理员或客服重置您的密码</p>
        <div class="contact-info">
          <div class="contact-item">
            <i class="el-icon-phone"></i>
            <span>客服热线: 400-123-4567</span>
          </div>
          <div class="contact-item">
            <i class="el-icon-message"></i>
            <span>邮箱: support@company.com</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showForgetPassword = false" class="dialog-close-btn"
          >关闭</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loginFormRef = ref(null);
const loading = ref(false);
const rememberMe = ref(false);
const showForgetPassword = ref(false);

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

  try {
    const valid = await loginFormRef.value.validate();
    if (valid) {
      loading.value = true;
      await userStore.login(loginForm);

      ElMessage.success({
        message: "登录成功",
        duration: 1500,
        showClose: true,
      });

      setTimeout(() => {
        const redirect = route.query.redirect || "/";
        router.push(redirect);
      }, 1000);
    }
  } catch (error) {
    ElMessage.error({
      message: error.message || "登录失败，请检查用户名和密码",
      duration: 3000,
      showClose: true,
    });
  } finally {
    loading.value = false;
  }
};

// 页面加载动画
onMounted(() => {
  document.body.style.overflow = "hidden";
});

onUnmounted(() => {
  document.body.style.overflow = "";
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  position: relative;
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.background-elements {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 150px;
  height: 150px;
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 100px;
  height: 100px;
  top: 70%;
  right: 8%;
  animation-delay: 2.5s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 15%;
  left: 12%;
  animation-delay: 5s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-15px) scale(1.05);
  }
}

.login-content-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-content {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  width: 100%;
  min-height: 680px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  overflow: hidden;
  animation: slideUp 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 左侧欢迎区域优化 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.1) 1px,
    transparent 1px
  );
  background-size: 30px 30px;
  animation: backgroundMove 25s linear infinite;
}

@keyframes backgroundMove {
  from {
    transform: translateX(0) translateY(0);
  }
  to {
    transform: translateX(-30px) translateY(-30px);
  }
}

.welcome-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand-area {
  margin-bottom: 40px;
}

.brand-logo {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.logo-icon {
  width: 70px;
  height: 70px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 32px;
  backdrop-filter: blur(10px);
}

.brand-text h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #fff 0%, #e0e7ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-text p {
  font-size: 14px;
  opacity: 0.8;
  margin: 0;
  font-weight: 500;
}

.welcome-area h2 {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 12px;
  line-height: 1.2;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.5;
  margin-bottom: 40px;
}

.feature-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
}

.feature-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 22px;
}

.feature-content h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 6px 0;
}

.feature-content p {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
  line-height: 1.4;
}

/* 右侧表单区域优化 */
.form-section {
  padding: 40px;
  display: flex;
  flex-direction: column;
  background: white;
}

.form-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h3 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.form-header p {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-item-custom {
  margin-bottom: 24px;
}

:deep(.form-item-custom .el-form-item__label) {
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

:deep(.custom-input .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
}

:deep(.custom-input .el-input__wrapper:hover) {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  border-color: #d1d5db;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-checkbox :deep(.el-checkbox__label) {
  color: #6b7280;
  font-size: 14px;
}

.forget-link {
  font-size: 14px;
}

.login-btn {
  width: 100%;
  border-radius: 12px;
  height: 50px;
  font-weight: 600;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  margin-bottom: 30px;
}

.login-btn:hover:not(.is-loading) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.35);
}

.form-divider {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.divider-text {
  padding: 0 16px;
  color: #9ca3af;
  font-size: 14px;
  font-weight: 500;
}

.alternative-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.register-action {
  display: flex;
  align-items: center;
  gap: 8px;
}

.register-text {
  color: #6b7280;
  font-size: 14px;
}

.register-btn {
  font-weight: 600;
}

.home-btn {
  border-radius: 10px;
  padding: 12px 24px;
  width: 100%;
  max-width: 200px;
}

.footer-info {
  margin-top: auto;
  padding-top: 30px;
}

.footer-content {
  text-align: center;
}

.footer-content p {
  color: #9ca3af;
  font-size: 12px;
  margin-bottom: 8px;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

/* 忘记密码对话框样式 */
.forget-dialog :deep(.el-dialog__header) {
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

.forget-password-content {
  text-align: center;
  padding: 10px 0;
}

.forget-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 32px;
  color: white;
}

.forget-password-content h4 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f2937;
}

.forget-password-content p {
  color: #6b7280;
  margin-bottom: 24px;
  line-height: 1.5;
}

.contact-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  color: #667eea;
  font-weight: 500;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-item i {
  margin-right: 8px;
  font-size: 16px;
}

.dialog-close-btn {
  border-radius: 8px;
  padding: 10px 24px;
}

/* 响应式设计优化 */
@media (max-width: 1024px) {
  .login-content {
    grid-template-columns: 1fr;
    min-height: auto;
    max-width: 500px;
  }

  .welcome-section {
    display: none;
  }

  .form-section {
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 15px;
  }

  .login-content {
    border-radius: 20px;
  }

  .form-section {
    padding: 30px 25px;
  }

  .form-header {
    margin-bottom: 30px;
  }

  .form-header h3 {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .form-section {
    padding: 25px 20px;
  }

  .form-header h3 {
    font-size: 22px;
  }

  .footer-links {
    flex-direction: column;
    gap: 8px;
  }

  .footer-links :deep(.el-divider) {
    display: none;
  }
}

/* 深色模式支持优化 */
@media (prefers-color-scheme: dark) {
  .form-section {
    background: #1f2937;
  }

  .form-header h3 {
    color: #f9fafb;
  }

  :deep(.form-item-custom .el-form-item__label) {
    color: #d1d5db;
  }

  :deep(.custom-input .el-input__wrapper) {
    background: #374151;
    border-color: #4b5563;
  }

  :deep(.custom-input .el-input__inner) {
    color: #f9fafb;
  }

  :deep(.custom-input .el-input__inner::placeholder) {
    color: #9ca3af;
  }
}
</style>