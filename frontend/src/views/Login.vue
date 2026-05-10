<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
      <div class="decoration-grid"></div>
      <div class="decoration-lines">
        <div class="line line-1"></div>
        <div class="line line-2"></div>
        <div class="line line-3"></div>
      </div>
      <div class="decoration-dots">
        <div class="dot dot-1"></div>
        <div class="dot dot-2"></div>
        <div class="dot dot-3"></div>
        <div class="dot dot-4"></div>
        <div class="dot dot-5"></div>
        <div class="dot dot-6"></div>
      </div>
    </div>
    
    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="login-brand">
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="brand-title">金蝶财务管理系统</h1>
        <p class="brand-subtitle">专业的财务解决方案，助力企业数字化转型</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon><Lock /></el-icon>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据精准</span>
          </div>
          <div class="feature-item">
            <el-icon><Operation /></el-icon>
            <span>操作便捷</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrapper">
        <div class="login-form">
          <div class="form-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号和密码</p>
          </div>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="login-form-content"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>


            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <div class="account-hints">
              <p class="account-hint"><span class="hint-label">管理员：</span>admin / admin123</p>
              <p class="account-hint"><span class="hint-label">普通用户：</span>user / user123</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, DataAnalysis, Operation } from '@element-plus/icons-vue'
import { authApi } from '../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await authApi.login(form.username, form.password)
      if (res.data.code === 200) {
        // 保存token和用户信息
        const token = res.data.data?.token || 'mock-token-' + Date.now()
        const userInfo = res.data.data?.user || { username: form.username, name: '管理员' }
        
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        if (rememberMe.value) {
          localStorage.setItem('username', form.username)
        } else {
          localStorage.removeItem('username')
        }

        ElMessage.success('登录成功')
        router.push('/account')
      } else {
        ElMessage.error(res.data.message || '登录失败')
      }
    } catch (error) {
      console.error('登录错误:', error)
      ElMessage.error('登录失败，请检查网络连接')
    } finally {
      loading.value = false
    }
  })
}

// 检查是否有记住的用户名
const savedUsername = localStorage.getItem('username')
if (savedUsername) {
  form.username = savedUsername
  rememberMe.value = true
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: 
    radial-gradient(ellipse at 0% 0%, rgba(37, 99, 235, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 100% 100%, rgba(14, 165, 233, 0.1) 0%, transparent 50%),
    #0f172a;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰元素 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

/* 装饰圆圈 */
.decoration-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.3;
  animation: float 20s ease-in-out infinite;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #8b5cf6, #6366f1);
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #0ea5e9, #06b6d4);
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* 网格背景 */
.decoration-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(37, 99, 235, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(37, 99, 235, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.3;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

/* 装饰线条 */
.decoration-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(37, 99, 235, 0.3), transparent);
  height: 1px;
  animation: lineMove 15s linear infinite;
}

.line-1 {
  width: 300px;
  top: 20%;
  left: -300px;
  animation-delay: 0s;
}

.line-2 {
  width: 400px;
  top: 60%;
  right: -400px;
  animation-delay: 5s;
  transform: rotate(180deg);
}

.line-3 {
  width: 250px;
  top: 80%;
  left: -250px;
  animation-delay: 10s;
}

@keyframes lineMove {
  0% {
    transform: translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateX(calc(100vw + 400px));
    opacity: 0;
  }
}

/* 装饰点 */
.decoration-dots {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.dot {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(37, 99, 235, 0.6);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(37, 99, 235, 0.8);
  animation: dotPulse 3s ease-in-out infinite;
}

.dot-1 {
  top: 15%;
  left: 15%;
  animation-delay: 0s;
}

.dot-2 {
  top: 25%;
  right: 20%;
  animation-delay: 0.5s;
}

.dot-3 {
  bottom: 30%;
  left: 25%;
  animation-delay: 1s;
}

.dot-4 {
  bottom: 20%;
  right: 15%;
  animation-delay: 1.5s;
}

.dot-5 {
  top: 50%;
  left: 10%;
  animation-delay: 2s;
}

.dot-6 {
  top: 40%;
  right: 10%;
  animation-delay: 2.5s;
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.5);
    opacity: 1;
  }
}

.login-wrapper {
  width: 100%;
  max-width: 1100px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  display: grid;
  grid-template-columns: 1fr 1fr;
  overflow: hidden;
  min-height: 600px;
  position: relative;
  z-index: 1;
}

/* 左侧品牌区域 */
.login-brand {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.login-brand::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.2) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

.login-brand::after {
  content: '';
  position: absolute;
  bottom: -30%;
  right: -30%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 4s ease-in-out infinite;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

.brand-logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.3), rgba(14, 165, 233, 0.3));
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  position: relative;
  z-index: 1;
  box-shadow: 0 8px 32px rgba(37, 99, 235, 0.3);
}

.brand-logo svg {
  width: 48px;
  height: 48px;
  color: white;
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 1px;
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, #ffffff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 50px;
  line-height: 1.6;
  position: relative;
  z-index: 1;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  max-width: 280px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.95;
  position: relative;
  z-index: 1;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.feature-item .el-icon {
  font-size: 20px;
  color: #60a5fa;
}

/* 右侧登录表单 */
.login-form-wrapper {
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: #6b7280;
}

.login-form-content {
  margin-top: 30px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #e5e7eb inset;
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea inset;
}

:deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px #667eea inset;
}


.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #2563eb 0%, #0ea5e9 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s;
}

.login-button:hover {
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
  background: linear-gradient(135deg, #3b82f6 0%, #06b6d4 100%);
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.account-hints {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.account-hint {
  font-size: 13px;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.hint-label {
  color: #6b7280;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .login-wrapper {
    grid-template-columns: 1fr;
    max-width: 500px;
  }

  .login-brand {
    display: none;
  }

  .login-form-wrapper {
    padding: 40px 30px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }

  .login-form-wrapper {
    padding: 30px 20px;
  }

  .form-header h2 {
    font-size: 24px;
  }
}
</style>
