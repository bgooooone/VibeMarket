package com.shopping.category.controller;

import com.shopping.category.entity.Category;
import com.shopping.category.service.CategoryService;
import com.shopping.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseResult<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseResult.success(categories);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseResult<List<Category>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        return ResponseResult.success(categories);
    }
}

