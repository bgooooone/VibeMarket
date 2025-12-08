<template>
  <div v-if="isLoggedIn" class="ai-assistant-container">
    <!-- AI助手浮窗按钮 -->
    <button 
      class="ai-floating-btn"
      @click="toggleChatWindow"
    >
      <i class="ai-icon">🤖</i>
    </button>
    
    <!-- AI聊天窗口 -->
    <div v-if="showChatWindow" class="ai-chat-window">
      <!-- 聊天窗口头部 -->
      <div class="chat-header">
        <h3>校园购物AI助手</h3>
        <button class="close-btn" @click="closeChatWindow">×</button>
      </div>
      
      <!-- 聊天内容区域 -->
      <div class="chat-body" ref="chatBody">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <p>👋 你好！我是校园购物AI助手，可以为你提供：</p>
          <ul>
            <li>💡 商品推荐</li>
            <li>📝 购物建议</li>
            <li>💰 价格参考</li>
            <li>🎯 使用建议</li>
          </ul>
          <p>有什么可以帮你的吗？</p>
        </div>
        
        <!-- 消息列表 -->
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-content" v-html="formatMessage(message.content)"></div>
          <div class="message-time">
            {{ message.time }}
          </div>
        </div>
        
        <!-- 加载指示器 -->
        <div v-if="loading" class="message ai">
          <div class="message-content typing">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
      
      <!-- 输入区域 -->
      <div class="chat-input">
        <textarea 
          v-model="userInput"
          @keydown.enter.exact.prevent="sendMessage"
          placeholder="输入您的问题，如：推荐文具..."
          rows="1"
          @input="adjustTextareaHeight"
          ref="textarea"
        ></textarea>
        <button 
          @click="sendMessage"
          :disabled="loading || !isLoggedIn"
          class="send-btn"
        >
          发送
        </button>
      </div>
      
      <!-- 快捷建议按钮 -->
      <div class="quick-suggestions">
        <button 
          v-for="suggestion in quickSuggestions"
          :key="suggestion"
          @click="sendQuickSuggestion(suggestion)"
          class="suggestion-btn"
        >
          {{ suggestion }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
  import aiApi from '@/api/ai';
  import { useUserStore } from '@/stores/user';
  import { useAdminStore } from '@/stores/admin';
  import { storeToRefs } from 'pinia';
  
  export default {
    name: 'AiAssistant',
    setup() {
      const userStore = useUserStore();
      const adminStore = useAdminStore();
      
      const { token: userToken } = storeToRefs(userStore);
      const { token: adminToken } = storeToRefs(adminStore);
      
      return {
        userToken,
        adminToken
      };
    },
    data() {
      return {
        showChatWindow: false,
        userInput: '',
        messages: [],
        loading: false,
        quickSuggestions: [
          '推荐学生文具',
          '有什么零食推荐？',
          '学习用品怎么选？',
          '电子产品推荐'
        ],
        // 拖拽相关
        isDragging: false,
        dragStartX: 0,
        dragStartY: 0,
        elementStartX: 0,
        elementStartY: 0
      };
    },
    computed: {
      isLoggedIn() {
        return !!(this.userToken || this.adminToken);
      }
    },
    methods: {
      // ============ 拖拽功能 ============
      startDrag(event) {
        if (!this.isLoggedIn) return;
        
        this.isDragging = true;
        const btn = event.currentTarget;
        const rect = btn.getBoundingClientRect();
        
        this.dragStartX = event.clientX || event.touches[0].clientX;
        this.dragStartY = event.clientY || event.touches[0].clientY;
        this.elementStartX = rect.left;
        this.elementStartY = rect.top;
        
        // 添加事件监听器
        document.addEventListener('mousemove', this.onDrag);
        document.addEventListener('touchmove', this.onDrag, { passive: false });
        document.addEventListener('mouseup', this.stopDrag);
        document.addEventListener('touchend', this.stopDrag);
        
        // 防止文本选择
        event.preventDefault();
        
        // 改变光标
        btn.style.cursor = 'grabbing';
      },
      
      onDrag(event) {
        if (!this.isDragging) return;
        
        const clientX = event.clientX || (event.touches && event.touches[0].clientX);
        const clientY = event.clientY || (event.touches && event.touches[0].clientY);
        
        if (!clientX || !clientY) return;
        
        const deltaX = clientX - this.dragStartX;
        const deltaY = clientY - this.dragStartY;
        
        const newX = this.elementStartX + deltaX;
        const newY = this.elementStartY + deltaY;
        
        // 限制在可视区域内
        const btn = document.querySelector('.ai-floating-btn');
        if (!btn) return;
        
        const maxX = window.innerWidth - btn.offsetWidth - 20;
        const maxY = window.innerHeight - btn.offsetHeight - 20;
        
        const clampedX = Math.max(20, Math.min(newX, maxX));
        const clampedY = Math.max(20, Math.min(newY, maxY));
        
        btn.style.left = `${clampedX}px`;
        btn.style.top = `${clampedY}px`;
        btn.style.right = 'auto';
        btn.style.bottom = 'auto';
        btn.style.position = 'fixed';
        
        event.preventDefault();
      },
      
      stopDrag() {
        this.isDragging = false;
        
        // 移除事件监听器
        document.removeEventListener('mousemove', this.onDrag);
        document.removeEventListener('touchmove', this.onDrag);
        document.removeEventListener('mouseup', this.stopDrag);
        document.removeEventListener('touchend', this.stopDrag);
        
        // 恢复光标
        const btn = document.querySelector('.ai-floating-btn');
        if (btn) {
          btn.style.cursor = 'grab';
        }
      },
      
      // ============ 原有方法 ============
      toggleChatWindow() {
        if (!this.isLoggedIn) {
          this.$message?.warning('请先登录后再使用AI助手');
          return;
        }
        
        this.showChatWindow = !this.showChatWindow;
        if (this.showChatWindow) {
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        }
      },
      
      closeChatWindow() {
        this.showChatWindow = false;
      },
      
      adjustTextareaHeight() {
        const textarea = this.$refs.textarea;
        if (textarea) {
          textarea.style.height = 'auto';
          textarea.style.height = textarea.scrollHeight + 'px';
        }
      },
      
      async sendMessage() {
        if (!this.isLoggedIn) {
          this.addMessage('请先登录后再使用AI助手', 'ai');
          return;
        }
        
        const message = this.userInput.trim();
        if (!message) return;
        
        this.addMessage(message, 'user');
        this.userInput = '';
        if (this.$refs.textarea) {
          this.$refs.textarea.style.height = 'auto';
        }
        this.loading = true;
        
        try {
          const requestData = { message: message };
          const response = await aiApi.chat(requestData);
          
          if (response.code === 200) {
            this.addMessage(response.data, 'ai');
          } else if (response.code === 401) {
            this.addMessage('登录已过期，请重新登录', 'ai');
            this.showChatWindow = false;
          } else {
            throw new Error(response.message || '请求失败');
          }
        } catch (error) {
          console.error('AI对话失败:', error);
          
          if (error.response && error.response.status === 401) {
            this.addMessage('登录已过期，请重新登录', 'ai');
            this.showChatWindow = false;
          } else {
            this.addMessage('抱歉，我暂时无法回答这个问题。请稍后再试。', 'ai');
          }
        } finally {
          this.loading = false;
          this.scrollToBottom();
        }
      },
      
      sendQuickSuggestion(suggestion) {
        this.userInput = suggestion;
        this.sendMessage();
      },
      
      addMessage(content, type) {
        const now = new Date();
        const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
        
        this.messages.push({
          content,
          type,
          time
        });
        
        this.scrollToBottom();
      },
      
      scrollToBottom() {
        this.$nextTick(() => {
          const chatBody = this.$refs.chatBody;
          if (chatBody) {
            chatBody.scrollTop = chatBody.scrollHeight;
          }
        });
      },
      
      formatMessage(content) {
        if (!content) return '';
        return content.replace(/\n/g, '<br>');
      }
    },
    mounted() {
      if (this.isLoggedIn) {
        aiApi.checkHealth().then(response => {
          if (response.code !== 200) {
            console.warn('AI服务可能不可用');
          }
        }).catch(() => {
          console.warn('AI健康检查失败');
        });
      }
    }
  };
  </script>



<style scoped>
/* ============ 主容器 ============ */
.ai-assistant-container {
  position: fixed;
  bottom: 32px;
  right: 32px;
  z-index: 9999;
}

/* ============ 可拖拽按钮 ============ */
.ai-floating-btn {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-size: 28px;
  cursor: grab;
  box-shadow: 
    0 10px 25px rgba(102, 126, 234, 0.4),
    0 6px 15px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  touch-action: none;
  animation: float 3s ease-in-out infinite;
}

.ai-floating-btn:hover {
  transform: scale(1.08) translateY(-2px);
  box-shadow: 
    0 15px 30px rgba(102, 126, 234, 0.5),
    0 8px 20px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.ai-floating-btn:active {
  cursor: grabbing;
  transform: scale(0.98);
  box-shadow: 
    0 5px 15px rgba(102, 126, 234, 0.3),
    0 3px 10px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.ai-floating-btn::before {
  content: '';
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea22 0%, #764ba222 100%);
  z-index: -1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.ai-floating-btn:hover::before {
  opacity: 1;
}

/* 按钮浮动动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-8px);
  }
}

/* ============ 聊天窗口 ============ */
.ai-chat-window {
  position: absolute;
  bottom: 96px;
  right: 0;
  width: 420px;
  height: 580px;
  background: white;
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 10px 30px rgba(0, 0, 0, 0.1),
    0 1px 0 rgba(255, 255, 255, 0.5);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  opacity: 0;
  transform: translateY(20px) scale(0.95);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

@keyframes slideUp {
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ============ 聊天头部 ============ */
.chat-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.chat-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  animation: shine 20s linear infinite;
  opacity: 0.1;
}

@keyframes shine {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.chat-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-header h3::before {
  content: '✨';
  font-size: 20px;
}

.close-btn {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  color: white;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(90deg);
}

/* ============ 聊天主体 ============ */
.chat-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: linear-gradient(165deg, #f8fafc 0%, #f1f5f9 100%);
  scroll-behavior: smooth;
}

/* 自定义滚动条 */
.chat-body::-webkit-scrollbar {
  width: 6px;
}

.chat-body::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.chat-body::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #667eea, #764ba2);
  border-radius: 3px;
}

.chat-body::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #5a67d8, #6b46c1);
}

/* ============ 欢迎消息 ============ */
.welcome-message {
  background: white;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 
    0 8px 30px rgba(0, 0, 0, 0.06),
    0 2px 10px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.05);
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-message p {
  margin: 0 0 16px 0;
  font-size: 15px;
  line-height: 1.5;
  color: #4a5568;
}

.welcome-message ul {
  margin: 0 0 16px 0;
  padding-left: 20px;
}

.welcome-message li {
  margin: 8px 0;
  font-size: 14px;
  color: #718096;
  display: flex;
  align-items: center;
  gap: 8px;
}

.welcome-message li::before {
  content: '';
  width: 6px;
  height: 6px;
  background: #667eea;
  border-radius: 50%;
  flex-shrink: 0;
}

/* ============ 消息样式 ============ */
.message {
  margin-bottom: 20px;
  max-width: 85%;
  animation: messageSlide 0.3s ease-out forwards;
  opacity: 0;
}

@keyframes messageSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  margin-left: auto;
  animation-delay: 0.1s;
}

.message.ai {
  margin-right: auto;
  animation-delay: 0.1s;
}

.message-content {
  padding: 14px 18px;
  border-radius: 18px;
  word-wrap: break-word;
  line-height: 1.5;
  font-size: 14.5px;
  position: relative;
  transition: transform 0.2s ease;
}

.message-content:hover {
  transform: translateY(-1px);
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 
    0 4px 15px rgba(102, 126, 234, 0.3),
    0 2px 8px rgba(0, 0, 0, 0.1);
}

.message.ai .message-content {
  background: white;
  color: #2d3748;
  border-bottom-left-radius: 4px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

/* 消息时间戳 */
.message-time {
  font-size: 11px;
  color: #a0aec0;
  margin-top: 6px;
  text-align: right;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.message.ai .message-time {
  text-align: left;
}

/* ============ 加载动画 ============ */
.typing {
  display: flex;
  align-items: center;
  height: 24px;
  padding: 0 8px;
}

.dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0 3px;
  animation: typing 1.4s infinite ease-in-out;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.3);
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { 
    transform: scale(0);
    opacity: 0.3;
  }
  40% { 
    transform: scale(1);
    opacity: 1;
  }
}

/* ============ 输入区域 ============ */
.chat-input {
  display: flex;
  padding: 20px 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  background: white;
  align-items: flex-end;
  gap: 12px;
}

.chat-input textarea {
  flex: 1;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  padding: 14px 20px;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  font-size: 15px;
  line-height: 1.5;
  background: #f8fafc;
  transition: all 0.3s ease;
  box-shadow: 
    inset 0 2px 4px rgba(0, 0, 0, 0.02),
    0 1px 0 rgba(255, 255, 255, 0.8);
}

.chat-input textarea:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 
    0 0 0 3px rgba(102, 126, 234, 0.1),
    inset 0 2px 4px rgba(0, 0, 0, 0.02);
}

.chat-input textarea::placeholder {
  color: #a0aec0;
}

.send-btn {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 6px 20px rgba(102, 126, 234, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 
    0 10px 25px rgba(102, 126, 234, 0.5),
    0 4px 12px rgba(0, 0, 0, 0.15);
}

.send-btn:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.send-btn:disabled {
  background: linear-gradient(135deg, #cbd5e0 0%, #a0aec0 100%);
  cursor: not-allowed;
  box-shadow: none;
}

.send-btn::before {
  content: '→';
}

/* ============ 快捷建议 ============ */
.quick-suggestions {
  padding: 16px 24px;
  background: rgba(248, 250, 252, 0.8);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  backdrop-filter: blur(10px);
}

.suggestion-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.04),
    0 1px 2px rgba(0, 0, 0, 0.02);
  white-space: nowrap;
}

.suggestion-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 
    0 8px 20px rgba(102, 126, 234, 0.3),
    0 4px 12px rgba(0, 0, 0, 0.08);
}

.suggestion-btn:active {
  transform: translateY(0);
}

/* ============ 响应式设计 ============ */
@media (max-width: 480px) {
  .ai-assistant-container {
    bottom: 20px;
    right: 20px;
  }
  
  .ai-floating-btn {
    width: 64px;
    height: 64px;
    font-size: 24px;
  }
  
  .ai-chat-window {
    width: calc(100vw - 40px);
    max-width: 380px;
    height: 500px;
    bottom: 84px;
    right: 0;
  }
  
  .chat-body {
    padding: 20px;
  }
  
  .chat-input {
    padding: 16px 20px;
  }
  
  .quick-suggestions {
    padding: 14px 20px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .ai-chat-window {
    background: #1a202c;
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .chat-body {
    background: linear-gradient(165deg, #171923 0%, #1a202c 100%);
  }
  
  .welcome-message {
    background: #2d3748;
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .welcome-message p,
  .welcome-message li {
    color: #cbd5e0;
  }
  
  .message.ai .message-content {
    background: #2d3748;
    color: #e2e8f0;
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .chat-input {
    background: #1a202c;
    border-top-color: rgba(255, 255, 255, 0.1);
  }
  
  .chat-input textarea {
    background: #2d3748;
    color: #e2e8f0;
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .quick-suggestions {
    background: rgba(26, 32, 44, 0.8);
  }
  
  .suggestion-btn {
    background: #2d3748;
    color: #cbd5e0;
    border-color: rgba(255, 255, 255, 0.1);
  }
}
</style>