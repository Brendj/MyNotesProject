/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import ru.iteco.restservice.property.SwaggerProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_KEY_SCHEME = "apikey-scheme";

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
        OpenAPI api = new OpenAPI().info(metaData());

        Components components = new Components();
        components.addSecuritySchemes(API_KEY_SCHEME, getAPIKeySecurityScheme());
        api.setComponents(components);

        SecurityRequirement apikeyReq = new SecurityRequirement();
        apikeyReq.addList(API_KEY_SCHEME);
        api.addSecurityItem(apikeyReq);

        return api;
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

    private SecurityScheme getAPIKeySecurityScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setName("API-KEY");
        scheme.setIn(SecurityScheme.In.HEADER);
        return scheme;
    }
}
