package com.shopping.address.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Address {
    private Long id;
    private Long userId;
    private String consignee;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    private Date createTime;
    private Date updateTime;
}

