package ru.axetta.ecafe.processor.core.proactive.config;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.proactive.service.PersonBenefitCategoryService;

@Component
@Scope("singleton")
public class BenefitSchedulerService {

    final static String BENEFIT_EXPIRATION = "CheckBenefitExpirationDate";
    private static final Logger logger = LoggerFactory.getLogger(BenefitSchedulerService.class);


    public static class BenefitScheduler implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(BenefitSchedulerService.class).start();
            } catch (Exception e) {
                logger.error("Failed auto check benefit expiration date", e);
            }
        }
    }

    public void scheduleSync() {
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.zlp.check_benefit_expiration", "");
        if (StringUtils.isBlank(syncScheduleSync))
            return;
        try {
            JobDetail jobDetailSync = new JobDetail(BENEFIT_EXPIRATION, Scheduler.DEFAULT_GROUP, BenefitScheduler.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(BENEFIT_EXPIRATION, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(BENEFIT_EXPIRATION, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(BENEFIT_EXPIRATION, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed auto scheduleSync CheckBenefitExpirationDate", e);
        }
    }

    public void start() throws Exception {
        RuntimeContext.getAppContext().getBean(PersonBenefitCategoryService.class).checkEndDateForBenefitCategory();
    }
}
