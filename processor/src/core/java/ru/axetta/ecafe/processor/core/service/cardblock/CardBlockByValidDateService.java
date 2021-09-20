/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.cardblock;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import java.util.Date;
import java.util.List;

@Component
@DependsOn("runtimeContext")
public class CardBlockByValidDateService {
    private static final Logger logger = LoggerFactory.getLogger(CardBlockByValidDateService.class);

    private static final String SCHEDULE_NAME = "CardBlockByValidDateService";
    private static final String LOCK_REASON = "Заблокировано на сервере: истек срок действия карты";

    private static final String RUN_PROPERTY = "ecafe.processor.card.blockByValidDate.run";
    private static final String TARGET_NODE_PROPERTY = "ecafe.processor.card.blockByValidDate.node";
    private static final String CRON_EXP_PROPERTY = "ecafe.processor.card.blockByValidDate.cron";

    @Autowired
    RuntimeContext runtimeContext;

    @PostConstruct
    public void init() {
        if (!isOn()) {
            return;
        }

        String cron = runtimeContext.getConfigProperties().getProperty(CRON_EXP_PROPERTY, "").trim();
        try {
            JobDetail jobDetail = new JobDetail(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP, CardBlockByValidDateService.TaskExecutorJob.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            CronTrigger triggerDaily = new CronTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
            triggerDaily.setCronExpression(cron);
            if (scheduler.getTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetail, triggerDaily);
        } catch (Exception e) {
            logger.error("Error in schedule CardBlockByValidDateService task: ", e);
        }
    }

    public boolean isOn() {
        try {
            String instance = runtimeContext.getNodeName().trim();
            String reqInstance = runtimeContext.getConfigProperties().getProperty(TARGET_NODE_PROPERTY, "").trim();

            String run = runtimeContext.getConfigProperties().getProperty(RUN_PROPERTY, "false").trim();

            boolean isCurrentInstance = StringUtils.equals(instance, reqInstance);
            boolean isRun = Boolean.parseBoolean(run);

            return isCurrentInstance && isRun;
        } catch (Exception e){
            logger.error("Error when initialize service. Service stopped", e);
            return false;
        }
    }

    public void run() {
        int counter = 0;
        Date now = new Date();
        CardManager cardManager = RuntimeContext.getInstance().getCardManager();

        logger.info("Start auto block cards with overdue validDate service");

        List<Card> cards = DAOReadonlyService.getInstance().getActiveCardsWithOverdueValidDate(now);
        for (Card card : cards) {
            try {
                cardManager.updateCard(card.getClient().getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                        CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(), LOCK_REASON,
                        card.getIssueTime(), card.getExternalId(), null, null, LOCK_REASON);

                counter++;
            } catch (Exception e) {
                logger.error("Error in CardBlockByValidDateService. CardNo = " + card.getCardNo(), e);
            }
        }
        logger.info(String.format("End auto block cards service. Processed %s cards", counter));
    }

    public static class TaskExecutorJob implements StatefulJob {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(CardBlockByValidDateService.class).run();
        }
    }
}
