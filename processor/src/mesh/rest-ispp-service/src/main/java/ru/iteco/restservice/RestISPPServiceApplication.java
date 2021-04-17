/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice;

import ru.iteco.restservice.property.SwaggerProperty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SwaggerProperty.class})
public class RestISPPServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(RestISPPServiceApplication.class, args);
    }
}
