package com.lynn.wiki.controller;

import com.lynn.wiki.req.EbookSaveReq;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.req.EbookQueryReq;
import com.lynn.wiki.resp.EbookResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")//接口的请求地址
    public CommonResp list(EbookQueryReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")//保存编辑修改的电子书信息
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
