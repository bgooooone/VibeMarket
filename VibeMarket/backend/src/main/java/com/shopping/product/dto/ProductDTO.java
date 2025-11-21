package com.shopping.product.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
    
    private String description;
    private String detail;
    private String mainImage;
    private String images;
    
    private Integer status;
}

