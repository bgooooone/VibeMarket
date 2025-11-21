package com.shopping.product.controller;

import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import com.shopping.product.dto.ProductDTO;
import com.shopping.product.entity.Product;
import com.shopping.product.service.ProductService;
import com.shopping.admin.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public ResponseResult<List<Product>> getAllProducts(@CurrentAdmin Admin currentAdmin) {
        // 管理员查看所有商品，包括下架的
        List<Product> products = productService.getAllProducts();
        return ResponseResult.success(products);
    }

    @GetMapping("/{id}")
    public ResponseResult<Product> getProductById(@CurrentAdmin Admin currentAdmin, @PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseResult.error("商品不存在");
        }
        return ResponseResult.success(product);
    }

    @PostMapping("/create")
    public ResponseResult<Product> createProduct(@CurrentAdmin Admin currentAdmin,
                                                 @Validated @RequestBody ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO);
        return ResponseResult.success("创建成功", product);
    }

    @PutMapping("/{id}")
    public ResponseResult<Product> updateProduct(@CurrentAdmin Admin currentAdmin,
                                                 @PathVariable Long id,
                                                 @Validated @RequestBody ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO);
        return ResponseResult.success("更新成功", product);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<String> deleteProduct(@CurrentAdmin Admin currentAdmin, @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseResult.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public ResponseResult<Product> updateProductStatus(@CurrentAdmin Admin currentAdmin,
                                                       @PathVariable Long id,
                                                       @RequestParam Integer status) {
        Product product = productService.updateProductStatus(id, status);
        return ResponseResult.success("状态更新成功", product);
    }
}

