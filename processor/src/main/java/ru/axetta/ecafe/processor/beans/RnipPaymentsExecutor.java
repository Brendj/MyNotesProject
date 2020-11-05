package ru.axetta.ecafe.processor.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nuc on 30.09.2020.
 */
@Component
@Qualifier("rnipPaymentsExecutor")
public class RnipPaymentsExecutor extends ThreadPoolTaskExecutor {
    @PostConstruct
    public void init() {
        this.setCorePoolSize(2);
        this.setMaxPoolSize(5);
        this.setQueueCapacity(100);
        this.setAllowCoreThreadTimeOut(true);
        this.setKeepAliveSeconds(120);
    }
}
