package ru.axetta.ecafe.processor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by nuc on 26.10.2020.
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(final HttpSecurity security) throws Exception {

        security.requestMatchers()
                .antMatchers("/processor/sync")
                .and()
                .authorizeRequests()
                .antMatchers("/processor/sync").permitAll()
                .and()
                .csrf().disable();

        security.cors();
    }
}
