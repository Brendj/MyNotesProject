package ru.axetta.ecafe.processor.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.axetta.ecafe.processor.web.token.security.jwt.JWTAuthenticationFilter;

@Configuration
public class JwtConfig {
    @Bean
    public FilterRegistrationBean registerJwtFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(jwtFilter());
        registration.addUrlPatterns("/school/api/v1/*");
        registration.setName("jwtFilter");
        registration.setOrder(1);
        return registration;
    }

    public JWTAuthenticationFilter jwtFilter() {
        return new JWTAuthenticationFilter();
    }
}
