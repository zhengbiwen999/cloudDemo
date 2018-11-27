package com.zbw.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubListener {

//    @Autowired
//    private OrderControllerRms orderControllerRms;

    @Bean
    RedisMessageListenerContainer keyExpirationListener(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();

        listenerContainer.setConnectionFactory(connectionFactory);

        listenerContainer.addMessageListener((message, pattern) -> {
            //获得key
            String key = String.valueOf(message);

            //业务逻辑处理 TODO

        }, new PatternTopic("__keyevent@10__:expired"));  //监听10号库过期key
        return listenerContainer;
    }


}
