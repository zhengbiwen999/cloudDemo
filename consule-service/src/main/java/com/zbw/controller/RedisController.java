package com.zbw.controller;

import com.zbw.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping("/getRedis")
    public String getRedis(){
        String aaaa = cacheUtil.get("aaaa");
        return aaaa;
    }
}


