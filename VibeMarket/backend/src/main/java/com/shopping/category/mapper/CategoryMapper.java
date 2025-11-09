package com.shopping.category.mapper;

import com.shopping.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectAll();
    List<Category> selectByParentId(Long parentId);
    Category selectById(Long id);
}

