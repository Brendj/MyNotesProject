package ru.iteco.meshsync.taskscheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.iteco.meshsync.mesh.service.logic.EntityChangesProcessorService;

import javax.annotation.PostConstruct;

@Component
@DependsOn("entityChangesProcessorService")
public class ThreadPoolTaskSchedulerManager {
    private final static Logger log = LoggerFactory.getLogger(ThreadPoolTaskSchedulerManager.class);

    private final CronTrigger cronTrigger;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final ApplicationContext context;

    public ThreadPoolTaskSchedulerManager(CronTrigger cronTrigger,
                                          ThreadPoolTaskScheduler taskScheduler,
                                          ApplicationContext context){
        this.cronTrigger = cronTrigger;
        this.taskScheduler = taskScheduler;
        this.context = context;
    }

    @PostConstruct
    public void scheduleRunnable(){
        taskScheduler.schedule(new RunnableTask(context.getBean(EntityChangesProcessorService.class)), cronTrigger);
    }

    private class RunnableTask implements Runnable {
        private final Scheduled scheduled;

        private RunnableTask(Scheduled scheduled){
            this.scheduled = scheduled;
        }

        @Override
        public void run() {
            try {
                scheduled.process();
            } catch (Exception e){
                log.error("Error in RunnableTask: ", e);
            }
        }
    }
}
