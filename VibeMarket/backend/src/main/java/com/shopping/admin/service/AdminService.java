package com.shopping.admin.service;

import com.shopping.admin.dto.AdminLoginDTO;
import com.shopping.admin.entity.Admin;

public interface AdminService {
    String login(AdminLoginDTO adminLoginDTO);
    Admin getAdminById(Long adminId);
}

