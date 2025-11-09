package com.shopping.admin.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    private Long roleId;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
}

