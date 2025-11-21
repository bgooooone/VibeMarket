package com.shopping.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Date createTime;
    private Date updateTime;
}

