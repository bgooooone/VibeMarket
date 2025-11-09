package com.shopping.category.service;

import com.shopping.category.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getCategoriesByParentId(Long parentId);
}

