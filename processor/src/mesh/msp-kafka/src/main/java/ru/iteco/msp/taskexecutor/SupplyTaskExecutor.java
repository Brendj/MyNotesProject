/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import ru.iteco.msp.kafka.SupplyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SupplyTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(SupplyTaskExecutor.class);

    private final CronTrigger supplyCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler;
    private final SupplyService supplyService;


    @Value(value = "${kafka.task.execution.supply.samplesize}")
    private Integer sampleSize;

    public SupplyTaskExecutor(CronTrigger supplyCronTrigger, ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler,
            SupplyService supplyService) {
        this.supplyCronTrigger = supplyCronTrigger;
        this.threadPoolSupplyTaskScheduler = threadPoolSupplyTaskScheduler;
        this.supplyService = supplyService;
    }

    @PostConstruct
    public void buildRunnableTask() {
        threadPoolSupplyTaskScheduler.schedule(new RunnableTask(), supplyCronTrigger);
    }

    class RunnableTask implements Runnable {

        @Override
        public void run() {
            supplyService.runFromTaskExecutor(sampleSize);
        }
    }
}
