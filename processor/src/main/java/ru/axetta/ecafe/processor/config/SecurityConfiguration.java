package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.axetta.ecafe.processor.beans.authentication.provider.ProcessingJaasAuthenticationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 26.10.2020.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private ProcessingJaasAuthenticationProvider processingJaasAuthenticationProvider;

    @Override
    public void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(processingJaasAuthenticationProvider);
    }


    @Override
    public void configure(final HttpSecurity security) throws Exception {
        security.authorizeRequests().antMatchers("/javax.faces.resource/**")
                .permitAll();

        security/*.requestMatchers()
                .antMatchers("/processor/sync", "/processor/back-office")
                .and()*/
                .authorizeRequests()
                .antMatchers("/processor/sync", "/school/api/v1/authorization/**").permitAll()
                .antMatchers("/school/api/v1/**")
                .hasAnyAuthority(User.WebArmRole.WA_OEE.getDescription(), User.WebArmRole.WA_OPP.getDescription(), User.WebArmRole.WA_OPP_OEE.getDescription())
                .and().x509();

        security.formLogin().loginPage("/login.xhtml").permitAll()
                .failureUrl("/login.xhtml?error=true")
                .and()
                .logout().logoutSuccessUrl("/login.xhtml").permitAll();

        security.authorizeRequests().antMatchers("/back-office/**")
                .hasAnyAuthority("CUSTOMER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
        security.cors();
    }

}
