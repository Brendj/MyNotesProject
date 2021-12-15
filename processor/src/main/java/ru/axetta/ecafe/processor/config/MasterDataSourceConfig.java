package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Created by nuc on 29.07.2020.
 */
@Configuration
@EnableJpaRepositories(basePackages = "ru.axetta.ecafe.processor",
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef= "masterTransactionManager")
@EntityScan(basePackages = "ru.axetta.ecafe.processor.core.persistence.*")
@ComponentScan(basePackages = "ru.axetta.ecafe.processor.core.persistence.*")
public class MasterDataSourceConfig {
    @Bean(name = "masterDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @PersistenceContext(unitName = "processorPU")
    @Primary
    @Bean(name = "masterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        //LocalContainerEntityManagerFactoryBean a = builder.dataSource(masterDataSource())
        return builder.dataSource(masterDataSource())
                .persistenceUnit("processorPU")
                .packages("ru.axetta.ecafe.processor")
                .build();
        //a.setPackagesToScan("ru.axetta.ecafe.processor.core.persistence");
        //return a;
    }

    @Bean
    @Primary
    @Qualifier("txManager")
    public PlatformTransactionManager masterTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(masterEntityManagerFactory(builder).getObject());
        tm.setDataSource(masterDataSource());
        return tm;
    }
}
