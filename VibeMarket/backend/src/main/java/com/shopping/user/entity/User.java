package com.shopping.user.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private Integer gender;
    private Date birthday;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
}

