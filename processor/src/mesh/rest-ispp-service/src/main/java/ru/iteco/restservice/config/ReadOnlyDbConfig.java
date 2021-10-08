/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import ru.iteco.restservice.property.ReadOnlyDbProperty;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "readonlyEntityManager",
        transactionManagerRef = "readonlyTransactionManager",
        basePackages = {
                "ru.iteco.restservice.db.DAO.readonly",
                "ru.iteco.restservice.db.repo.readonly"
        })
public class ReadOnlyDbConfig {

    private final ReadOnlyDbProperty readonlyDbProperty;

    public ReadOnlyDbConfig(ReadOnlyDbProperty readonlyDbProperty){
        this.readonlyDbProperty = readonlyDbProperty;
    }

    @Bean
    public DataSource readonlyDataSource() {
        return DataSourceBuilder
                .create()
                .driverClassName(readonlyDbProperty.getDriverClassName())
                .url(readonlyDbProperty.getUrl())
                .username(readonlyDbProperty.getUsername())
                .password(readonlyDbProperty.getPassword())
                .build();
    }

    @Bean(name = "readonlyEntityManager")
    public LocalContainerEntityManagerFactoryBean readonlyEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(readonlyDataSource())
                .packages("ru.iteco.restservice.model")
                .persistenceUnit("readonlyPU").build();
    }

    @Bean(name = "readonlyTransactionManager")
    public PlatformTransactionManager readonlyTransactionManager(
            @Qualifier("readonlyEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
