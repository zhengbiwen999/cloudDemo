package com.zbw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {

//    @Value("${redis.sentinel.group}")
//    private String sentinelGroupName;
//
//    @Value("${redis.sentinel.nodes}")
//    private String sentinelNodes;

    private int maxTotal;

    private int minIdle;

    private int maxIdle;

    private long maxWaitMillis;

    private boolean testOnBorrow = true;

    private boolean testOnReturn = true;

    private String password;

    private int timeout;

//    @Bean
//    public RedisSentinelConfiguration redisSentinelConfiguration() {
//        String[] nodes = sentinelNodes.split(",");
//        Set<String> setNodes = new HashSet<String>();
//        for (String n : nodes) {
//            setNodes.add(n.trim());
//        }
//        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration(sentinelGroupName, setNodes);
//        return configuration;
//    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        return poolConfig;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean(name = "redisTemplate00")
    @Primary
    public StringRedisTemplate redisTemplate00() {
        return buildRedisTemplate(buildConnectionFactory(0));
    }

    @Bean(name = "redisTemplate01")
    public StringRedisTemplate redisTemplate01() {
        return buildRedisTemplate(buildConnectionFactory(1));
    }

    @Bean(name = "redisTemplate02")
    public StringRedisTemplate redisTemplate02() {
        return buildRedisTemplate(buildConnectionFactory(2));
    }

    @Bean(name = "redisTemplate03")
    public StringRedisTemplate redisTemplate03() {
        return buildRedisTemplate(buildConnectionFactory(3));
    }

    @Bean(name = "redisTemplate04")
    public StringRedisTemplate redisTemplate04() {
        return buildRedisTemplate(buildConnectionFactory(4));
    }

    private JedisConnectionFactory buildConnectionFactory(int database) {
//        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
//        connectionFactory.setUsePool(true);
//        connectionFactory.setTimeout(timeout);
//        connectionFactory.setDatabase(database);
//        connectionFactory.setPassword(password);
//        connectionFactory.afterPropertiesSet();
//        return connectionFactory;

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration ();
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        //  connection timeout
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(timeout));

        JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig());
        return factory;

    }

    protected StringRedisTemplate buildRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(stringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

}

