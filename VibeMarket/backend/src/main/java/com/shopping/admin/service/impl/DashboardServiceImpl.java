package com.shopping.admin.service.impl;

import com.shopping.admin.service.DashboardService;
import com.shopping.order.mapper.OrderMapper;
import com.shopping.product.mapper.ProductMapper;
import com.shopping.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", userMapper.countList(null));
        overview.put("totalProducts", productMapper.countAll());
        overview.put("totalOrders", orderMapper.countAll(null));
        BigDecimal totalSales = orderMapper.sumTotalSales();
        overview.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);

        LocalDate today = LocalDate.now();
        overview.put("todayOrders", orderMapper.countByDate(today));
        overview.put("yesterdayOrders", orderMapper.countByDate(today.minusDays(1)));
        return overview;
    }

    @Override
    public List<Map<String, Object>> getOrderTrends(int days) {
        List<Map<String, Object>> rawData = orderMapper.getOrderTrends(days);
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        if (rawData != null) {
            for (Map<String, Object> item : rawData) {
                dataMap.put((String) item.get("date"), item);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(DATE_FORMATTER);
            Map<String, Object> item = dataMap.getOrDefault(dateStr, null);
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", dateStr);
            entry.put("orderCount", item != null ? item.get("orderCount") : 0);
            entry.put("totalAmount", item != null ? item.get("totalAmount") : BigDecimal.ZERO);
            result.add(entry);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryDistribution() {
        return productMapper.countByCategory();
    }
}

