package com.shopping.admin.service.impl;

import com.shopping.admin.dto.AdminLoginDTO;
import com.shopping.admin.entity.Admin;
import com.shopping.admin.mapper.AdminMapper;
import com.shopping.common.exception.BusinessException;
import com.shopping.common.utils.JwtUtils;
import com.shopping.common.utils.PasswordUtils;
import com.shopping.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(AdminLoginDTO adminLoginDTO) {
        Admin admin = adminMapper.selectByUsername(adminLoginDTO.getUsername());
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 调试信息：检查密码匹配
        boolean passwordMatches = passwordUtils.matches(adminLoginDTO.getPassword(), admin.getPassword());
        if (!passwordMatches) {
            // 如果密码不匹配，尝试重新加密并更新（仅用于调试）
            System.out.println("密码验证失败，原始密码: " + adminLoginDTO.getPassword());
            System.out.println("数据库中的密码hash: " + admin.getPassword());
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        admin.setLastLoginTime(new Date());
        adminMapper.updateById(admin);

        return jwtUtils.generateAdminToken(admin.getId(), admin.getUsername());
    }

    @Override
    public Admin getAdminById(Long adminId) {
        return adminMapper.selectById(adminId);
    }
}

