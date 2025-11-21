package com.shopping.order.mapper;

import com.shopping.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    OrderItem selectById(Long id);
    List<OrderItem> selectByOrderId(Long orderId);
    int insert(OrderItem orderItem);
    int insertBatch(List<OrderItem> orderItems);
}

