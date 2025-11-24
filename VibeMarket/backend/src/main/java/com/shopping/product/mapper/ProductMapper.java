package com.shopping.product.mapper;

import com.shopping.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    List<Product> selectAll();
    List<Product> selectAllWithoutStatus();
    Product selectById(Long id);
    Product selectByIdWithoutStatus(Long id);
    List<Product> selectByCategoryId(Long categoryId);
    List<Product> selectByKeyword(@Param("keyword") String keyword);
    int insert(Product product);
    int updateById(Product product);
    int deleteById(Long id);
    int countAll();
    List<Map<String, Object>> countByCategory();
}

