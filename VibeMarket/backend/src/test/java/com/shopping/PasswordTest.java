package com.shopping;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成admin123的BCrypt加密值
        String password = "admin123";
        String encoded = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("BCrypt加密值: " + encoded);
        
        // 验证旧的加密值
        String oldEncoded = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC";
        boolean matches = encoder.matches(password, oldEncoded);
        System.out.println("旧加密值验证结果: " + matches);
        
        // 验证新生成的加密值
        boolean newMatches = encoder.matches(password, encoded);
        System.out.println("新加密值验证结果: " + newMatches);
    }
}

