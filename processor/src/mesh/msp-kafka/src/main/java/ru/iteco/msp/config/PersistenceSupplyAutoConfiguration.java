/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.iteco.msp.repo.supply",
        entityManagerFactoryRef = "supplyEntityManager",
        transactionManagerRef = "supplyTransactionManager")
@EnableTransactionManagement
public class PersistenceSupplyAutoConfiguration {

    @Bean(name = "supplyEntityManager")
    public LocalContainerEntityManagerFactoryBean supplyEntityManager(
            @Qualifier("supplyDataSource") DataSource supplyDataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(supplyDataSource)
                .packages("ru.iteco.msp")
                .persistenceUnit("supplyPU")
                .build();
    }

    @Bean("supplyDataSource")
    @ConfigurationProperties(prefix="spring.supply-datasource")
    public DataSource supplyDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "supplyTransactionManager")
    public PlatformTransactionManager supplyTransactionManager(
            @Qualifier("supplyEntityManager") LocalContainerEntityManagerFactoryBean supplyEntityManager) {
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(supplyEntityManager.getObject());
        return transactionManager;
    }
}
