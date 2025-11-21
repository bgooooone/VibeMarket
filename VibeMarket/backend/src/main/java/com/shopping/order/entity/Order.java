package com.shopping.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal actualPay;
    private Integer status;
    private Integer paymentType;
    private String paymentNo;
    private String consignee;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private String remark;
    private Date paymentTime;
    private Date deliveryTime;
    private Date receiveTime;
    private Date cancelTime;
    private Date createTime;
    private Date updateTime;
}

