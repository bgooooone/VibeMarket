package com.shopping.order.controller;

import com.shopping.common.annotation.CurrentUser;
import com.shopping.common.response.ResponseResult;
import com.shopping.order.dto.OrderCreateDTO;
import com.shopping.order.entity.Order;
import com.shopping.order.entity.OrderItem;
import com.shopping.order.service.OrderService;
import com.shopping.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseResult<Order> createOrder(@CurrentUser User currentUser,
                                            @Validated @RequestBody OrderCreateDTO orderCreateDTO) {
        Order order = orderService.createOrder(currentUser.getId(), orderCreateDTO);
        return ResponseResult.success("订单创建成功", order);
    }

    @GetMapping("/list")
    public ResponseResult<List<Order>> getUserOrders(@CurrentUser User currentUser,
                                                     @RequestParam(required = false) Integer status) {
        List<Order> orders = orderService.getUserOrders(currentUser.getId(), status);
        return ResponseResult.success(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseResult<Map<String, Object>> getOrderDetail(@CurrentUser User currentUser,
                                                              @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseResult.error("订单不存在");
        }
        if (!order.getUserId().equals(currentUser.getId())) {
            return ResponseResult.error("无权访问");
        }

        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderItems", orderItems);
        return ResponseResult.success(data);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseResult<Order> cancelOrder(@CurrentUser User currentUser,
                                            @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseResult.error("订单不存在");
        }
        if (!order.getUserId().equals(currentUser.getId())) {
            return ResponseResult.error("无权操作");
        }
        if (order.getStatus() != 0) {
            return ResponseResult.error("订单状态不允许取消");
        }

        order = orderService.updateOrderStatus(orderId, 5);
        return ResponseResult.success("订单已取消", order);
    }

    @PutMapping("/{orderId}/pay")
    public ResponseResult<Order> payOrder(@CurrentUser User currentUser,
                                          @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseResult.error("订单不存在");
        }
        if (!order.getUserId().equals(currentUser.getId())) {
            return ResponseResult.error("无权操作");
        }
        if (order.getStatus() != 0) {
            return ResponseResult.error("订单状态不允许支付");
        }

        // 更新订单状态为待发货（1），表示已支付
        order = orderService.updateOrderStatus(orderId, 1);
        return ResponseResult.success("支付成功", order);
    }
}

