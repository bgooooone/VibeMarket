package com.shopping.product.service.impl;

import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import com.shopping.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productMapper.selectByKeyword(keyword);
    }
}

