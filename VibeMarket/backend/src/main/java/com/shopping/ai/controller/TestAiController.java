package com.shopping.ai.controller;

import com.shopping.common.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestAiController {

    private static final Logger logger = LoggerFactory.getLogger(TestAiController.class);

    @GetMapping("/simple")
    public ResponseResult<String> simpleTest() {
        logger.info("收到简单测试请求");
        return ResponseResult.success("测试接口正常工作");
    }

    @PostMapping("/echo")
    public ResponseResult<String> echoTest(@RequestBody String message) {
        logger.info("收到回显请求: {}", message);
        return ResponseResult.success("收到消息: " + message);
    }
}