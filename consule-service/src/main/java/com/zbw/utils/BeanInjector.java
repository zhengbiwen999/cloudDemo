package com.zbw.utils;

import com.zbw.cache.CacheUtil;
import com.zbw.cache.RedisTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BeanInjector extends AbstractInjector {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    private RetryTemplate retryTemplate(){
        return getRetryTemplate();
    }


    @Bean
    private CacheUtil cacheUtil() {
        RedisTemplateUtil cacheUtil = new RedisTemplateUtil();
        cacheUtil.setRetryTemplate(retryTemplate());
        cacheUtil.setStringRedisTemplate(stringRedisTemplate);
        return cacheUtil;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
