package com.zbw.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

//public class DataSource {
//
////    @Primary
////    @Bean
////    @ConfigurationProperties("spring.datasource.druid")
////    public DruidDataSource dataSourceOne(){
////        return DruidDataSourceBuilder.create().build();
////    }
//
//}
@Configuration
@Profile("dev")
public class DataSource {

    @Bean
    public DruidDataSource dataSource(Environment environment) {
        return DruidDataSourceBuilder
                .create()
                .build(environment, "spring.datasource.druid.");
    }
//    @Primary
//    @Bean
//    @ConfigurationProperties("spring.datasource.druid")
//    public DruidDataSource dataSourceOne() {
//        return DruidDataSourceBuilder.create().build();
//    }
}