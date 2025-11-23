# 前端访问问题排查指南

## 问题：无法访问 http://localhost:3000/

### 解决方案

#### 1. 确保前端开发服务器已启动

在 `frontend` 目录下运行：

```bash
npm run dev
```

**预期输出：**
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

#### 2. 检查端口是否被占用

**Windows PowerShell:**
```powershell
netstat -ano | findstr :3000
```

如果看到 `LISTENING` 状态，说明端口已被占用。

**解决方法：**
- 关闭占用端口的程序
- 或者修改 `vite.config.js` 中的端口号：
  ```javascript
  server: {
    port: 3001,  // 改为其他端口
  }
  ```

#### 3. 检查防火墙设置

确保 Windows 防火墙允许 Node.js 访问网络：
1. 打开"Windows Defender 防火墙"
2. 点击"允许应用通过防火墙"
3. 找到 Node.js，确保已勾选

#### 4. 清除缓存并重新启动

```bash
# 停止当前运行的服务器（Ctrl+C）
# 清除缓存
npm cache clean --force

# 删除 node_modules 和重新安装（如果问题持续）
rm -rf node_modules package-lock.json
npm install

# 重新启动
npm run dev
```

#### 5. 检查浏览器控制台

打开浏览器开发者工具（F12），查看：
- **Console** 标签：是否有 JavaScript 错误
- **Network** 标签：请求是否成功发送

#### 6. 尝试使用 127.0.0.1 代替 localhost

有时 `localhost` 解析有问题，尝试访问：
- http://127.0.0.1:3000

#### 7. 检查 hosts 文件

确保 `C:\Windows\System32\drivers\etc\hosts` 文件中没有将 `localhost` 重定向到其他地址。

---

## 关于 CORS 错误（联想广告 API）

**错误信息：**
```
Access to fetch at 'https://dsp.lenovo.com.cn/...' from origin 'null' has been blocked by CORS policy
```

**原因：**
这是浏览器扩展或第三方脚本尝试访问联想广告 API 导致的，**不影响你的项目功能**。

**解决方法：**
1. **忽略此错误** - 它不会影响你的应用运行
2. **禁用浏览器扩展** - 检查是否有广告拦截器或其他扩展在运行
3. **使用无痕模式** - 在无痕模式下测试，排除扩展干扰

---

## 完整启动流程

### 第一步：启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

验证后端启动成功：
- 访问 http://localhost:8080/api/v1/categories

### 第二步：启动前端（端口 3000）

```bash
cd frontend
npm run dev
```

验证前端启动成功：
- 访问 http://localhost:3000
- 应该能看到登录/注册页面

### 第三步：检查代理配置

确保 `frontend/vite.config.js` 中的代理配置正确：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端地址
      changeOrigin: true,
      rewrite: (path) => path
    }
  }
}
```

---

## 常见错误及解决

### 错误：`Cannot GET /`

**原因：** 路由配置问题或服务器未正确启动

**解决：** 
- 检查 `src/router/index.js` 路由配置
- 确保有默认路由（如 `/` 指向首页）

### 错误：`Network Error` 或 `ERR_CONNECTION_REFUSED`

**原因：** 后端服务未启动或端口不对

**解决：**
- 确认后端在 8080 端口运行
- 检查 `vite.config.js` 中的 `target` 地址

### 错误：`Port 3000 is already in use`

**原因：** 3000 端口被其他程序占用

**解决：**
```bash
# Windows 查找占用端口的进程
netstat -ano | findstr :3000

# 结束进程（替换 PID 为实际进程ID）
taskkill /PID <PID> /F

# 或修改端口
# 编辑 vite.config.js，修改 port: 3001
```

---

## 如果问题仍然存在

1. **检查 Node.js 版本**
   ```bash
   node -v
   ```
   建议使用 Node.js 16+ 或 18+

2. **检查 npm 版本**
   ```bash
   npm -v
   ```

3. **查看完整错误日志**
   - 在终端查看 `npm run dev` 的完整输出
   - 在浏览器控制台查看详细错误

4. **重新安装依赖**
   ```bash
   cd frontend
   rm -rf node_modules package-lock.json
   npm install
   npm run dev
   ```

---

## 联系支持

如果以上方法都无法解决问题，请提供：
1. 完整的错误信息（终端和浏览器控制台）
2. Node.js 和 npm 版本
3. 操作系统版本
4. 具体的操作步骤

