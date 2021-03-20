package com.lynn.wiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")//映射的请求地址
                .allowedOriginPatterns("*")//允许来源
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)//允许get，post等请求
                .allowCredentials(true)//允许凭证（cookie等
                .maxAge(3600);//1h内不需要再发options请求预检（检查接口是否存在或者正常
    }
}
