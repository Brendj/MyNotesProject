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
        entityManagerFactoryRef = "symmetricEntityManagerFactory",
        transactionManagerRef= "symmetricTransactionManager")
public class SymmetricDataSourceConfig {
    @Bean(name = "symmetricDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.symmetric")
    public DataSource symmetricDataSource() {
        return DataSourceBuilder.create().build();
    }

    @PersistenceContext(unitName = "symmetricPU")
    @Bean(name = "symmetricEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean symmetricEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(symmetricDataSource())
                .persistenceUnit("symmetricPU")
                //.properties(jpaProperties())
                .packages("ru.axetta.ecafe.processor").build();
    }

    @Bean
    @Qualifier("txManagerSymmetric")
    public PlatformTransactionManager symmetricTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(symmetricEntityManagerFactory(builder).getObject());
        tm.setDataSource(symmetricDataSource());
        return tm;
    }
}
