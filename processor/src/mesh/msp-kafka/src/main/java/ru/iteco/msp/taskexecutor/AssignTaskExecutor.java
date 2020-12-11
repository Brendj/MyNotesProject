/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import ru.iteco.msp.kafka.KafkaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AssignTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(AssignTaskExecutor.class);

    private final CronTrigger assignCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolAssignTaskScheduler;
    private final KafkaService kafkaService;

    public AssignTaskExecutor(
            CronTrigger assignCronTrigger,
            ThreadPoolTaskScheduler threadPoolAssignTaskScheduler,
            KafkaService kafkaService){
        this.assignCronTrigger = assignCronTrigger;
        this.threadPoolAssignTaskScheduler = threadPoolAssignTaskScheduler;
        this.kafkaService = kafkaService;
    }

    @PostConstruct
    public void buildRunnableTask(){
        threadPoolAssignTaskScheduler.schedule(new RunnableTask(), assignCronTrigger);
    }

    class RunnableTask implements Runnable{

        @Override
        public void run() {

        }
    }
}
