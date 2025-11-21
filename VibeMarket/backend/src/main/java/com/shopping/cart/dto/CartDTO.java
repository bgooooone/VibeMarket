package com.shopping.cart.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Data
public class CartDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    
    private Integer selected;
}

