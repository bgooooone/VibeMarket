# 本地开发指南

## 环境要求

- JDK 17（已安装在 D:\javajdk）
- Maven 3.6+
- MySQL 8.0
- Redis 6.0+（可选）

## 配置本地JDK

### Windows 环境变量配置

1. 打开系统环境变量设置
2. 添加或编辑 `JAVA_HOME` 变量：
   ```
   JAVA_HOME=D:\javajdk
   ```
3. 在 `Path` 变量中添加：
   ```
   %JAVA_HOME%\bin
   ```
4. 验证配置：
   ```bash
   java -version
   ```
   应该显示 JDK 17 的版本信息

### IDE 配置（IntelliJ IDEA）

1. 打开 `File` -> `Project Structure` -> `Project`
2. 设置 `SDK` 为 JDK 17
3. 设置 `Project language level` 为 17
4. 打开 `File` -> `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Maven`
5. 配置 Maven 的 JDK 为 17

### IDE 配置（Eclipse）

1. 打开 `Window` -> `Preferences` -> `Java` -> `Installed JREs`
2. 添加 JDK 17（路径：D:\javajdk）
3. 设置为默认 JRE
4. 打开 `Window` -> `Preferences` -> `Java` -> `Compiler`
5. 设置 `Compiler compliance level` 为 17

## 数据库配置

### 方式一：使用本地MySQL

1. 确保 MySQL 已安装并运行
2. 创建数据库：
   ```sql
   CREATE DATABASE shopping_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 执行初始化脚本：
   ```bash
   mysql -u root -p shopping_mall < sql/init.sql
   ```
4. 修改 `application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/shopping_mall?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: 你的密码
   ```

### 方式二：使用Docker MySQL

```bash
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=你的密码 \
  -e MYSQL_DATABASE=shopping_mall \
  -p 3306:3306 \
  mysql:8.0
```

## 运行项目

### 使用 Maven 命令

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 使用 IDE

1. 在 IDE 中打开项目
2. 找到 `ShoppingApplication.java`
3. 右键 -> `Run 'ShoppingApplication'`

### 使用 Maven Wrapper（推荐）

```bash
cd backend
./mvnw spring-boot:run
```

Windows 系统使用：
```bash
cd backend
mvnw.cmd spring-boot:run
```

## 验证运行

项目启动后，访问：
- 健康检查：http://localhost:8080/actuator/health（如果添加了actuator）
- API测试：http://localhost:8080/api/v1/categories

## 常见问题

### 1. JDK版本不匹配

如果遇到 JDK 版本错误，检查：
- `JAVA_HOME` 环境变量是否正确
- IDE 中的 JDK 配置
- Maven 使用的 JDK 版本

### 2. 数据库连接失败

- 检查 MySQL 服务是否运行
- 验证数据库用户名和密码
- 确认数据库已创建

### 3. 端口被占用

如果 8080 端口被占用，可以修改 `application.yml`：
```yaml
server:
  port: 8081
```

### 4. Maven 依赖下载慢

可以配置国内镜像，编辑 `~/.m2/settings.xml`：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 开发建议

1. 使用 IDE 的代码格式化功能
2. 启用代码检查工具（如 SonarLint）
3. 编写单元测试
4. 使用 Git 进行版本控制
5. 遵循代码规范

