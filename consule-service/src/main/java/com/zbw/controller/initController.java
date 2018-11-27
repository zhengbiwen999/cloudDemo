package com.zbw.controller;

import com.zbw.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class initController {

    @Autowired
    private TestConfig config;

    @Value("${zbwName}")
    private String zbwName;

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }

    @RequestMapping("/getConfig")
    public String getConfig(){
        String env = config.getEnv();
        String value = config.getValue();
        return "获取的环境是："+env+" ,值是："+value+",name 是："+zbwName;
    }
}
