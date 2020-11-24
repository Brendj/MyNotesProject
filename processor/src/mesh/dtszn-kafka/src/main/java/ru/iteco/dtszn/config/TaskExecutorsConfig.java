/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class TaskExecutorsConfig {

    @Value(value = "${spring.task.execution.supply.cron}")
    private String supplyCronExp;

    @Value(value = "${spring.task.execution.assign.cron}")
    private String assignCronExp;

    @Bean
    public CronTrigger supplyCronTrigger(){
        return new CronTrigger(supplyCronExp);
    }

    @Bean
    public CronTrigger assignCronTrigger(){
        return new CronTrigger(assignCronExp);
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadGroupName("Supply");
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolSupplyTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolAssignTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadGroupName("Assign");
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolAssignTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
