package com.shopping.order.mapper;

import com.shopping.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    Order selectById(Long id);
    Order selectByOrderNo(String orderNo);
    List<Order> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    List<Map<String, Object>> selectAll(@Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAll(@Param("status") Integer status);
    int insert(Order order);
    int updateById(Order order);
}

