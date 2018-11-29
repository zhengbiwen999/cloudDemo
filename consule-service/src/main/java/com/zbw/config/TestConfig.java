package com.zbw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@Component
@ConfigurationProperties(prefix ="app")
public class TestConfig {

    //环境
    private String env;

    //值
    private String value;
}
