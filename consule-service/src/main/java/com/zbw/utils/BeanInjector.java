package com.zbw.utils;

import com.zbw.cache.CacheUtil;
import com.zbw.cache.RedisTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Component
public class BeanInjector extends AbstractInjector {

//    @Value("${redis.password}")
//    protected String password;
//
//    @Value("${redis.timeout}")
//    protected int timeout;
//
//    @Value("${redis.sentinel.master}")
//    protected String sentinelGroupName;
//
//    @Value("${redis.sentinel.nodes}")
//    protected String sentinelNodes;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    private RetryTemplate retryTemplate() {
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


//    @Bean(name = "redisSessionTemplat03")
//    public StringRedisTemplate stringRedisTemplate03() {
//        return buildRedisTemplate(buildConnectionFactory(3));
//    }
//
//    @Bean(name = "cacheUtil03")
//    public RedisTemplateUtil redisTemplateUtil03(@Qualifier("redisSessionTemplat03") StringRedisTemplate stringRedisTemplate, RetryTemplate retryTemplate) {
//        RedisTemplateUtil redisTemplateUtil = new RedisTemplateUtil();
//        redisTemplateUtil.setStringRedisTemplate(stringRedisTemplate);
//        redisTemplateUtil.setRetryTemplate(retryTemplate);
//        return redisTemplateUtil;
//    }
//
//    protected StringRedisTemplate buildRedisTemplate(RedisConnectionFactory connectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(connectionFactory);
//        template.setValueSerializer(stringRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    private JedisConnectionFactory buildConnectionFactory(int database) {
//        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
//        connectionFactory.setUsePool(true);
//        connectionFactory.setTimeout(timeout);
//        connectionFactory.setDatabase(database);
//        connectionFactory.setPassword(password);
//        connectionFactory.afterPropertiesSet();
//        return connectionFactory;
//    }
//
//    @Bean
//    public RedisSentinelConfiguration redisSentinelConfiguration() {
//        String[] nodes = sentinelNodes.split(",");
//        Set<String> setNodes = new HashSet<>();
//        for (String n : nodes) {
//            setNodes.add(n.trim());
//        }
//        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration(sentinelGroupName, setNodes);
//        return configuration;
//    }
//
//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(20);
//        jedisPoolConfig.setMaxIdle(5);
//        jedisPoolConfig.setMinIdle(1);
//        jedisPoolConfig.setMaxWaitMillis(50);
//        jedisPoolConfig.setTestOnBorrow(true);
//        jedisPoolConfig.setTestOnReturn(true);
//        return jedisPoolConfig;
//    }
//
//    @Bean
//    public StringRedisSerializer stringRedisSerializer() {
//        return new StringRedisSerializer();
//    }

}
