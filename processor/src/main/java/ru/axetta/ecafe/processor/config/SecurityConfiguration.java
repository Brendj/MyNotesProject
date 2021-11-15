package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.axetta.ecafe.processor.beans.authentication.provider.ProcessingJaasAuthenticationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.login.ProcessingLoginModule;

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
                .permitAll()
                .antMatchers("/back-office/styles.css").permitAll();

        security/*.requestMatchers()
                .antMatchers("/processor/sync", "/processor/back-office")
                .and()*/
                .authorizeRequests()
                .antMatchers("/processor/sync", "/school/api/v1/authorization/**").permitAll()
                .antMatchers("/school/api/v1/**")
                .hasAnyAuthority(User.WebArmRole.WA_OEE.getDescription(), User.WebArmRole.WA_OPP.getDescription(), User.WebArmRole.WA_OPP_OEE.getDescription())
                .and().x509();

        security.formLogin().loginPage("/back-office/login.faces").permitAll()
                .loginProcessingUrl("/back-office/j_spring_security_check")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .failureUrl("/back-office/login.faces?error=true")
                .defaultSuccessUrl("/back-office/index.faces")
                .and()
                .logout().permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/j_spring_security_logout"))
                .logoutSuccessUrl("/back-office/login.faces")
                .invalidateHttpSession(true);

        security.authorizeRequests().antMatchers("/back-office/index.faces").authenticated()
                .antMatchers("/back-office/admin/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_ADMIN)
                .antMatchers("/back-office/director/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_DIRECTOR)
                .and()
                .csrf().disable();
        security.cors();
    }

}
