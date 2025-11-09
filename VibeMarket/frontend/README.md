# VibeMarket 前端项目

## 技术栈

- Vue 3
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API 请求封装
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   ├── styles/           # 样式文件
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── public/               # 公共静态文件
├── package.json          # 项目配置
├── vite.config.js        # Vite 配置
└── Dockerfile            # Docker 构建文件
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

开发服务器将在 http://localhost:3000 启动

## 构建

```bash
npm run build
```

构建产物在 `dist/` 目录

## 预览构建结果

```bash
npm run preview
```

## 主要功能

- ✅ 用户注册/登录
- ✅ 商品浏览/搜索
- ✅ 购物车管理
- ✅ 订单管理
- ✅ 用户中心
- ✅ 管理员后台

## 路由说明

- `/` - 首页
- `/login` - 登录页
- `/register` - 注册页
- `/products` - 商品列表
- `/products/:id` - 商品详情
- `/cart` - 购物车
- `/checkout` - 结算页
- `/user/*` - 用户中心
- `/admin/*` - 管理员后台

## API 请求

所有 API 请求通过 `src/api/request.js` 统一封装，支持：
- 自动添加 JWT Token
- 统一错误处理
- 请求/响应拦截

## 状态管理

使用 Pinia 进行状态管理，主要 Store：
- `user` - 用户信息和认证状态

## 样式

- 使用 Element Plus 组件库
- 支持响应式设计
- 自定义样式在 `src/styles/` 目录

