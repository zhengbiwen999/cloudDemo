package com.zbw.config;

import com.zbw.utils.RedisTemplateUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
    @Value("${spring.redis.sentinel.master}")
    protected String sentinelGroupName;

    @Value("${spring.redis.sentinel.nodes}")
    protected String sentinelNodes;

//    @Value("${spring.redis.pool.maxTotal}")
//    protected int maxTotal;
//
//    @Value("${spring.redis.pool.min-idle}")
//    protected int minIdle;
//
//    @Value("${spring.redis.pool.max-idle}")
//    protected int maxIdle;
//
//    @Value("${spring.redis.pool.max-wait}")
//    protected long maxWaitMillis;

//    @Value("${spring.redis.testOnBorrow}")
//    protected boolean testOnBorrow;
//
//    @Value("${spring.redis.testOnReturn}")
//    protected boolean testOnReturn;

//    pool:
//    max-active: ${redis.maxActive}
//    max-wait: ${redis.maxWait}
//    max-idle: ${redis.maxIdle}
//    min-idle: ${redis.minIdle}
//    sentinel:
//    master: ${redis.sentinel.master}
//    nodes: ${redis.sentinel.nodes}
//    database: 0
//    password:
//    timeout: ${redis.timeout}

    @Value("${spring.redis.password}")
    protected String password;

    @Value("${spring.redis.timeout}")
    protected int timeout;

    @Bean(name = "cacheUtil02")
    public RedisTemplateUtil redisTemplateUtil(@Qualifier("redisSessionTemplate02")StringRedisTemplate stringRedisTemplate, RetryTemplate retryTemplate) {
        RedisTemplateUtil redisTemplateUtil = new RedisTemplateUtil();
        redisTemplateUtil.setStringRedisTemplate(stringRedisTemplate);
        redisTemplateUtil.setRetryTemplate(retryTemplate);
        return  redisTemplateUtil;
    }

    @Bean(name = "redisSessionTemplate02")
    public StringRedisTemplate stringRedisTemplate() {
        return  buildRedisTemplate(buildConnectionFactory(1));
    }

    @Bean(name = "cacheUtil03")
    public RedisTemplateUtil redisTemplateUtil03(@Qualifier("redisSessionTemplat03")StringRedisTemplate stringRedisTemplate,RetryTemplate retryTemplate) {
        RedisTemplateUtil redisTemplateUtil = new RedisTemplateUtil();
        redisTemplateUtil.setStringRedisTemplate(stringRedisTemplate);
        redisTemplateUtil.setRetryTemplate(retryTemplate);
        return  redisTemplateUtil;
    }

    @Bean(name = "redisSessionTemplat03")
    public StringRedisTemplate stringRedisTemplate03() {
        return  buildRedisTemplate(buildConnectionFactory(3));
    }

    @Bean(name = "redisSessionTemplat10")
    public StringRedisTemplate stringRedisTemplate10() {
        return  buildRedisTemplate(buildConnectionFactory(10));
    }

    @Bean(name = "cacheUtil01")
    public RedisTemplateUtil redisTemplateUtil01(@Qualifier("redisSessionTemplate01")StringRedisTemplate stringRedisTemplate,RetryTemplate retryTemplate) {
        RedisTemplateUtil redisTemplateUtil = new RedisTemplateUtil();
        redisTemplateUtil.setStringRedisTemplate(stringRedisTemplate);
        redisTemplateUtil.setRetryTemplate(retryTemplate);
        return  redisTemplateUtil;
    }

    @Bean(name = "redisSessionTemplate01")
    public StringRedisTemplate stringRedisTemplate01() {
        return  buildRedisTemplate(buildConnectionFactory(0));
    }

    @Bean(name = "cacheUtil10")
    public RedisTemplateUtil redisTemplateUtil10(@Qualifier("redisSessionTemplat10")StringRedisTemplate stringRedisTemplate, RetryTemplate retryTemplate) {
        RedisTemplateUtil redisTemplateUtil = new RedisTemplateUtil();
        redisTemplateUtil.setStringRedisTemplate(stringRedisTemplate);
        redisTemplateUtil.setRetryTemplate(retryTemplate);
        return  redisTemplateUtil;
    }


    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
        timeoutRetryPolicy.setTimeout(500000);
        retryTemplate.setRetryPolicy(timeoutRetryPolicy);
        return retryTemplate;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(1);
        jedisPoolConfig.setMaxWaitMillis(50);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        return jedisPoolConfig;
    }


    private JedisConnectionFactory buildConnectionFactory(int database) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
        connectionFactory.setUsePool(true);
        connectionFactory.setTimeout(timeout);
        connectionFactory.setDatabase(database);
        connectionFactory.setPassword(password);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    protected StringRedisTemplate buildRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(stringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer()
    {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        String[] nodes = sentinelNodes.split(",");
        Set<String> setNodes = new HashSet<>();
        for (String n : nodes) {
            setNodes.add(n.trim());
        }
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration(sentinelGroupName, setNodes);
        return configuration;
    }

}