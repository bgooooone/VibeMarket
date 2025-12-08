package com.shopping.ai.service.impl;

import com.shopping.ai.service.AiAssistantService;
import com.shopping.ai.service.AiProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EnhancedAiAssistantServiceImpl implements AiAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedAiAssistantServiceImpl.class);

    @Value("${alibaba.dashscope.api-key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AiProductService aiProductService;

    @Override
    public String chatWithAI(String message) {
        logger.info("处理AI聊天: {}", message);

        // 检查是否是商品相关的问题
        String lowerMessage = message.toLowerCase();

        // 商品相关关键词
        String[] productKeywords = {
                "商品", "产品", "买", "购买", "购物", "推荐",
                "价格", "多少钱", "贵不贵", "便宜", "贵",
                "库存", "有货", "缺货", "断货",
                "销量", "热门", "畅销", "好卖",
                "笔记本", "计算器", "洗发水", "教材", "洗面奶", "方便面", "手机",
                "文具", "日用品", "食品", "电子产品"
        };

        boolean isProductRelated = Arrays.stream(productKeywords)
                .anyMatch(lowerMessage::contains);

        if (isProductRelated) {
            return getProductBasedResponse(message);
        } else {
            return getGeneralResponse(message);
        }
    }

    @Override
    public String getShoppingAdvice(String keyword) {
        logger.info("处理购物建议: {}", keyword);
        return getProductBasedResponse("推荐" + keyword);
    }

    /**
     * 基于商品数据的响应
     */
    private String getProductBasedResponse(String userMessage) {
        try {
            logger.info("开始生成商品响应，用户问题: {}", userMessage);

            // 1. 生成包含商品信息的提示词
            String productPrompt = aiProductService.generateProductPrompt(userMessage);

            logger.info("生成的商品提示词长度: {}", productPrompt.length());

            // 如果商品数据为空
            if (productPrompt.contains("暂无商品信息") || productPrompt.contains("暂无商品数据")) {
                return "抱歉，目前商品数据暂时不可用，请稍后再试。";
            }

            // 2. 构建AI系统消息
            String systemMessage = "你是一个校园购物商城的AI助手。请根据提供的商品信息，为用户提供准确、有用的建议。" +
                    "注意：\n" +
                    "1. 只推荐商城中实际有的商品\n" +
                    "2. 提供准确的商品价格和库存信息\n" +
                    "3. 如果用户问的商品没有，如实告知\n" +
                    "4. 可以对比不同商品的特点\n" +
                    "5. 语气友好亲切，适合学生群体\n" +
                    "6. 回复中要引用具体的商品信息，比如价格、库存等";

            // 3. 调用AI API
            String aiResponse = callQwenAPIWithContext(systemMessage, productPrompt);

            logger.info("AI回复内容: {}", aiResponse);
            return aiResponse;

        } catch (Exception e) {
            logger.error("商品响应异常: ", e);
            return "抱歉，商品信息服务暂时不可用。错误: " + e.getMessage();
        }
    }

    /**
     * 通用聊天响应
     */
    private String getGeneralResponse(String message) {
        try {
            String systemMessage = "你是一个校园购物商城的AI助手，专门为大学生提供购物帮助和服务咨询。";
            return callQwenAPIWithContext(systemMessage, message);
        } catch (Exception e) {
            logger.error("通用聊天异常: ", e);
            return "抱歉，聊天服务暂时不可用。";
        }
    }

    /**
     * 带系统消息的API调用
     */
    private String callQwenAPIWithContext(String systemMessage, String userMessage) {
        try {
            String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("X-DashScope-SSE", "disable");

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统消息
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemMessage);
            messages.add(systemMsg);

            // 用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // 请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-max");

            Map<String, Object> input = new HashMap<>();
            input.put("messages", messages);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "message");
            parameters.put("max_tokens", 1000);
            requestBody.put("parameters", parameters);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.info("调用通义千问API，用户消息长度: {}", userMessage.length());

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null) {
                    Map<String, Object> output = (Map<String, Object>) body.get("output");
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");

                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                        String content = (String) messageObj.get("content");
                        logger.info("AI API返回成功，回复长度: {}", content.length());
                        return content;
                    }
                }
            }

            logger.error("AI API返回异常状态: {}", response.getStatusCode());
            return "抱歉，AI服务暂时无法响应。";

        } catch (Exception e) {
            logger.error("AI API调用异常: ", e);
            return "抱歉，AI服务调用失败: " + e.getMessage();
        }
    }
}