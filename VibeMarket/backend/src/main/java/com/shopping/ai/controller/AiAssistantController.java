package com.shopping.ai.controller;

import com.shopping.ai.model.AiRequest;
import com.shopping.ai.service.AiAssistantService;
import com.shopping.common.response.ResponseResult;
import com.shopping.common.annotation.LoginRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AiAssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantController.class);

    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 智能聊天 - 需要登录
     */
    @PostMapping("/chat")
    @LoginRequired  // 添加这个注解
    public ResponseResult<String> chat(@RequestBody AiRequest request) {
        try {
            logger.info("收到AI聊天请求: {}", request.getMessage());

            String response = aiAssistantService.chatWithAI(request.getMessage());

            logger.info("AI回复: {}", response.substring(0, Math.min(50, response.length())) + "...");

            return ResponseResult.success(response);
        } catch (Exception e) {
            logger.error("AI聊天异常: ", e);
            return ResponseResult.fail(500, "AI服务异常: " + e.getMessage());
        }
    }

    /**
     * 获取购物建议 - 需要登录
     */
    @GetMapping("/advice")
    @LoginRequired  // 添加这个注解
    public ResponseResult<String> getShoppingAdvice(@RequestParam String keyword) {
        try {
            logger.info("收到购物建议请求: {}", keyword);

            String advice = aiAssistantService.getShoppingAdvice(keyword);

            return ResponseResult.success(advice);
        } catch (Exception e) {
            logger.error("购物建议异常: ", e);
            return ResponseResult.fail(500, "获取建议失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查 - 可以公开访问
     */
    @GetMapping("/health")
    public ResponseResult<String> healthCheck() {
        logger.info("收到健康检查请求");
        return ResponseResult.success("AI助手服务运行正常");
    }
}