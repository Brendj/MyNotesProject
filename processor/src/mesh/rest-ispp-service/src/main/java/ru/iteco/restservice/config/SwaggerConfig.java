/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                            .title("REST ISPP API")
                            .version("1.0.0")
                );
    }

}
