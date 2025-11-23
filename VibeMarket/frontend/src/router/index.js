import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/product/ProductList.vue')
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/ProductDetail.vue')
  },
  {
    path: '/user',
    component: () => import('@/views/user/UserLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'addresses',
        name: 'UserAddresses',
        component: () => import('@/views/user/Addresses.vue')
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/order/UserOrders.vue')
      }
    ]
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/cart/Cart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/cart/Checkout.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const adminStore = useAdminStore()
  
  // 用户登录页面：如果管理员已登录，先退出管理员登录
  if (to.name === 'Login' || to.name === 'Register') {
    if (adminStore.isLoggedIn) {
      await adminStore.logout()
      ElMessage.warning('已退出管理员登录')
    }
    // 如果用户已登录，重定向到首页
    if (userStore.isLoggedIn) {
      const redirect = to.query.redirect
      next({ 
        name: redirect ? undefined : 'Home',
        path: redirect || undefined
      })
      return
    }
  }
  
  // 管理员登录页面：如果用户已登录，先退出用户登录
  if (to.name === 'AdminLogin') {
    if (userStore.isLoggedIn) {
      await userStore.logout()
      ElMessage.warning('已退出用户登录')
    }
    // 如果管理员已登录，跳转到首页
    if (adminStore.isLoggedIn) {
      next({ name: 'Home' })
      return
    }
  }
  
  // 管理员路由检查
  if (to.meta.requiresAdmin) {
    if (!adminStore.isLoggedIn) {
      // 保存重定向路径
      next({ 
        name: 'AdminLogin', 
        query: { redirect: to.fullPath } 
      })
    } else {
      // 验证token有效性（可选，避免频繁请求）
      next()
    }
  }
  // 普通用户路由检查
  else if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      // 保存重定向路径
      next({ 
        name: 'Login', 
        query: { redirect: to.fullPath } 
      })
    } else {
      // 验证token有效性（可选，避免频繁请求）
      next()
    }
  } else {
    next()
  }
})

export default router

