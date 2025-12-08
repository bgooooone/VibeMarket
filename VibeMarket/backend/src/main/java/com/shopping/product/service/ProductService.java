package com.shopping.product.service;

import com.shopping.product.dto.ProductDTO;
import com.shopping.product.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getPublicProducts();
    Product getProductById(Long id);
    List<Product> getProductsByCategoryId(Long categoryId);
    List<Product> searchProducts(String keyword);
    Product createProduct(ProductDTO productDTO);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    Product updateProductStatus(Long id, Integer status);

}

