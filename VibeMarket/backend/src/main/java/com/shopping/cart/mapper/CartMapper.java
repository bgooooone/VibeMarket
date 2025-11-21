package com.shopping.cart.mapper;

import com.shopping.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    Cart selectById(Long id);
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    List<Cart> selectByUserId(Long userId);
    List<Cart> selectSelectedByUserId(Long userId);
    int insert(Cart cart);
    int updateById(Cart cart);
    int deleteById(Long id);
    int deleteByUserId(Long userId);
    int deleteSelectedByUserId(Long userId);
}

