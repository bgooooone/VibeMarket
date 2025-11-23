# 令牌和守卫功能实现说明

## 功能概述

已实现完整的JWT令牌认证和路由守卫功能，确保只有登录用户才能访问需要认证的页面和接口。

## 后端实现

### 1. JWT工具类 (`JwtUtils.java`)

**功能：**
- 生成用户token：`generateToken(userId, username)`
- 生成管理员token：`generateAdminToken(adminId, username)`
- 解析token：`parseToken(token)`
- 验证token是否过期：`isTokenExpired(token)`

**配置：**
- 密钥：`jwt.secret`（从application.yml读取）
- 过期时间：`jwt.expiration`（默认3600秒，1小时）

### 2. 认证拦截器 (`AuthInterceptor.java`)

**功能：**
- 拦截所有 `/api/**` 路径的请求
- 检查方法参数中的 `@CurrentUser` 或 `@CurrentAdmin` 注解
- 验证JWT token的有效性
- 验证用户/管理员状态（是否被禁用）
- 将用户/管理员信息存入request属性

**排除路径（无需认证）：**
- `/api/v1/auth/login` - 用户登录
- `/api/v1/auth/register` - 用户注册
- `/api/v1/categories` - 分类列表
- `/api/v1/products` - 商品列表
- `/api/v1/products/**` - 商品详情
- `/api/v1/admin/auth/login` - 管理员登录

**错误处理：**
- 401：未登录或token已过期
- 401：用户/管理员不存在或已被禁用
- 401：token解析失败
- 403：权限不足（用户token访问管理员接口或反之）

### 3. 参数解析器 (`CurrentUserMethodArgumentResolver.java`)

**功能：**
- 自动将request中的用户/管理员信息注入到控制器方法参数
- 支持 `@CurrentUser` 和 `@CurrentAdmin` 注解

**使用示例：**
```java
@GetMapping("/profile")
public ResponseResult<User> getProfile(@CurrentUser User currentUser) {
    // currentUser 已自动注入
    return ResponseResult.success(currentUser);
}
```

## 前端实现

### 1. 路由守卫 (`router/index.js`)

**功能：**
- 在路由跳转前检查用户登录状态
- 保护需要认证的路由
- 处理登录后的重定向

**路由元信息：**
- `requiresAuth: true` - 需要用户登录
- `requiresAdmin: true` - 需要管理员登录
- `requiresAuth: false` - 无需认证（登录/注册页）

**保护的路由：**
- `/user/*` - 用户中心相关页面
- `/cart` - 购物车
- `/checkout` - 结算页面
- `/admin/*` - 管理员后台

**重定向逻辑：**
1. 未登录用户访问受保护路由 → 重定向到登录页，保存原始路径
2. 已登录用户访问登录页 → 重定向到首页或原始路径
3. 登录成功后 → 跳转到保存的原始路径或默认路径

### 2. API请求拦截器 (`api/request.js`)

**请求拦截器：**
- 自动添加Authorization头（Bearer token）
- 管理员接口优先使用管理员token
- 其他接口使用用户token

**响应拦截器：**
- 处理401错误：清除登录状态，重定向到登录页
- 区分用户和管理员的401处理
- 处理403权限错误
- 统一错误提示

### 3. 状态管理 (Pinia Stores)

#### 用户Store (`stores/user.js`)

**状态：**
- `token` - JWT令牌
- `userInfo` - 用户信息

**方法：**
- `login(loginData)` - 登录，保存token
- `register(registerData)` - 注册
- `getUserInfo()` - 获取用户信息
- `validateToken()` - 验证token有效性
- `logout()` - 登出，清除token和信息

**Getters：**
- `isLoggedIn` - 是否已登录（检查token是否存在）

#### 管理员Store (`stores/admin.js`)

**状态：**
- `token` - JWT令牌
- `adminInfo` - 管理员信息

**方法：**
- `login(loginData)` - 登录，保存token
- `getAdminInfo()` - 获取管理员信息
- `validateToken()` - 验证token有效性
- `logout()` - 登出，清除token和信息

**Getters：**
- `isLoggedIn` - 是否已登录（检查token是否存在）

## 使用指南

### 后端：保护接口

**1. 在控制器方法参数中添加注解：**

```java
@GetMapping("/profile")
public ResponseResult<User> getProfile(@CurrentUser User currentUser) {
    // 此接口需要用户登录
    return ResponseResult.success(currentUser);
}

@PostMapping("/upload/image")
public ResponseResult<String> uploadImage(
    @CurrentAdmin Admin currentAdmin,
    @RequestParam("file") MultipartFile file
) {
    // 此接口需要管理员登录
    return ResponseResult.success("上传成功");
}
```

**2. 无需认证的接口：**
- 不需要添加 `@CurrentUser` 或 `@CurrentAdmin` 注解
- 确保路径在 `WebConfig` 的排除列表中

### 前端：保护页面

**1. 在路由配置中添加meta：**

```javascript
{
  path: '/cart',
  name: 'Cart',
  component: () => import('@/views/cart/Cart.vue'),
  meta: { requiresAuth: true }  // 需要登录
}
```

**2. 在组件中检查登录状态：**

```javascript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

if (!userStore.isLoggedIn) {
  router.push('/login')
}
```

**3. 在API调用中：**
- Token会自动通过请求拦截器添加
- 无需手动处理

## 安全特性

### 1. Token过期处理
- 后端验证token是否过期
- 前端收到401后自动清除登录状态
- 自动重定向到登录页

### 2. 用户状态验证
- 检查用户/管理员是否被禁用
- 禁用账户无法访问受保护资源

### 3. 权限分离
- 用户token和管理员token分离
- 用户无法访问管理员接口
- 管理员无法使用用户token

### 4. 自动登出
- Token过期自动登出
- 401错误自动清除登录状态
- 防止无效token继续使用

## 测试场景

### 1. 未登录访问受保护页面
- 访问 `/cart` → 自动跳转到 `/login?redirect=/cart`
- 登录成功后 → 自动跳转回 `/cart`

### 2. Token过期
- 访问需要认证的接口 → 返回401
- 前端自动清除token → 跳转到登录页

### 3. 用户被禁用
- 登录后用户被禁用
- 访问接口 → 返回401 "用户不存在或已被禁用"

### 4. 权限错误
- 使用用户token访问管理员接口 → 返回403 "权限不足"

## 配置说明

### 后端配置 (`application.yml`)

```yaml
jwt:
  secret: your-secret-key-here  # JWT密钥
  expiration: 3600  # Token过期时间（秒）
```

### 前端配置

**无需额外配置**，所有功能已自动集成。

## 常见问题

### Q1: 为什么登录后还是无法访问受保护页面？

**A:** 检查以下几点：
1. Token是否正确保存到localStorage
2. 路由meta是否正确设置 `requiresAuth: true`
3. Store中的 `isLoggedIn` getter是否正确

### Q2: Token过期后如何处理？

**A:** 
- 后端会返回401错误
- 前端拦截器会自动清除token并跳转到登录页
- 用户重新登录即可

### Q3: 如何延长Token过期时间？

**A:** 修改 `application.yml` 中的 `jwt.expiration` 值（单位：秒）

### Q4: 如何实现Token刷新？

**A:** 当前实现中，Token过期需要重新登录。如需实现刷新机制，可以：
1. 添加刷新token接口
2. 在响应拦截器中检测401错误
3. 尝试使用刷新token获取新token
4. 如果刷新失败，则跳转到登录页

## 总结

✅ **已实现功能：**
- JWT令牌生成和验证
- 后端接口认证拦截
- 前端路由守卫
- 自动token管理
- 错误处理和重定向
- 用户/管理员权限分离

✅ **安全特性：**
- Token过期验证
- 用户状态检查
- 权限分离
- 自动登出

✅ **用户体验：**
- 登录后自动重定向
- 友好的错误提示
- 无缝的认证流程

