package com.shopping.product.controller;

import com.shopping.product.entity.Product;
import com.shopping.product.service.ProductService;
import com.shopping.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseResult<List<Product>> getAllProducts() {
        // 普通用户只能看到上架的商品
        List<Product> products = ((com.shopping.product.service.impl.ProductServiceImpl) productService).getPublicProducts();
        return ResponseResult.success(products);
    }

    @GetMapping("/{id}")
    public ResponseResult<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseResult.fail(404, "商品不存在");
        }
        return ResponseResult.success(product);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseResult<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseResult.success(products);
    }

    @GetMapping("/search")
    public ResponseResult<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseResult.success(products);
    }
}

