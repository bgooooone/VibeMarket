package com.shopping.admin.controller;

import com.shopping.admin.entity.Admin;
import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import com.shopping.user.entity.User;
import com.shopping.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseResult<Map<String, Object>> getUsers(@CurrentAdmin Admin currentAdmin,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        List<User> users = userService.getUserList(keyword, page, pageSize);
        int total = userService.countUsers(keyword);

        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return ResponseResult.success(data);
    }

    @PutMapping("/{id}/status")
    public ResponseResult<String> updateStatus(@CurrentAdmin Admin currentAdmin,
                                               @PathVariable Long id,
                                               @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ResponseResult.success("更新成功", null);
    }
}

