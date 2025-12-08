package com.shopping.ai.service;

import com.shopping.product.entity.Product;
import com.shopping.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiProductService {

    @Autowired
    private ProductService productService;

    /**
     * 获取所有上架商品（用于AI分析）
     */
    public List<Product> getAllAvailableProducts() {
        try {
            // 记录日志查看是否调用成功
            System.out.println("=== AI商品服务：开始获取商品数据 ===");
            List<Product> products = productService.getPublicProducts();
            System.out.println("=== AI商品服务：获取到 " + products.size() + " 个商品 ===");
            products.forEach(p -> System.out.println("商品: " + p.getName() + " 价格: " + p.getPrice()));
            return products;
        } catch (Exception e) {
            System.out.println("=== AI商品服务：获取商品失败 ===");
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * 根据关键词搜索商品
     */
    public List<Product> searchProducts(String keyword) {
        return productService.searchProducts(keyword);
    }

    /**
     * 根据分类ID获取商品
     */
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    /**
     * 获取商品统计信息（用于AI了解库存情况）
     */
    public String getProductStats() {
        List<Product> products = getAllAvailableProducts();

        if (products.isEmpty()) {
            return "暂无商品数据";
        }

        long totalProducts = products.size();
        double avgPrice = products.stream()
                .mapToDouble(p -> p.getPrice().doubleValue())
                .average()
                .orElse(0);

        long lowStockCount = products.stream()
                .filter(p -> p.getStock() < 10)
                .count();

        long highSalesCount = products.stream()
                .filter(p -> p.getSales() > 0)
                .count();

        return String.format(
                "商品总数：%d件，平均价格：¥%.2f，库存紧张商品：%d件，有销量商品：%d件",
                totalProducts, avgPrice, lowStockCount, highSalesCount
        );
    }

    /**
     * 获取热门商品（按销量）
     */
    public List<Product> getPopularProducts(int limit) {
        return getAllAvailableProducts().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getSales(), p1.getSales()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取价格实惠的商品（按价格排序）
     */
    public List<Product> getAffordableProducts(int limit) {
        return getAllAvailableProducts().stream()
                .sorted((p1, p2) -> Double.compare(p1.getPrice().doubleValue(), p2.getPrice().doubleValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 根据商品信息生成AI提示词
     */
    public String generateProductPrompt(String userQuestion) {
        List<Product> products = getAllAvailableProducts();

        if (products.isEmpty()) {
            return "暂无商品信息";
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("以下是商城的商品信息：\n\n");

        // 添加商品列表
        prompt.append("【商品列表】\n");
        products.forEach(product -> {
            prompt.append("• ").append(product.getName())
                    .append(" - ¥").append(product.getPrice());

            // 显示折扣信息
            if (product.getOriginalPrice() != null &&
                    product.getOriginalPrice().compareTo(product.getPrice()) > 0) {
                double discount = product.getOriginalPrice().subtract(product.getPrice()).doubleValue();
                prompt.append(" (优惠¥").append(String.format("%.2f", discount)).append(")");
            }

            prompt.append("，库存：").append(product.getStock()).append("件");

            if (product.getSales() > 0) {
                prompt.append("，销量：").append(product.getSales()).append("件");
            }

            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                prompt.append("，描述：").append(product.getDescription());
            }

            prompt.append("\n");
        });

        // 添加统计信息
        prompt.append("\n【商品统计】\n");
        prompt.append(getProductStats()).append("\n");

        // 添加热门商品
        List<Product> popularProducts = getPopularProducts(3);
        if (!popularProducts.isEmpty()) {
            prompt.append("\n【热门商品】\n");
            popularProducts.forEach(p ->
                    prompt.append("• ").append(p.getName())
                            .append(" (销量").append(p.getSales()).append("件)\n"));
        }

        // 添加价格实惠的商品
        List<Product> affordableProducts = getAffordableProducts(3);
        if (!affordableProducts.isEmpty()) {
            prompt.append("\n【实惠商品】\n");
            affordableProducts.forEach(p ->
                    prompt.append("• ").append(p.getName())
                            .append(" (¥").append(p.getPrice()).append(")\n"));
        }

        prompt.append("\n用户的问题是：").append(userQuestion);
        prompt.append("\n请根据以上商品信息，为用户提供专业的购物建议。");

        return prompt.toString();
    }
}