package com.lynn.wiki.controller;

import com.lynn.wiki.domain.Demo;
import com.lynn.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @GetMapping("/list")//接口的请求地址
    public List<Demo> list(){
        return demoService.list();
    }
}
