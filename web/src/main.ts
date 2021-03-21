import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import * as Icons from '@ant-design/icons-vue'
import axios from 'axios'


axios.defaults.baseURL = process.env.VUE_APP_SERVER

const app = createApp(App)
app.use(store).use(router).use(Antd).mount('#app')

// 全局使用图标
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

//test multi-environment
console.log('environment:client', process.env.NODE_ENV)
console.log('environment:server', process.env.VUE_APP_SERVER)

//axios拦截器
//拦截请求
axios.interceptors.request.use(function(config){
    console.log('request param:' + config)
    return config
},error => {
    return Promise.reject(error)
})
//拦截返回
axios.interceptors.response.use(function(response){
    console.log('response' + response)
    return response
},error => {
    return Promise.reject(error)
})