import aiRequest from './ai-request';  // 使用专门的AI请求

const aiApi = {
  chat(data) {
    return aiRequest({
      url: '/v1/ai/chat',
      method: 'post',
      data
    });
  },
  
  getShoppingAdvice(keyword) {
    return aiRequest({
      url: '/v1/ai/advice',
      method: 'get',
      params: { keyword }
    });
  },
  
  checkHealth() {
    return aiRequest({
      url: '/v1/ai/health',
      method: 'get'
    });
  }
};

export default aiApi;