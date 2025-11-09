# 修复管理员密码问题

## 问题描述
管理员登录时提示"用户名或密码错误"，可能是数据库中的BCrypt加密值不正确。

## 解决方案

### 方法一：使用测试接口生成新密码（推荐）

1. **启动后端服务**

2. **访问测试接口生成新的BCrypt密码**：
   ```
   http://localhost:8080/api/v1/admin/test/generate-password?password=admin123
   ```

3. **复制返回的 `encodedPassword` 值**

4. **执行SQL更新密码**：
   ```sql
   UPDATE `admin` SET `password` = '复制的encodedPassword值' WHERE `username` = 'admin';
   ```

5. **重新尝试登录**

### 方法二：直接更新数据库密码

1. **连接到MySQL数据库**

2. **执行以下SQL**（使用新生成的BCrypt值）：
   ```sql
   -- 先删除旧的管理员记录（如果存在）
   DELETE FROM `admin` WHERE `username` = 'admin';
   
   -- 插入新的管理员记录（需要先运行测试接口获取BCrypt值）
   INSERT INTO `admin` (`username`, `password`, `name`, `status`) 
   VALUES ('admin', '这里填入从测试接口获取的BCrypt值', '系统管理员', 1);
   ```

### 方法三：临时使用明文密码（仅用于测试，不推荐）

如果需要快速测试，可以临时修改代码跳过密码验证，但**生产环境绝对不能这样做**。

## 验证步骤

1. 确保数据库中有管理员记录：
   ```sql
   SELECT * FROM `admin` WHERE `username` = 'admin';
   ```

2. 检查管理员状态是否为1（启用）：
   ```sql
   SELECT username, status FROM `admin` WHERE `username` = 'admin';
   ```

3. 使用新生成的BCrypt值更新密码后，重新尝试登录

## 注意事项

- BCrypt每次加密的结果都不同，但验证时应该能匹配
- 确保使用相同的BCrypt算法版本（$2a$10$）
- 生产环境请删除 `AdminTestController` 测试控制器

## 快速修复命令

如果后端服务正在运行，可以直接访问：
```
http://localhost:8080/api/v1/admin/test/generate-password?password=admin123
```

然后复制返回的SQL语句执行即可。

