package com.lynn.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lynn.wiki.domain.Ebook;
import com.lynn.wiki.domain.EbookExample;
import com.lynn.wiki.mapper.EbookMapper;
import com.lynn.wiki.req.EbookQueryReq;
import com.lynn.wiki.req.EbookSaveReq;
import com.lynn.wiki.resp.EbookResp;
import com.lynn.wiki.resp.PageResp;
import com.lynn.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    @Autowired
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookQueryReq req){
        PageHelper.startPage(1,3);
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //动态sql
        if(!ObjectUtils.isEmpty(req.getName())){
            //模糊查询
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(),req.getSize());//只会对第一个查询进行分页
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数{}",pageInfo.getTotal());
        LOG.info("总页数{}",pageInfo.getPages());
//
//        //复制整个列表-ebook中的实体在resp中循环再get出来
//        List<EbookResp> respList = new ArrayList<>();
//        for ( Ebook ebook : ebookList){
////            EbookResp ebookResp = new EbookResp();
////            //ebookResp.setId(ebook.getId());用beanutils就不用一个个属性打
////            BeanUtils.copyProperties(ebook, ebookResp);
//             //对象复制
//            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
//
//            respList.add(ebookResp);
//        }

        //列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);

        PageResp<EbookResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    //保存
    public void save(EbookSaveReq req){
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            ebookMapper.insert(ebook);
        }else{
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
