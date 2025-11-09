# VibeMarket 后端服务

## 技术栈

- Spring Boot 2.7.14
- MyBatis
- MySQL 8.0
- Redis
- JWT

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/shopping/
│   │   │   ├── common/          # 公共模块
│   │   │   ├── user/            # 用户模块
│   │   │   ├── product/         # 商品模块
│   │   │   ├── cart/             # 购物车模块
│   │   │   ├── order/            # 订单模块
│   │   │   └── admin/            # 管理员模块
│   │   └── resources/
│   │       ├── mapper/           # MyBatis映射文件
│   │       └── application.yml # 配置文件
│   └── test/                     # 测试代码
├── pom.xml                      # Maven配置
└── Dockerfile                   # Docker构建文件
```

## 运行项目

### 前置要求

- JDK 17（推荐安装在 D:\javajdk）
- Maven 3.6+
- MySQL 8.0

### 使用 Maven

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 使用 Docker

```bash
docker build -t vibemarket-backend .
docker run -p 8080:8080 vibemarket-backend
```

### 配置本地JDK

1. 设置环境变量 `JAVA_HOME=D:\javajdk`
2. 在 `Path` 中添加 `%JAVA_HOME%\bin`
3. 验证：`java -version` 应显示 JDK 17

详细配置请参考 [DEVELOPMENT.md](./DEVELOPMENT.md)

## API 接口

### 用户相关

- `POST /api/v1/auth/register` - 用户注册
- `POST /api/v1/auth/login` - 用户登录
- `POST /api/v1/auth/logout` - 用户登出
- `GET /api/v1/auth/profile` - 获取用户信息

### 商品相关

- `GET /api/v1/products` - 获取商品列表
- `GET /api/v1/products/{id}` - 获取商品详情
- `GET /api/v1/products/search` - 搜索商品

### 购物车相关

- `GET /api/v1/carts` - 获取购物车列表
- `POST /api/v1/carts` - 添加商品到购物车
- `PUT /api/v1/carts/{id}` - 更新购物车
- `DELETE /api/v1/carts/{id}` - 删除购物车商品

### 订单相关

- `POST /api/v1/orders` - 创建订单
- `GET /api/v1/orders` - 获取订单列表
- `GET /api/v1/orders/{id}` - 获取订单详情
- `PUT /api/v1/orders/{id}/cancel` - 取消订单
- `PUT /api/v1/orders/{id}/confirm` - 确认收货

## 配置说明

主要配置在 `application.yml` 中：

- 数据库连接配置
- Redis 配置
- JWT 配置
- 文件上传配置

## 开发说明

1. 使用 MyBatis 进行数据库操作
2. 使用 JWT 进行身份认证
3. 使用统一响应格式 `ResponseResult`
4. 使用全局异常处理 `GlobalExceptionHandler`

