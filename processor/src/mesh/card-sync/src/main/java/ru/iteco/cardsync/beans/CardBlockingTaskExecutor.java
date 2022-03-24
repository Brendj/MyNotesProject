package ru.iteco.cardsync.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Qualifier("cardBlockingTaskExecutor")
public class CardBlockingTaskExecutor extends ThreadPoolTaskExecutor {
    @PostConstruct
    public void init() {
        this.setCorePoolSize(1);
        this.setMaxPoolSize(5);
        this.setQueueCapacity(0);
        this.setAllowCoreThreadTimeOut(true);
        this.setKeepAliveSeconds(120);
        this.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
    }
}
