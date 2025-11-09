package com.shopping.admin.mapper;

import com.shopping.admin.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin selectById(Long id);
    Admin selectByUsername(String username);
    int updateById(Admin admin);
}

