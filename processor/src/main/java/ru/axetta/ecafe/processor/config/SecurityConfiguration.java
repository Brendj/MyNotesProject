package ru.axetta.ecafe.processor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.axetta.ecafe.processor.core.persistence.User;

/**
 * Created by nuc on 26.10.2020.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(final HttpSecurity security) throws Exception {

        security.requestMatchers()
                .antMatchers("/processor/sync")
                .and()
                .authorizeRequests()
                .antMatchers("/processor/sync", "/school/api/v1/authorization/**").permitAll()
                .antMatchers("/school/api/v1/**").hasAnyAuthority(User.WebArmRole.WA_OEE.getDescription(),
                User.WebArmRole.WA_OPP.getDescription(),
                User.WebArmRole.WA_OPP_OEE.getDescription())
                .anyRequest() .authenticated()
                .and().x509()
                .and()
                .csrf().disable();

        security.cors();
    }
}
