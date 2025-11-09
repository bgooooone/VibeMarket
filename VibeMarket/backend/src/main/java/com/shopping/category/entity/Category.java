package com.shopping.category.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private String icon;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

