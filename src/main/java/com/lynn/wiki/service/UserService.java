package com.lynn.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lynn.wiki.domain.User;
import com.lynn.wiki.domain.UserExample;
import com.lynn.wiki.exception.BusinessException;
import com.lynn.wiki.exception.BusinessExceptionCode;
import com.lynn.wiki.mapper.UserMapper;
import com.lynn.wiki.req.UserQueryReq;
import com.lynn.wiki.req.UserSaveReq;
import com.lynn.wiki.resp.UserQueryResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.util.CopyUtil;
import com.lynn.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){
        PageHelper.startPage(1,3);
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        //动态sql
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(),req.getSize());//只会对第一个查询进行分页
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数{}",pageInfo.getTotal());
        LOG.info("总页数{}",pageInfo.getPages());

        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    //保存
    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(req.getId())){
            User userDB = selectByLoginName(req.getLoginName());
            if(ObjectUtils.isEmpty(userDB)){
                //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else{
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else{
            //更新
            userMapper.updateByPrimaryKeySelective(user);//Selective表示user有值才去更新
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(userList)){
            return null;
        }else{
            return userList.get(0);
        }
    }
}
