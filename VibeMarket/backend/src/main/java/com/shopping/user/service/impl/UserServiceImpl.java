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
import java.util.List;

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
        // 如果email为空字符串，设置为null，避免数据库唯一约束冲突
        String email = userRegisterDTO.getEmail();
        user.setEmail(email != null && !email.trim().isEmpty() ? email : null);
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

    @Override
    public User updateProfile(Long userId, User user) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 只允许更新特定字段
        if (user.getPhone() != null) {
            // 检查手机号是否被其他用户使用
            User userByPhone = userMapper.selectByPhone(user.getPhone());
            if (userByPhone != null && !userByPhone.getId().equals(userId)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            existingUser.setPhone(user.getPhone());
        }

        if (user.getEmail() != null) {
            // 如果email为空字符串，设置为null
            String email = user.getEmail().trim().isEmpty() ? null : user.getEmail();
            existingUser.setEmail(email);
        }

        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }

        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }

        userMapper.updateById(existingUser);
        return existingUser;
    }

    @Override
    public List<User> getUserList(String keyword, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return userMapper.selectList(keyword, offset, pageSize);
    }

    @Override
    public int countUsers(String keyword) {
        return userMapper.countList(keyword);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateStatus(userId, status);
    }
}

