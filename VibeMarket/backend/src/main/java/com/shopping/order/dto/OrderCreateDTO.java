package com.shopping.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class OrderCreateDTO {
    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartIds;
    
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    
    private String remark;
}

