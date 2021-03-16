package com.lynn.wiki.service;

import com.lynn.wiki.domain.Test;
import com.lynn.wiki.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {

    @Resource
    @Autowired
    private TestMapper testMapper;

    public List<Test> list(){
        return testMapper.list();
    }


}
