package com.zbw.aop.db;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DbSlaveOrMasterAop {

    @Pointcut()
    public void doDbAop(){

    }



}
