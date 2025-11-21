package com.shopping.product.service.impl;

import com.shopping.common.exception.BusinessException;
import com.shopping.product.dto.ProductDTO;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import com.shopping.product.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getAllProducts() {
        // 返回所有商品（包括下架的），用于管理员查看
        return productMapper.selectAllWithoutStatus();
    }
    
    public List<Product> getPublicProducts() {
        // 返回上架商品，用于普通用户查看
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

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setSales(0);
        if (product.getStatus() == null) {
            product.setStatus(1); // 默认上架
        }
        productMapper.insert(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productMapper.selectByIdWithoutStatus(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        BeanUtils.copyProperties(productDTO, product, "id", "sales", "createTime");
        productMapper.updateById(product);
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectByIdWithoutStatus(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Product updateProductStatus(Long id, Integer status) {
        Product product = productMapper.selectByIdWithoutStatus(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        return product;
    }
}

