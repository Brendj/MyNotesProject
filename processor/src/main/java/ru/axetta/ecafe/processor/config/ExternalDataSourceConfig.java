package ru.axetta.ecafe.processor.config;

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

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Created by nuc on 29.07.2020.
 */
@Configuration
@EnableJpaRepositories(//basePackages = "guru.springframework.multipledatasources.repository.card",
        entityManagerFactoryRef = "externalEntityManagerFactory",
        transactionManagerRef= "externalTransactionManager")

public class ExternalDataSourceConfig {
    @Bean(name = "externalDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.external")
    public DataSource externalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @PersistenceContext(unitName = "externalServicesPU")
    @Bean(name = "externalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean externalEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(externalDataSource())
                .persistenceUnit("externalServicesPU")
                //.properties(jpaProperties())
                .packages("ru.axetta.ecafe.processor").build();
    }

    @Bean
    @Qualifier("txManagerExternalServices")
    public PlatformTransactionManager externalTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(externalEntityManagerFactory(builder).getObject());
        tm.setDataSource(externalDataSource());
        return tm;
    }
}
