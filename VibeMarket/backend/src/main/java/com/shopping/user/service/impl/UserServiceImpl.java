package com.shopping.user.service.impl;

import com.shopping.common.exception.BusinessException;
import com.shopping.common.utils.JwtUtils;
import com.shopping.common.utils.PasswordUtils;
import com.shopping.user.dto.UserLoginDTO;
import com.shopping.user.dto.UserRegisterDTO;
import com.shopping.user.entity.User;
import com.shopping.user.mapper.UserMapper;
import com.shopping.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        if (userMapper.existsByUsername(userRegisterDTO.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        if (userMapper.existsByPhone(userRegisterDTO.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(passwordUtils.encode(userRegisterDTO.getPassword()));
        user.setPhone(userRegisterDTO.getPhone());
        user.setEmail(userRegisterDTO.getEmail());
        user.setStatus(1);

        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        User user = userMapper.selectByUsername(userLoginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordUtils.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        user.setLastLoginTime(new Date());
        userMapper.updateById(user);

        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
}

