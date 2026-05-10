import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { 
    path: '/login', 
    name: 'Login', 
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  { 
    path: '/', 
    redirect: '/voucher',
    meta: { requiresAuth: true }
  },
  { 
    path: '/account', 
    name: 'Account', 
    component: () => import('../views/Account.vue'), 
    meta: { title: '会计科目', requiresAuth: true } 
  },
  { 
    path: '/voucher', 
    name: 'Voucher', 
    component: () => import('../views/Voucher.vue'), 
    meta: { title: '凭证管理', requiresAuth: true } 
  },
  { 
    path: '/ledger', 
    name: 'Ledger', 
    component: () => import('../views/Ledger.vue'), 
    meta: { title: '账簿查询', requiresAuth: true } 
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录访问登录页，跳转到account页面
    next('/account')
  } else {
    next()
  }
})

export default router
