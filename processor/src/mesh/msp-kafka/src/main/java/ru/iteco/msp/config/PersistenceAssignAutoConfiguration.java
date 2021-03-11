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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.iteco.msp.repo.assign",
        entityManagerFactoryRef = "assignEntityManager",
        transactionManagerRef = "assignTransactionManager")
@EnableTransactionManagement
public class PersistenceAssignAutoConfiguration {

    @Primary
    @Bean(name = "assignEntityManager")
    public LocalContainerEntityManagerFactoryBean assignEntityManager(
            @Qualifier("assignDataSource") DataSource assignDataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(assignDataSource)
                .packages("ru.iteco.msp")
                .persistenceUnit("assignPU")
                .build();
    }

    @Primary
    @Bean("assignDataSource")
    @ConfigurationProperties(prefix="spring.assign-datasource")
    public DataSource assignDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "assignTransactionManager")
    public PlatformTransactionManager assignTransactionManager(
            @Qualifier("assignEntityManager") LocalContainerEntityManagerFactoryBean assignEntityManagerFactory) {
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(assignEntityManagerFactory.getObject());
        return transactionManager;
    }
}
