package com.shopping.user.controller;

import com.shopping.common.annotation.CurrentUser;
import com.shopping.common.response.ResponseResult;
import com.shopping.user.dto.UserLoginDTO;
import com.shopping.user.dto.UserRegisterDTO;
import com.shopping.user.entity.User;
import com.shopping.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseResult<Map<String, Object>> register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        User user = userService.register(userRegisterDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        return ResponseResult.success("注册成功", data);
    }

    @PostMapping("/login")
    public ResponseResult<Map<String, Object>> login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        String token = userService.login(userLoginDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ResponseResult.success("登录成功", data);
    }

    @PostMapping("/logout")
    public ResponseResult<String> logout() {
        return ResponseResult.success("登出成功");
    }

    @GetMapping("/profile")
    public ResponseResult<User> getProfile(@CurrentUser User currentUser) {
        User user = userService.getUserById(currentUser.getId());
        user.setPassword(null); // 不返回密码
        return ResponseResult.success(user);
    }

    @PutMapping("/profile")
    public ResponseResult<User> updateProfile(@CurrentUser User currentUser, @RequestBody User user) {
        User updatedUser = userService.updateProfile(currentUser.getId(), user);
        updatedUser.setPassword(null); // 不返回密码
        return ResponseResult.success("更新成功", updatedUser);
    }
}
