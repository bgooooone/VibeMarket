package com.shopping.order.controller;

import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import com.shopping.order.entity.Order;
import com.shopping.order.entity.OrderItem;
import com.shopping.order.service.OrderService;
import com.shopping.admin.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public ResponseResult<Map<String, Object>> getAllOrders(@CurrentAdmin Admin currentAdmin,
                                                           @RequestParam(required = false) Integer status,
                                                           @RequestParam(defaultValue = "1") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Map<String, Object>> orders = orderService.getAllOrders(status, page, pageSize);
        int total = orderService.countAllOrders(status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", orders);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return ResponseResult.success(data);
    }

    @GetMapping("/{orderId}")
    public ResponseResult<Map<String, Object>> getOrderDetail(@CurrentAdmin Admin currentAdmin,
                                                             @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseResult.error("订单不存在");
        }

        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderItems", orderItems);
        return ResponseResult.success(data);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseResult<Order> shipOrder(@CurrentAdmin Admin currentAdmin,
                                          @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseResult.error("订单不存在");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return ResponseResult.error("订单状态不允许发货");
        }

        // 如果状态是0（待支付），先更新为1（待发货），再更新为2（待收货）
        if (order.getStatus() == 0) {
            order = orderService.updateOrderStatus(orderId, 1);
        }
        order = orderService.updateOrderStatus(orderId, 2);
        return ResponseResult.success("发货成功", order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseResult<Order> updateOrderStatus(@CurrentAdmin Admin currentAdmin,
                                                   @PathVariable Long orderId,
                                                   @RequestParam Integer status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseResult.success("状态更新成功", order);
    }
}

