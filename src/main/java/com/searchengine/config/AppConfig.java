package com.searchengine.config;

import com.searchengine.distributed.NodeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NodeManager nodeManager() {
        return new NodeManager(); // create and register NodeManager as a Spring Bean
    }
}
