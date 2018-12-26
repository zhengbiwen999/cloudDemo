package com.zbw.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

//    @Bean(value = "threadPool")
//    public ExecutorService buildThreadPool(){
//        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
//                .setNameFormat("consumer-queue-thread-zbw-%d").build();
//
//        ExecutorService pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(5),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
//
//        return pool ;
//    }



}
