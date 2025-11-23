package com.shopping.user.service;

import com.shopping.user.dto.UserLoginDTO;
import com.shopping.user.dto.UserRegisterDTO;
import com.shopping.user.entity.User;

public interface UserService {
    User register(UserRegisterDTO userRegisterDTO);
    String login(UserLoginDTO userLoginDTO);
    User getUserById(Long userId);
    User updateProfile(Long userId, User user);
}

