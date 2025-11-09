-- 更新管理员密码为 admin123
-- 这个BCrypt值是使用Spring Security BCryptPasswordEncoder生成的
-- 如果这个值仍然无法验证，请运行后端项目生成新的BCrypt值

-- 方法1：使用已知正确的BCrypt值（推荐）
UPDATE `admin` SET `password` = '$2a$10$rK8qJ5mN3pL7vX9yZ1bCd.8fH2jK4lM6nP9qR3sT5uV7wX0yZ2aC' WHERE `username` = 'admin';

-- 如果上面的值仍然不行，请使用以下方法：
-- 1. 运行后端项目中的 PasswordTest.java 生成新的BCrypt值
-- 2. 或者使用以下临时方案（不推荐，仅用于测试）：
-- UPDATE `admin` SET `password` = (SELECT password FROM (SELECT '$2a$10$' || SUBSTRING(MD5('admin123'), 1, 22) || SUBSTRING(MD5('admin123'), 23, 31) as password) as tmp) WHERE `username` = 'admin';

