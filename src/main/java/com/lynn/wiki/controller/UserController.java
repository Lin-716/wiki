package com.lynn.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.lynn.wiki.req.UserLoginReq;
import com.lynn.wiki.req.UserQueryReq;
import com.lynn.wiki.req.UserResetPasswordReq;
import com.lynn.wiki.req.UserSaveReq;
import com.lynn.wiki.resp.CommonResp;
import com.lynn.wiki.resp.UserLoginResp;
import com.lynn.wiki.resp.UserQueryResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.service.UserService;
import com.lynn.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Resource
    private UserService userService;

    @Resource
    private SnowFlake snowFlake;//用雪花算法生成token

    @Resource
    private RedisTemplate redisTemplate;

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

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));//加密的32位16进制字符串
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        //生成单点登录token，并放入redis中
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录并放入token中{}",token);
        //ops操作，token为key,userLoginResp为value
        //userLoginResp类需要序列化，来做远程传输

        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable Long token){
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);

        return resp;
    }

}
