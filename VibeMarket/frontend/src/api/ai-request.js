import axios from 'axios';

// AI专用的请求实例
const aiRequest = axios.create({
  baseURL: '/api',
  timeout: 30000
});

// 专门的响应处理（返回完整ResponseResult）
aiRequest.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code === 200) {
      return res;  // 返回完整ResponseResult
    } else {
      return Promise.reject(new Error(res.message || '请求失败'));
    }
  },
  error => {
    console.error('AI请求错误:', error);
    return Promise.reject(error);
  }
);

export default aiRequest;