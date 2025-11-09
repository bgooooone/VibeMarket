package com.shopping.product.mapper;

import com.shopping.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectAll();
    Product selectById(Long id);
    List<Product> selectByCategoryId(Long categoryId);
    List<Product> selectByKeyword(@Param("keyword") String keyword);
}

