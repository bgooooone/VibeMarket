package com.shopping.admin.controller;

import com.shopping.admin.dto.AdminLoginDTO;
import com.shopping.admin.entity.Admin;
import com.shopping.admin.service.AdminService;
import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseResult<Map<String, Object>> login(@Validated @RequestBody AdminLoginDTO adminLoginDTO) {
        String token = adminService.login(adminLoginDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ResponseResult.success("登录成功", data);
    }

    @PostMapping("/logout")
    public ResponseResult<String> logout() {
        return ResponseResult.success("登出成功");
    }

    @GetMapping("/profile")
    public ResponseResult<Admin> getProfile(@CurrentAdmin Admin currentAdmin) {
        Admin admin = adminService.getAdminById(currentAdmin.getId());
        admin.setPassword(null); // 不返回密码
        return ResponseResult.success(admin);
    }
}

