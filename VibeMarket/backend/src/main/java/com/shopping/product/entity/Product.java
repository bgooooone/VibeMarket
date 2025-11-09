package com.shopping.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String description;
    private String detail;
    private String mainImage;
    private String images;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

