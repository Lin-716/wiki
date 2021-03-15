package com.lynn.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //用来返回字符串 = controller + responsebody(返回字符串/json对象)
public class TestController {
    @GetMapping("/hello")//接口的请求地址
    public String hello(){
        return "hello world";
    }
}
