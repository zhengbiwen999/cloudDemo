package com.zbw.filter;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinsterConfig {

    @Bean
    public ServletListenerRegistrationBean<TestLinster> linsten(){
        ServletListenerRegistrationBean<TestLinster> listener = new ServletListenerRegistrationBean<>(new TestLinster());
        return listener;
    }


}
