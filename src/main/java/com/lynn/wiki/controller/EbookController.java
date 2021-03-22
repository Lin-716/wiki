package com.lynn.wiki.controller;

import com.lynn.wiki.domain.Ebook;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.req.EbookReq;
import com.lynn.wiki.resp.EbookResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")//接口的请求地址
    public CommonResp list(EbookReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
