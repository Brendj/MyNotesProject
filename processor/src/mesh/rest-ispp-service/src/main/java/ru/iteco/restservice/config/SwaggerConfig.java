/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import ru.iteco.restservice.property.SwaggerProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String[] AUTH_WHITELIST = {
            "/v3/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    private final SwaggerProperty swaggerProperty;

    public SwaggerConfig(SwaggerProperty swaggerProperty) {
        this.swaggerProperty = swaggerProperty;
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                /*.host(swaggerProperty.getHostUrl())
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.iteco.restservice"))*/
                .info(metaData());
    }

    private Info metaData() {
        return new Info()
                .title("ISPP REST API")
                .description("Описание REST API ИС \"Проход и Питание\"")
                .version("1.0.0")
                .license(new License()
                        .name("Apache License Version 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0\"")
                );
    }

    /*private ApiKey apiKey() {
        return new ApiKey("Token Access", HttpHeaders.AUTHORIZATION, SecurityScheme.In.HEADER.name());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(
                new SecurityReference("JWT", authorizationScopes));
    }*/
}
