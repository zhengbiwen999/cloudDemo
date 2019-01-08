package com.zbw.controller;

import com.zbw.mapper.AreaEntityMapper;
import com.zbw.model.AreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RefreshScope
public class DbTestController {

    @Autowired
    AreaEntityMapper areaEntityMapper;

    @RequestMapping("/getData")
    public String getData(){
        List<AreaEntity> areaEntities = areaEntityMapper.selectAll();
        System.out.println(areaEntities.size());
        return "success";
    }
}


