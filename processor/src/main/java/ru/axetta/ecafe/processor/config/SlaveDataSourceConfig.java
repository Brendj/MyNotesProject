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
        entityManagerFactoryRef = "slaveEntityManagerFactory",
        transactionManagerRef= "slaveTransactionManager")
public class SlaveDataSourceConfig {
    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @PersistenceContext(unitName = "reportsPU")
    @Bean(name = "slaveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(slaveDataSource())
                .persistenceUnit("reportsPU")
                //.properties(jpaProperties())
                .packages("ru.axetta.ecafe.processor").build();
    }

    @Bean
    @Qualifier("txManagerReports")
    public PlatformTransactionManager slaveTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(slaveEntityManagerFactory(builder).getObject());
        tm.setDataSource(slaveDataSource());
        return tm;
    }
}
