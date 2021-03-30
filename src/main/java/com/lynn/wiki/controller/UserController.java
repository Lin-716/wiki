package com.lynn.wiki.controller;

import com.lynn.wiki.req.UserQueryReq;
import com.lynn.wiki.req.UserResetPasswordReq;
import com.lynn.wiki.req.UserSaveReq;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.resp.UserQueryResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")//接口的请求地址
    public CommonResp list(@Valid UserQueryReq req){//@valid开启校验规则
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")//保存编辑修改的电子书信息
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));//加密的32位16进制字符串
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")//保存编辑修改的电子书信息
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")//保存编辑修改的电子书信息
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));//加密的32位16进制字符串
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

}
