package com.zbw.controller;

import com.zbw.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping("/insert")
    private String getAllKeys(){
        cacheUtil.set("ttt",1234);
        return "success";
    }

    @RequestMapping("/setNewValue")
    private String setKey(){
        cacheUtil.set("ttt",6666);
        return "success";
    }

    /**
     * http://localhost:8082/getKeyValue
     */
    @RequestMapping("/getKeyValue")
    private String getKeyValue(){
        String ttt = cacheUtil.get("ttt");
        return ttt;
    }

}
