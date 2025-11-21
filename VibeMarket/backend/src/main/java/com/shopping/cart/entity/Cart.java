package com.shopping.cart.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;
    private Date createTime;
    private Date updateTime;
}

