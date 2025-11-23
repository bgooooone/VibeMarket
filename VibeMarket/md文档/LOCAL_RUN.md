# 本地运行指南（不使用Docker）

本项目完全支持在本地环境运行，无需Docker。以下是详细的运行步骤。

## 环境要求

### 必需环境
- **JDK 17**（已安装在 D:\javajdk）
- **Node.js 14+** 和 npm
- **MySQL 8.0**
- **Maven 3.6+**（可选，项目已包含Maven Wrapper）

### 可选环境
- **Redis 6.0+**（可选，如果不需要缓存功能可以暂时不用）

## 第一步：配置JDK环境

### Windows环境变量配置

1. 设置 `JAVA_HOME` 环境变量：
   - 打开"系统属性" -> "高级" -> "环境变量"
   - 新建系统变量：
     - 变量名：`JAVA_HOME`
     - 变量值：`D:\javajdk`
   - 或者在命令行运行（需要管理员权限）：
     ```cmd
     setx JAVA_HOME "D:\javajdk" /M
     ```

2. 添加到 PATH：
   - 编辑系统变量 `Path`
   - 添加：`%JAVA_HOME%\bin`
   - 或者在命令行运行：
     ```cmd
     setx PATH "%PATH%;%JAVA_HOME%\bin" /M
     ```

3. 验证配置：
   ```cmd
   java -version
   ```
   应该显示 JDK 17 的版本信息

### 快速配置（使用脚本）

以管理员身份运行项目根目录下的 `setup-jdk.bat` 脚本。

## 第二步：配置和启动MySQL

### 1. 确保MySQL服务运行

```cmd
# 检查MySQL服务状态
net start MySQL80
# 或者通过服务管理器启动MySQL服务
```

### 2. 创建数据库

```sql
CREATE DATABASE shopping_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 执行初始化脚本

```cmd
mysql -u root -p shopping_mall < sql/init.sql
```

或者使用MySQL客户端工具（如Navicat、MySQL Workbench）执行 `sql/init.sql` 文件。

### 4. 修改后端数据库配置

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping_mall?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码  # 修改为你的实际密码
```

## 第三步：启动后端服务

### 方式一：使用Maven Wrapper（推荐）

```cmd
cd backend
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### 方式二：使用Maven（如果已安装）

```cmd
cd backend
mvn clean install
mvn spring-boot:run
```

### 方式三：使用IDE

1. 在IDE（IntelliJ IDEA / Eclipse）中打开 `backend` 目录
2. 配置项目SDK为JDK 17
3. 找到 `ShoppingApplication.java`
4. 右键 -> Run 'ShoppingApplication'

### 验证后端启动

后端启动成功后，访问：
- http://localhost:8080/api/v1/categories（应该返回空数组或分类数据）

## 第四步：启动前端服务

### 1. 安装依赖

```cmd
cd frontend
npm install
```

如果下载慢，可以使用国内镜像：
```cmd
npm install --registry=https://registry.npmmirror.com
```

### 2. 启动开发服务器

```cmd
npm run dev
```

### 3. 访问前端

前端启动后，浏览器访问：
- http://localhost:3000

## 第五步：配置前端API地址（可选）

如果后端运行在不同端口，需要修改前端配置：

编辑 `frontend/vite.config.js`：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 修改为你的后端地址
      changeOrigin: true,
      rewrite: (path) => path
    }
  }
}
```

## 运行顺序

1. **MySQL** - 确保数据库服务运行
2. **后端** - 启动Spring Boot应用（端口8080）
3. **前端** - 启动Vue开发服务器（端口3000）

## 常见问题

### 1. 后端启动失败 - 数据库连接错误

**问题**：`Communications link failure` 或 `Access denied`

**解决**：
- 检查MySQL服务是否运行
- 验证数据库用户名和密码是否正确
- 确认数据库 `shopping_mall` 已创建
- 检查防火墙是否阻止了3306端口

### 2. 后端启动失败 - 端口被占用

**问题**：`Port 8080 is already in use`

**解决**：
- 修改 `application.yml` 中的端口：
  ```yaml
  server:
    port: 8081  # 改为其他端口
  ```
- 或者关闭占用8080端口的程序

### 3. 前端启动失败 - 依赖安装错误

**问题**：`npm ERR!` 相关错误

**解决**：
- 清除缓存：`npm cache clean --force`
- 删除 `node_modules` 和 `package-lock.json`，重新安装
- 使用国内镜像：`npm install --registry=https://registry.npmmirror.com`

### 4. 前端无法访问后端API

**问题**：前端页面显示网络错误或404

**解决**：
- 确认后端服务已启动（访问 http://localhost:8080/api/v1/categories 测试）
- 检查 `vite.config.js` 中的代理配置
- 检查浏览器控制台的错误信息

### 5. JDK版本错误

**问题**：`UnsupportedClassVersionError` 或版本不匹配

**解决**：
- 确认 `JAVA_HOME` 指向 JDK 17
- 在IDE中配置项目使用JDK 17
- 运行 `java -version` 确认版本

## 开发模式 vs 生产模式

### 开发模式（当前方式）

- 前端：使用 `npm run dev`，支持热更新
- 后端：使用 `mvn spring-boot:run`，支持热部署（需要配置）
- 优点：开发方便，修改代码立即生效
- 缺点：需要同时运行多个服务

### 生产模式（使用Docker）

- 使用 `docker-compose up -d` 一键启动所有服务
- 优点：环境一致，部署简单
- 缺点：需要安装Docker

## 性能优化建议

1. **数据库连接池**：已在 `application.yml` 中配置HikariCP
2. **Redis缓存**：如果需要，可以安装Redis并配置（可选）
3. **前端构建**：生产环境使用 `npm run build` 构建优化版本

## 下一步

- 完善业务功能代码
- 添加更多API接口
- 编写单元测试
- 配置生产环境

## 总结

✅ **完全可以在不使用Docker的情况下运行项目**

只需要：
1. 配置JDK 17环境
2. 安装并启动MySQL
3. 启动后端服务
4. 启动前端服务

所有步骤都可以在本地完成，无需Docker！

