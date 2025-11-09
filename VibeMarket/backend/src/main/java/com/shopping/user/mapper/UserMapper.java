package com.shopping.user.mapper;

import com.shopping.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(Long id);
    User selectByUsername(String username);
    User selectByPhone(String phone);
    User selectByEmail(String email);
    int insert(User user);
    int updateById(User user);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
}

