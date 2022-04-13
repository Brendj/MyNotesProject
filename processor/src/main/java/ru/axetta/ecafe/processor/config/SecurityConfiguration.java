package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.axetta.ecafe.processor.beans.authentication.provider.ProcessingJaasAuthenticationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.login.ProcessingLoginModule;
import ru.axetta.ecafe.processor.web.partner.meals.security.MealsJwtFilter;
import ru.axetta.ecafe.processor.web.token.security.jwt.JWTAuthenticationManager;
import ru.axetta.ecafe.processor.web.token.security.jwt.JwtConfigurer;
import ru.axetta.ecafe.processor.web.token.security.jwt.JwtTokenProvider;

/**
 * Created by nuc on 26.10.2020.
 */

@Configuration
@EnableWebSecurity()
public class SecurityConfiguration {

    @Order(1)
    @Configuration
    public static class RestConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private JwtConfigurer jwtConfigurer;

        @Override
        protected void configure(HttpSecurity security) throws Exception {
            security
                    .antMatcher("/school/api/**")
                    .cors()
                    .and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/school/api/v1/authorization/**", "/school/api/v1/payments/**")
                    .permitAll()
                    .antMatchers("/school/api/v1/infos/**")
                    .hasAnyAuthority(User.DefaultRole.WA_ADMIN_SECURITY.name(), User.WebArmRole.WA_OEE.name(),
                                     User.WebArmRole.WA_OPP.name(), User.WebArmRole.WA_OPP_OEE.name())
                    .antMatchers("/school/api/v1/**")
                    .hasAnyAuthority(User.WebArmRole.WA_OEE.name(), User.WebArmRole.WA_OPP.name(), User.WebArmRole.WA_OPP_OEE.name())
                    .anyRequest().authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().apply(jwtConfigurer);
        }
    }


    @Order(2)
    @Configuration
    public static class WebConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private ProcessingJaasAuthenticationProvider processingJaasAuthenticationProvider;

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(processingJaasAuthenticationProvider);
        }

        @Override
        public void configure(final HttpSecurity security) throws Exception {
            security.authorizeRequests().antMatchers("/javax.faces.resource/**")
                    .permitAll()
                    .antMatchers("/back-office/styles.css").permitAll();

            security
                    .authorizeRequests()
                    .antMatchers("/processor/sync").permitAll()
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

            security.sessionManagement().invalidSessionStrategy(((request, response) -> {
                String loginUrl = request.getContextPath() + "/back-office/login.faces";
                if("partial/ajax".equals(request.getHeader("Faces-Request"))) {
                    response.setContentType("text/xml");
                    response.getWriter()
                            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                            .printf("<partial-response><redirect url=\"%s\"></redirect></partial-response>",
                                    loginUrl);
                } else{
                    response.sendRedirect(loginUrl);
                }
            }));

            security.authorizeRequests().antMatchers("/back-office/index.faces").authenticated()
                    .antMatchers("/back-office/admin/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_ADMIN)
                    .antMatchers("/back-office/director/index.faces").hasAnyAuthority(ProcessingLoginModule.ROLENAME_DIRECTOR)
                    .and()
                    .csrf().disable();

            security.cors();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/back-office/login.faces");
        }

        @Bean
        @Override
        public UserDetailsService userDetailsService() {
            UserDetails user =
                    org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
                                                                      .username("us")
                                                                      .password("pa")
                                                                      .roles("US")
                                                                      .build();

            return new InMemoryUserDetailsManager(user);
        }
    }

    @Order(3)
    @Configuration
    public static class MealsRestConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private MealsJwtFilter mealsJwtFilter;

        @Override
        protected void configure(HttpSecurity security) throws Exception {
            security
                    .antMatcher("/ispp/meals/**")
                    .cors()
                    .and()
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(mealsJwtFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
