package ru.axetta.ecafe.processor.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nuc on 30.09.2020.
 */
@Component
@Qualifier("smsSendingTaskExecutor")
public class SmsSendingTaskExecutor extends ThreadPoolTaskExecutor {
    @PostConstruct
    public void init() {
        this.setCorePoolSize(100);
        this.setMaxPoolSize(150);
        this.setQueueCapacity(20000);
        this.setAllowCoreThreadTimeOut(true);
        this.setKeepAliveSeconds(120);
    }
}
