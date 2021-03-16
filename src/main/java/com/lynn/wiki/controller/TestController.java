package com.lynn.wiki.controller;

import com.lynn.wiki.domain.Test;
import com.lynn.wiki.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController //用来返回字符串 = controller + responsebody(返回字符串/json对象)
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/hello")//接口的请求地址
    public String hello(){
        return "hello world1";
    }

    @GetMapping("/test/list")//接口的请求地址
    public List<Test> list(){
        return testService.list();
    }
}
