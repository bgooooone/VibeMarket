package com.shopping.user.service;

import com.shopping.user.dto.UserLoginDTO;
import com.shopping.user.dto.UserRegisterDTO;
import com.shopping.user.entity.User;

import java.util.List;

public interface UserService {
    User register(UserRegisterDTO userRegisterDTO);
    String login(UserLoginDTO userLoginDTO);
    User getUserById(Long userId);
    User updateProfile(Long userId, User user);
    List<User> getUserList(String keyword, Integer page, Integer pageSize);
    int countUsers(String keyword);
    void updateUserStatus(Long userId, Integer status);
}

