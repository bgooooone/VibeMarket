package com.shopping.user.mapper;

import com.shopping.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    List<User> selectList(@Param("keyword") String keyword,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);
    int countList(@Param("keyword") String keyword);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

