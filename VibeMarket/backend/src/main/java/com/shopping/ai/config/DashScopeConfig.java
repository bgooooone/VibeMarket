package com.shopping.ai.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
/**
 * ClassName:DashScopeConfig
 * Description:
 *
 * @Author Bingo
 * @Create 2025/12/7 22:42
 */
@Configuration
public class DashScopeConfig {

    @Value("${alibaba.dashscope.api-key}")
    private String apiKey;

    @Value("${alibaba.dashscope.model}")
    private String model;

    @Value("${alibaba.dashscope.base-url}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}