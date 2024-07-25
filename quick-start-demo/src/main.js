import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './mock/index'; // 引入Mock.js配置

createApp(App).use(router).mount('#app');