package com.simulation.global.config;

import com.simulation.global.config.monitoring.QueryCountInspector;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public StatementInspector statementInspector() {
        return new QueryCountInspector();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(StatementInspector inspector) {
        return props -> props.put(AvailableSettings.STATEMENT_INSPECTOR, inspector);
    }
}