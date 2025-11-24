package com.shopping.admin.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getOrderTrends(int days);
    List<Map<String, Object>> getCategoryDistribution();
}

