# 快速开始指南

## 项目结构

```
VibeMarket/
├── backend/              # Spring Boot 后端项目
├── frontend/             # Vue 3 前端项目
├── sql/                  # 数据库初始化脚本
├── nginx/                # Nginx 配置文件
├── docker-compose.yml    # Docker 编排文件
└── README.md            # 项目说明
```

## 快速启动（使用 Docker Compose）

### 1. 确保已安装 Docker 和 Docker Compose

```bash
docker --version
docker-compose --version
```

### 2. 启动所有服务

```bash
docker-compose up -d
```

这将启动以下服务：
- MySQL 数据库（端口 3306）
- Redis 缓存（端口 6379）
- 后端服务（端口 8080）
- Nginx 前端服务（端口 80）

### 3. 访问系统

- 前端页面：http://localhost
- 后端API：http://localhost/api
- 数据库：localhost:3306

### 4. 默认账号

- 管理员账号：admin
- 管理员密码：admin123

## 本地开发

### 后端开发

1. 进入后端目录
```bash
cd backend
```

2. 配置数据库连接
编辑 `src/main/resources/application.yml`，修改数据库连接信息

3. 运行项目
```bash
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端开发

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 数据库初始化

数据库会在 Docker 容器启动时自动初始化。如果需要手动初始化：

```bash
# 进入 MySQL 容器
docker exec -it vibemarket-mysql bash

# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source /docker-entrypoint-initdb.d/init.sql
```

## 常见问题

### 1. 端口被占用

如果端口被占用，可以修改 `docker-compose.yml` 中的端口映射

### 2. 数据库连接失败

检查 MySQL 容器是否正常运行：
```bash
docker ps
docker logs vibemarket-mysql
```

### 3. 前端无法访问后端API

检查 Nginx 配置和代理设置，确保 `/api` 路径正确代理到后端服务

## 停止服务

```bash
docker-compose down
```

## 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

## 重新构建

```bash
# 停止并删除容器
docker-compose down

# 重新构建并启动
docker-compose up -d --build
```

## 下一步

1. 完善业务功能代码
2. 添加更多API接口
3. 完善前端页面交互
4. 添加单元测试
5. 配置生产环境

