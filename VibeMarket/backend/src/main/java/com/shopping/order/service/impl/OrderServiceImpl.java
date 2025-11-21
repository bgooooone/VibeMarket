package com.shopping.order.service.impl;

import com.shopping.address.entity.Address;
import com.shopping.address.mapper.AddressMapper;
import com.shopping.cart.entity.Cart;
import com.shopping.cart.mapper.CartMapper;
import com.shopping.common.exception.BusinessException;
import com.shopping.order.dto.OrderCreateDTO;
import com.shopping.order.entity.Order;
import com.shopping.order.entity.OrderItem;
import com.shopping.order.mapper.OrderItemMapper;
import com.shopping.order.mapper.OrderMapper;
import com.shopping.order.service.OrderService;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        // 获取地址信息
        Address address = addressMapper.selectById(orderCreateDTO.getAddressId());
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权使用该地址");
        }

        // 获取购物车项
        List<Cart> cartList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Long cartId : orderCreateDTO.getCartIds()) {
            Cart cart = cartMapper.selectById(cartId);
            if (cart == null) {
                throw new BusinessException("购物车项不存在");
            }
            if (!cart.getUserId().equals(userId)) {
                throw new BusinessException("无权操作");
            }
            if (cart.getSelected() != 1) {
                throw new BusinessException("请选择要购买的商品");
            }

            Product product = productMapper.selectByIdWithoutStatus(cart.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品已下架");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException("商品库存不足：" + product.getName());
            }

            cartList.add(cart);
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }

        if (cartList.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setActualPay(totalAmount);
        order.setStatus(0); // 待支付
        order.setConsignee(address.getConsignee());
        order.setPhone(address.getPhone());
        order.setProvince(address.getProvince());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setDetailAddress(address.getDetailAddress());
        order.setRemark(orderCreateDTO.getRemark());
        orderMapper.insert(order);

        // 创建订单项并更新库存
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productMapper.selectByIdWithoutStatus(cart.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItems.add(orderItem);

            // 更新商品库存和销量
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }

        orderItemMapper.insertBatch(orderItems);

        // 删除购物车项
        for (Cart cart : cartList) {
            cartMapper.deleteById(cart.getId());
        }

        return order;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> getUserOrders(Long userId, Integer status) {
        return orderMapper.selectByUserId(userId, status);
    }

    @Override
    public List<Map<String, Object>> getAllOrders(Integer status, Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Map<String, Object>> result = orderMapper.selectAll(status, offset, pageSize);
        // MyBatis会自动将下划线字段转换为驼峰，但userName需要保留
        return result;
    }

    @Override
    public int countAllOrders(Integer status) {
        return orderMapper.countAll(status);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        order.setStatus(status);
        Date now = new Date();
        if (status == 1) { // 待发货
            if (order.getPaymentTime() == null) {
                order.setPaymentTime(now); // 如果还没支付时间，设置支付时间
            }
        } else if (status == 2) { // 待收货 -> 已发货
            order.setDeliveryTime(now);
        } else if (status == 4) { // 已完成 -> 已收货
            order.setReceiveTime(now);
        } else if (status == 5) { // 已取消
            order.setCancelTime(now);
        }

        orderMapper.updateById(order);
        return order;
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

