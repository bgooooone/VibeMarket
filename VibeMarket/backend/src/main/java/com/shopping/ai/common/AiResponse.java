package com.shopping.ai.common;

/**
 * ClassName:AiResponse
 * Description:
 *
 * @Author Bingo
 * @Create 2025/12/7 22:43
 */
public class AiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> AiResponse<T> success(T data) {
        AiResponse<T> response = new AiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> AiResponse<T> error(String message) {
        AiResponse<T> response = new AiResponse<>();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
