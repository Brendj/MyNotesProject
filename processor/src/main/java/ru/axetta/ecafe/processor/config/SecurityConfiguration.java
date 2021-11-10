package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.axetta.ecafe.processor.beans.authentication.provider.ProcessingJaasAuthenticationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.login.ProcessingLoginModule;

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

        security.formLogin().loginPage("/back-office/login.xhtml").permitAll()
                .loginProcessingUrl("/back-office/j_spring_security_check")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .failureUrl("/back-office/login.xhtml?error=true")
                .defaultSuccessUrl("/back-office/index.faces")
                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/back-office/login.xhtml?logout")
                .invalidateHttpSession(true);

        security.authorizeRequests().antMatchers("/back-office/index.faces").authenticated()
                .antMatchers("/back-office/admin/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_ADMIN)
                .antMatchers("/back-office/director/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_DIRECTOR)
                .and()
                .csrf().disable();
        security.cors();
    }

}
