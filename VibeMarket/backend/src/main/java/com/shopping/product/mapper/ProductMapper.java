package com.shopping.product.mapper;

import com.shopping.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    @Select("SELECT * FROM product WHERE " +
            "name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY sales DESC LIMIT 10")
    List<Product> selectByKeywords(@Param("keywords") List<String> keywords);

    @Select("SELECT * FROM product WHERE " +
            "name LIKE CONCAT('%', #{query}, '%') OR " +
            "description LIKE CONCAT('%', #{query}, '%') " +
            "ORDER BY create_time DESC LIMIT 5")
    List<Product> selectBySearch(@Param("query") String query);
}

