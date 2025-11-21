package com.shopping.order.service;

import com.shopping.order.dto.OrderCreateDTO;
import com.shopping.order.entity.Order;
import com.shopping.order.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Long userId, OrderCreateDTO orderCreateDTO);
    Order getOrderById(Long orderId);
    Order getOrderByOrderNo(String orderNo);
    List<Order> getUserOrders(Long userId, Integer status);
    List<Map<String, Object>> getAllOrders(Integer status, Integer page, Integer pageSize);
    int countAllOrders(Integer status);
    Order updateOrderStatus(Long orderId, Integer status);
    List<OrderItem> getOrderItems(Long orderId);
}

