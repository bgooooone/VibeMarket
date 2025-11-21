import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './styles/main.scss'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 应用启动时，如果已登录，自动获取用户信息
const userStore = useUserStore()
if (userStore.isLoggedIn && !userStore.userInfo) {
  userStore.getUserInfo().catch(() => {
    // 如果获取失败，可能是token过期，清除token
    userStore.logout()
  })
}

app.mount('#app')

