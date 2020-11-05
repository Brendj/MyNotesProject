package ru.axetta.ecafe.processor.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nuc on 22.10.2020.
 */
@Component
@Qualifier("clearMenuExecutor")
public class ClearMenuExecutor extends ThreadPoolTaskExecutor {
    @PostConstruct
    public void init() {
        this.setMaxPoolSize(10);
        this.setQueueCapacity(100);
        this.setAllowCoreThreadTimeOut(true);
        this.setKeepAliveSeconds(60);
    }
}