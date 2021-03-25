package com.lynn.wiki.controller;

import com.lynn.wiki.req.EbookSaveReq;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.req.EbookQueryReq;
import com.lynn.wiki.resp.EbookQueryResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")//接口的请求地址
    public CommonResp list(@Valid EbookQueryReq req){//@valid开启校验规则
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")//保存编辑修改的电子书信息
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")//保存编辑修改的电子书信息
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
