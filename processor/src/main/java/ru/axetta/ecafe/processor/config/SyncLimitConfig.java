package ru.axetta.ecafe.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.axetta.ecafe.processor.web.LimitFilter;

@Configuration
public class SyncLimitConfig {
    @Value("${sync.limit}")
    private int syncLimit;
    @Value("${sync.retry.after}")
    private int syncRetryAfter;

    @Bean
    public LimitFilterParams limitFilterParams() {
        return new LimitFilterParams(syncLimit, syncRetryAfter);
    }

    @Bean
    public FilterRegistrationBean limitFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(syncLimitFilter());
        registration.addUrlPatterns("/sync");
        registration.setName("limitFilter");
        registration.setOrder(1);
        return registration;
    }

    public LimitFilter syncLimitFilter() {
        return new LimitFilter();
    }
}
