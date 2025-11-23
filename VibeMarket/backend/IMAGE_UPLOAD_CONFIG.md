# 图片上传配置说明

## 问题诊断

图片上传成功但无法显示，通常是由以下原因导致的：

1. **开发环境路径不匹配**
2. **Nginx配置路径错误**
3. **前端代理配置缺失**

## 配置检查清单

### 1. 本地开发环境配置

#### 后端配置（application.yml）

**Windows本地开发：**
```yaml
file:
  upload-dir: ${UPLOAD_PATH:./upload}
```

**Docker环境：**
```yaml
file:
  upload-dir: ${UPLOAD_PATH:/app/upload}
```

#### 前端配置（vite.config.js）

已添加 `/upload` 路径的代理：
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path
    },
    '/upload': {  // ✅ 已添加
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path
    }
  }
}
```

### 2. 生产环境配置（Docker）

#### Nginx配置（nginx.conf）

```nginx
location /upload/ {
    alias /app/upload/;
    autoindex off;
    expires 30d;
    add_header Cache-Control "public, immutable";
}
```

#### Docker Compose配置

确保 `backend` 和 `nginx` 共享同一个卷：
```yaml
volumes:
  - shopping-upload:/app/upload  # 两个服务都使用这个卷
```

## 路径映射关系

### 开发环境

1. **文件保存路径**：`./upload/filename.jpg`（相对于后端项目根目录）
2. **后端访问路径**：`http://localhost:8080/upload/filename.jpg`
3. **前端访问路径**：`http://localhost:3000/upload/filename.jpg` → 代理到后端

### 生产环境（Docker）

1. **文件保存路径**：`/app/upload/filename.jpg`（容器内路径）
2. **Nginx访问路径**：`http://your-domain/upload/filename.jpg`
3. **后端访问路径**：`http://backend:8080/upload/filename.jpg`（容器间通信）

## 常见问题解决

### 问题1：本地开发时图片无法显示

**原因**：前端没有配置 `/upload` 代理

**解决**：
1. ✅ 已在 `vite.config.js` 中添加 `/upload` 代理
2. 重启前端开发服务器：`npm run dev`

### 问题2：Docker环境图片无法显示

**原因**：Nginx和Backend没有共享上传目录

**解决**：
1. 检查 `docker-compose.yml` 中两个服务是否都挂载了 `shopping-upload` 卷
2. 检查 Nginx 配置中的 `alias` 路径是否正确
3. 重启容器：`docker-compose restart nginx backend`

### 问题3：上传目录权限问题

**原因**：目录没有写权限

**解决**：
```bash
# Linux/Mac
chmod -R 755 ./upload

# Windows
# 右键文件夹 -> 属性 -> 安全 -> 编辑权限
```

### 问题4：路径不匹配

**检查点**：
- ✅ WebConfig.java 中配置的路径：`/upload/**` → `file:${uploadDir}/`
- ✅ Nginx 中配置的路径：`/upload/` → `/app/upload/`
- ✅ 后端返回的URL格式：`/upload/filename.jpg`

## 测试步骤

### 1. 测试上传功能

```bash
# 使用curl测试上传
curl -X POST http://localhost:8080/api/v1/upload/image \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@test.jpg"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/upload/xxx-xxx-xxx.jpg",
    "filename": "xxx-xxx-xxx.jpg"
  }
}
```

### 2. 测试图片访问

**开发环境**：
- 直接访问：`http://localhost:8080/upload/xxx.jpg`
- 通过前端代理：`http://localhost:3000/upload/xxx.jpg`

**生产环境**：
- 通过Nginx：`http://your-domain/upload/xxx.jpg`

### 3. 检查文件是否存在

**本地开发**：
```bash
# Windows PowerShell
dir .\backend\upload\

# Linux/Mac
ls -la backend/upload/
```

**Docker环境**：
```bash
docker exec -it vibemarket-backend ls -la /app/upload/
docker exec -it vibemarket-nginx ls -la /app/upload/
```

## 配置验证清单

- [ ] 后端 `application.yml` 中 `upload-dir` 配置正确
- [ ] 前端 `vite.config.js` 中 `/upload` 代理已配置
- [ ] `WebConfig.java` 中静态资源映射正确
- [ ] Nginx 配置中 `/upload/` location 正确
- [ ] Docker Compose 中卷挂载正确
- [ ] 上传目录存在且有写权限
- [ ] 后端服务已重启（配置修改后）
- [ ] 前端服务已重启（配置修改后）

## 调试技巧

### 1. 查看后端日志

检查 `WebConfig.java` 启动时输出的路径信息：
```
上传目录已存在: /app/upload
上传目录可读: true
上传目录可写: true
静态资源路径映射: /upload/** -> file:/app/upload/
```

### 2. 查看Nginx日志

```bash
docker logs vibemarket-nginx
```

### 3. 浏览器开发者工具

1. 打开 Network 标签
2. 查看图片请求的URL和状态码
3. 如果是 404，检查路径是否正确
4. 如果是 403，检查权限配置

### 4. 检查文件系统

```bash
# 检查文件是否真的存在
docker exec -it vibemarket-backend ls -la /app/upload/

# 检查文件权限
docker exec -it vibemarket-backend stat /app/upload/filename.jpg
```

## 最佳实践

1. **统一路径格式**：始终使用 `/upload/filename.jpg` 格式
2. **环境变量**：使用环境变量区分开发和生产环境
3. **权限控制**：确保上传目录有适当的读写权限
4. **错误处理**：在前端添加图片加载失败的回退机制
5. **CDN集成**：生产环境考虑使用CDN加速图片访问

