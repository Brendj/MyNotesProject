/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import ru.iteco.restservice.property.WritableDbProperty;

import org.springframework.beans.factory.annotation.Qualifier;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "writableEntityManager",
        transactionManagerRef = "writableTransactionManager",
        basePackages = {
            "ru.iteco.restservice.db.DAO.writable",
            "ru.iteco.restservice.db.repo.writable"
        })
public class WritableDbConfig {

    private final WritableDbProperty writableDbProperty;

    public WritableDbConfig(WritableDbProperty writableDbProperty){
        this.writableDbProperty = writableDbProperty;
    }

    @Primary
    @Bean
    public DataSource writableDataSource() {
        return DataSourceBuilder
                .create()
                .driverClassName(writableDbProperty.getDriverClassName())
                .url(writableDbProperty.getUrl())
                .username(writableDbProperty.getUsername())
                .password(writableDbProperty.getPassword())
                .build();
    }

    @Primary
    @Bean(name = "writableEntityManager")
    public LocalContainerEntityManagerFactoryBean writableEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(writableDataSource())
                .packages("ru.iteco.restservice.model")
                .persistenceUnit("writablePU").build();
    }

    @Primary
    @Bean(name = "writableTransactionManager")
    public PlatformTransactionManager writableTransactionManager(
            @Qualifier("writableEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
