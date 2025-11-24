package com.shopping.admin.controller;

import com.shopping.admin.entity.Admin;
import com.shopping.admin.service.DashboardService;
import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseResult<Map<String, Object>> getOverview(@CurrentAdmin Admin currentAdmin) {
        return ResponseResult.success(dashboardService.getOverview());
    }

    @GetMapping("/trends")
    public ResponseResult<List<Map<String, Object>>> getTrends(@CurrentAdmin Admin currentAdmin,
                                                               @RequestParam(defaultValue = "7") Integer days) {
        int safeDays = days == null || days <= 0 ? 7 : Math.min(days, 30);
        return ResponseResult.success(dashboardService.getOrderTrends(safeDays));
    }

    @GetMapping("/categories")
    public ResponseResult<List<Map<String, Object>>> getCategoryDistribution(@CurrentAdmin Admin currentAdmin) {
        return ResponseResult.success(dashboardService.getCategoryDistribution());
    }
}

