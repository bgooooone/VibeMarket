package com.shopping.admin.controller;

import com.shopping.common.response.ResponseResult;
import com.shopping.common.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 临时测试控制器 - 用于生成BCrypt密码
 * 生产环境请删除此控制器
 */
@RestController
@RequestMapping("/api/v1/admin/test")
public class AdminTestController {
    @Autowired
    private PasswordUtils passwordUtils;

    /**
     * 生成BCrypt加密密码
     * GET /api/v1/admin/test/generate-password?password=admin123
     */
    @GetMapping("/generate-password")
    public ResponseResult<Map<String, String>> generatePassword(@RequestParam String password) {
        String encoded = passwordUtils.encode(password);
        Map<String, String> result = new HashMap<>();
        result.put("rawPassword", password);
        result.put("encodedPassword", encoded);
        result.put("sql", "UPDATE `admin` SET `password` = '" + encoded + "' WHERE `username` = 'admin';");
        return ResponseResult.success(result);
    }
}

