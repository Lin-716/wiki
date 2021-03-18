package com.lynn.wiki.service;

import com.lynn.wiki.domain.Ebook;
import com.lynn.wiki.domain.EbookExample;
import com.lynn.wiki.mapper.EbookMapper;
import com.lynn.wiki.req.EbookReq;
import com.lynn.wiki.resp.EbookResp;
import com.lynn.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
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

        return list;
    }
}
