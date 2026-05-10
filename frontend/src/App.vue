<template>
  <div class="app-container" :class="{ 'login-page': $route.path === '/login' }">
    <!-- 登录页直接显示，不显示侧边栏和顶部栏 -->
    <router-view v-if="$route.path === '/login'" />
    
    <!-- 主应用布局 -->
    <template v-else>
    <!-- 左侧菜单 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <transition name="fade">
          <div class="logo-text" v-show="!isCollapsed">
            <span class="brand">金蝶云</span>
            <span class="sub">财务管理系统</span>
          </div>
        </transition>
      </div>

      <nav class="nav-menu">
        <div class="nav-section">
          <div class="nav-section-title" v-show="!isCollapsed">基础设置</div>
          <router-link to="/account" class="nav-item" :class="{ active: $route.path === '/account' }">
            <div class="nav-icon">
              <el-icon><Folder /></el-icon>
            </div>
            <span class="nav-label" v-show="!isCollapsed">会计科目</span>
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-title" v-show="!isCollapsed">日常业务</div>
          <router-link to="/voucher" class="nav-item" :class="{ active: $route.path === '/voucher' }">
            <div class="nav-icon">
              <el-icon><Document /></el-icon>
            </div>
            <span class="nav-label" v-show="!isCollapsed">凭证管理</span>
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-title" v-show="!isCollapsed">报表查询</div>
          <router-link to="/ledger" class="nav-item" :class="{ active: $route.path === '/ledger' }">
            <div class="nav-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <span class="nav-label" v-show="!isCollapsed">账簿查询</span>
          </router-link>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
          <el-icon :class="{ rotated: isCollapsed }"><DArrowLeft /></el-icon>
        </div>
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-wrapper">
      <!-- 顶部栏 -->
      <header class="top-header">
        <div class="header-left">
          <div class="breadcrumb">
            <span class="section">{{ pageConfig.section }}</span>
            <el-icon><ArrowRight /></el-icon>
            <span class="current">{{ pageConfig.name }}</span>
          </div>
        </div>
        <div class="header-right">
          <div class="divider"></div>
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">
                <el-icon :size="18"><User /></el-icon>
              </el-avatar>
              <div class="user-detail">
                <span class="user-name">{{ userInfo.name }}</span>
                <span class="user-role">{{ userInfo.role }}</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 主内容区 -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Folder, Document, TrendCharts, Bell, Setting, User, ArrowDown, SwitchButton, DArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapsed = ref(false)
const userInfo = ref({ name: '用户', role: '普通用户' })

const pageConfig = computed(() => {
  const config = {
    '/account': { section: '基础设置', name: '会计科目' },
    '/voucher': { section: '日常业务', name: '凭证管理' },
    '/ledger': { section: '报表查询', name: '账簿查询' }
  }
  return config[route.path] || { section: '首页', name: '概览' }
})

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // 用户取消
  }
}

// 加载用户信息
onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      const user = JSON.parse(storedUserInfo)
      userInfo.value = {
        name: user.name || user.username || '用户',
        role: user.role || '普通用户'
      }
    } catch (e) {
      console.error('解析用户信息失败', e)
    }
  }
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

:root {
  --primary-color: #2563eb;
  --primary-light: #3b82f6;
  --primary-dark: #1d4ed8;
  --accent-color: #0ea5e9;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;
  --bg-primary: #0f172a;
  --bg-secondary: #1e293b;
  --bg-tertiary: #334155;
  --bg-card: rgba(30, 41, 59, 0.8);
  --bg-sidebar: #0c1222;
  --text-primary: #f1f5f9;
  --text-secondary: #94a3b8;
  --text-muted: #64748b;
  --border-color: rgba(148, 163, 184, 0.1);
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.3);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --sidebar-width: 240px;
  --sidebar-collapsed: 72px;
  --header-height: 64px;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(15, 23, 42, 0.05);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.4), rgba(14, 165, 233, 0.4));
  border-radius: 4px;
  transition: background 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.7), rgba(14, 165, 233, 0.7));
}

::-webkit-scrollbar-corner {
  background: transparent;
}

/* 深色背景区域滚动条 */
.sidebar ::-webkit-scrollbar-track,
.nav-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.sidebar ::-webkit-scrollbar-thumb,
.nav-menu::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.3);
}

.sidebar ::-webkit-scrollbar-thumb:hover,
.nav-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.5);
}

/* Firefox 滚动条样式 */
* {
  scrollbar-width: thin;
  scrollbar-color: rgba(37, 99, 235, 0.4) rgba(15, 23, 42, 0.05);
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: var(--bg-primary);
  color: var(--text-primary);
  min-height: 100vh;
}

.app-container {
  min-height: 100vh;

}

.app-container:not(.login-page) {
  display: flex;
  background: 
    radial-gradient(ellipse at 0% 0%, rgba(37, 99, 235, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 100% 100%, rgba(14, 165, 233, 0.05) 0%, transparent 50%),
    var(--bg-primary);
}

/* 左侧菜单 */
.sidebar {
  width: var(--sidebar-width);
  background: var(--bg-sidebar);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar.collapsed {
  width: var(--sidebar-collapsed);
}

.logo {
  height: 72px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--border-color);
}

.logo-icon {
  min-width: 40px;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon svg {
  width: 22px;
  height: 22px;
}

.logo-text {
  display: flex;
  flex-direction: column;
  white-space: nowrap;
}

.logo-text .brand {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(90deg, var(--primary-light), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-text .sub {
  font-size: 11px;
  color: var(--text-muted);
  letter-spacing: 0.5px;
}

.nav-menu {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 24px;
}

.nav-section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 12px;
  margin-bottom: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  margin-bottom: 4px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.nav-item.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.nav-icon {
  min-width: 24px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  white-space: nowrap;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--border-color);
}

.collapse-btn {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.collapse-btn .el-icon {
  transition: transform 0.3s ease;
}

.collapse-btn .el-icon.rotated {
  transform: rotate(180deg);
}

/* 右侧主区域 */
.main-wrapper {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s ease;
  width: calc(100% - var(--sidebar-width));
  max-width: calc(100% - var(--sidebar-width));
}

.sidebar.collapsed ~ .main-wrapper {
  margin-left: var(--sidebar-collapsed);
  width: calc(100% - var(--sidebar-collapsed));
  max-width: calc(100% - var(--sidebar-collapsed));
}

/* 顶部栏 */
.top-header {
  height: var(--header-height);
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb .section {
  color: var(--text-muted);
}

.breadcrumb .el-icon {
  color: var(--text-muted);
  font-size: 12px;
}

.breadcrumb .current {
  color: var(--text-muted);
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.divider {
  width: 1px;
  height: 32px;
  background: #e5e7eb;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px 6px 6px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.user-role {
  font-size: 11px;
  color: #6b7280;
}

.dropdown-icon {
  color: #6b7280;
  font-size: 12px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  padding: 24px;
  background: #ffffff;
  overflow-x: auto;
  width: 100%;
  min-width: 0;
}

/* 动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* Element Plus 主题覆盖 */
.el-button--primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark)) !important;
  border: none !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color)) !important;
}

.el-dialog {
  background: #fff !important;
  border-radius: var(--radius-lg) !important;
  border: 1px solid #e5e7eb !important;
}

.el-dialog__header {
  border-bottom: 1px solid #e5e7eb !important;
  padding: 20px 24px !important;
}

.el-dialog__title {
  color: #1f2937 !important;
  font-weight: 600 !important;
}

.el-dialog__body {
  padding: 24px !important;
  color: #374151 !important;
}

.el-dialog__footer {
  border-top: 1px solid #e5e7eb !important;
  padding: 16px 24px !important;
}

.el-form-item__label {
  color: #374151 !important;
}

.el-input__wrapper,
.el-select__wrapper,
.el-textarea__inner {
  background: #fff !important;
  border: 1px solid #d1d5db !important;
  box-shadow: none !important;
}

.el-input__inner,
.el-select__input,
.el-textarea__inner {
  color: #1f2937 !important;
}

.el-input__wrapper:hover,
.el-select__wrapper:hover {
  border-color: var(--primary-light) !important;
}

.el-input__wrapper.is-focus,
.el-select__wrapper.is-focus {
  border-color: var(--primary-color) !important;
}

.el-select-dropdown {
  background: #fff !important;
  border: 1px solid #e5e7eb !important;
}

.el-select-dropdown__item {
  color: #374151 !important;
}

.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover {
  background: #f3f4f6 !important;
}

.el-select-dropdown__item.selected {
  color: var(--primary-color) !important;
  font-weight: 600 !important;
}

.el-dropdown-menu {
  background: #fff !important;
  border: 1px solid #e5e7eb !important;
}

.el-dropdown-menu__item {
  color: #374151 !important;
}

.el-dropdown-menu__item:hover {
  background: #f3f4f6 !important;
  color: #1f2937 !important;
}

.el-message-box {
  background: #fff !important;
  border: 1px solid #e5e7eb !important;
}

.el-message-box__title {
  color: #1f2937 !important;
}

.el-message-box__content {
  color: #374151 !important;
}

.el-picker__popper {
  background: #fff !important;
  border: 1px solid #e5e7eb !important;
}

.el-date-picker__header-label {
  color: #1f2937 !important;
}

.el-picker-panel {
  background: #fff !important;
  color: #374151 !important;
}

.el-date-table td.available:hover {
  color: var(--primary-color) !important;
}

.el-month-table td.current:not(.disabled) .cell,
.el-date-table td.current:not(.disabled) .cell {
  background: var(--primary-color) !important;
}

/* 自定义 Loading 动画样式 */
.el-loading-mask {
  background-color: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(2px);
}

.el-loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.el-loading-spinner .circular {
  width: 42px !important;
  height: 42px !important;
  animation: loading-rotate 1.5s linear infinite;
}

.el-loading-spinner .path {
  stroke: var(--primary-color) !important;
  stroke-width: 3;
  animation: loading-dash 1.5s ease-in-out infinite;
  stroke-linecap: round;
}

@keyframes loading-rotate {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes loading-dash {
  0% {
    stroke-dasharray: 1, 200;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 200;
    stroke-dashoffset: -35px;
  }
  100% {
    stroke-dasharray: 90, 200;
    stroke-dashoffset: -125px;
  }
}

.el-loading-spinner .el-loading-text {
  color: var(--primary-color) !important;
  font-size: 14px;
  font-weight: 500;
  margin-top: 12px;
  letter-spacing: 0.5px;
}

/* 表格 loading 时的骨架屏效果 */
.el-table.is-loading::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(37, 99, 235, 0.05),
    transparent
  );
  animation: shimmer 1.5s infinite;
  z-index: 1;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* 移动端响应式 - 菜单只显示图标 */
@media (max-width: 768px) {
  :root {
    --sidebar-width: 72px;
  }
  
  .sidebar {
    width: 72px !important;
  }
  
  .logo {
    padding: 0;
    justify-content: center;
  }
  
  .logo-text {
    display: none !important;
  }
  
  .nav-section-title {
    display: none !important;
  }
  
  .nav-label {
    display: none !important;
  }
  
  .nav-item {
    justify-content: center;
    padding: 12px 8px;
  }
  
  .nav-icon {
    margin: 0;
  }
  
  .sidebar-footer {
    display: none;
  }
  
  .main-wrapper {
    margin-left: 72px !important;
    width: calc(100% - 72px) !important;
    max-width: calc(100% - 72px) !important;
  }
  
  .top-header {
    padding: 0 12px;
  }
  
  .user-detail {
    display: none;
  }
  
  .dropdown-icon {
    display: none;
  }
  
  .user-info {
    padding: 4px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .breadcrumb .section {
    display: none;
  }
  
  .breadcrumb .el-icon {
    display: none;
  }
}
</style>
