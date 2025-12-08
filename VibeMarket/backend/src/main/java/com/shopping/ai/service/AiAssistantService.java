package com.shopping.ai.service;

public interface AiAssistantService {
    /**
     * 智能聊天（能理解商品查询）
     */
    String chatWithAI(String message);

    /**
     * 获取购物建议
     */
    String getShoppingAdvice(String keyword);
}