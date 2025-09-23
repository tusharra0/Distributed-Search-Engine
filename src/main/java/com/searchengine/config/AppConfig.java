package com.searchengine.config;

import com.searchengine.distributed.NodeManager;
import com.searchengine.persistence.PersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PersistenceService persistenceService() {
        return new PersistenceService();
    }

    @Bean
    public NodeManager nodeManager(PersistenceService persistenceService) {
        return new NodeManager(persistenceService);
    }
}
