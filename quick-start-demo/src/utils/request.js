import axios from 'axios';

// 创建axios实例
const service = axios.create({
    baseURL: '', // 确保这里与Mock.js生成的URL一致
    timeout: 2000,
    headers: { 'X-Custom-Header': 'foobar' }
});

// 设置请求和应答拦截器
// 添加请求拦截器
service.interceptors.request.use(
    function (config) {
        console.log('请求拦截...');
        // 在发送请求之前做些什么
        return config;
    },
    function (error) {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 添加响应拦截器
service.interceptors.response.use(
    function (response) {
        console.log('应答拦截...');
        // 对响应数据做点什么
        return response.data;
    },
    function (error) {
        // 对响应错误做点什么
        return Promise.reject(error);
    }
);

// 导出请求对象
export default service;
