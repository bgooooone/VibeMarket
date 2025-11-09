package com.shopping.product.service;

import com.shopping.product.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    List<Product> getProductsByCategoryId(Long categoryId);
    List<Product> searchProducts(String keyword);
}

