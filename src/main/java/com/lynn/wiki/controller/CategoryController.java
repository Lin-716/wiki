package com.lynn.wiki.controller;

import com.lynn.wiki.req.CategoryQueryReq;
import com.lynn.wiki.req.CategorySaveReq;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.resp.CategoryQueryResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/all")//接口的请求地址
    public CommonResp all(){//@valid开启校验规则
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")//保存编辑修改的电子书信息
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")//保存编辑修改的电子书信息
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
