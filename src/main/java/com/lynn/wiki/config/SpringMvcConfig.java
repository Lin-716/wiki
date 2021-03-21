package com.lynn.wiki.config;

import com.lynn.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    //注入拦截器
    @Resource
    LogInterceptor logInterceptor;

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**").excludePathPatterns("/login");
        //增加所有请求再去掉某些请求
    }
}
