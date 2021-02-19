/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit;

import ru.iteco.transit.taskexecutor.TransitTaskExecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TransitDiscountsHistoryApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TransitDiscountsHistoryApplication.class, args);
        TransitTaskExecutor executor = ctx.getBean(TransitTaskExecutor.class);
        executor.run();
    }
}
