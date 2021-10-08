/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.config;

import ru.iteco.restservice.property.ReadOnlyDbProperty;
import ru.iteco.restservice.property.SwaggerProperty;
import ru.iteco.restservice.property.WritableDbProperty;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SwaggerProperty.class,
        WritableDbProperty.class,
        ReadOnlyDbProperty.class
})
public class PropertyConfig {

}
