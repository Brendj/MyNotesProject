/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class TaskExecutorsConfig {

    @Value(value = "${spring.task.execution.transit.cron}")
    private String transitCronExp;

    @Bean
    public CronTrigger transitCronTrigger(){
        return new CronTrigger(transitCronExp);
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTransitTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadGroupName("TransitClientDiscountHistory");
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTransitClientDiscountHistoryTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
