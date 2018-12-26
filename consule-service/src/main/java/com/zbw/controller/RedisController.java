package com.zbw.controller;

import com.zbw.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.Set;

@RestController
public class RedisController {

    @Autowired
    private CacheUtil cacheUtil;

    Random random = new Random();

    @RequestMapping("/getRedis")
    public String getRedis(){
        cacheUtil.set("aaaa","zbw");
        String aaaa = cacheUtil.get("aaaa");
        return aaaa;
    }


    @RequestMapping("/lpush")
    public List<String> lpush(){

        for(int i = 0 ;i<=10;i++){
            cacheUtil.lPush("zbw","用户zbw"+i);
        }
        List<String> zbw = cacheUtil.lRange("zbw", 0, 3);
        return zbw;
    }


    @RequestMapping("/zadd")
    public Set<String> zadd(){
        cacheUtil.delete("user");
        for(int i = 0 ;i<=10;i++){
            int source = i+random.nextInt(15);
            System.out.println(source);
            cacheUtil.zadd("user",source,"用户user"+i+":source:==>"+source);
        }
        Set<String> zbw = cacheUtil.zrange("user",0,8);
        return zbw;
    }
}


