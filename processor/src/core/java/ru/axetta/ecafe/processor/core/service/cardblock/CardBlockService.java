/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.cardblock;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class CardBlockService {
    private static final Logger logger = LoggerFactory.getLogger(CardBlockService.class);
    public static final String SCHEDULE_NAME = "Card_Block_Service";
    public static final String LOCK_REASON = "Заблокировано на сервере по причине длительного неиспользования";
    public static final int TYPE_ENTEREVENT = 0;
    public static final int TYPE_ORDER = 1;
    public static final int TYPE_MUSEUM_CULTURE = 2;

    @Autowired
    RuntimeContext runtimeContext;

    @PostConstruct
    public void init() {
        if (!isOn()) return;
        String cron = runtimeContext.getOptionValueString(Option.OPTION_CARD_AUTOBLOCK);

        try {
            JobDetail jobDetail = new JobDetail(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP, NotificationJob.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            CronTrigger triggerDaily = new CronTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
            triggerDaily.setCronExpression(cron);
            if (scheduler.getTrigger(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(SCHEDULE_NAME, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetail, triggerDaily);
        } catch (Exception e) {
            logger.error("Error in schedule payment notification task: ", e);
        }
    }

    public boolean isOn() {
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getOptionValueString(Option.OPTION_CARD_AUTOBLOCK_NODE);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void run() {
        logger.info("Start auto block cards service");
        int counter = 0;
        List<Card> cards = DAOReadonlyService.getInstance().getCardsToBlock(runtimeContext.getOptionValueInt(Option.OPTION_CARD_AUTOBLOCK_DAYS));
        for (Card card : cards) {
            try {
                RuntimeContext.getAppContext().getBean(CardService.class)
                        .block(card.getCardNo(), card.getOrg().getIdOfOrg(), card.getClient().getIdOfClient(), false,
                                LOCK_REASON, CardState.BLOCKED);
                counter++;
            } catch (Exception e) {
                logger.error("Error in CardBlockService. CardNo = " + card.getCardNo(), e);
            }
        }
        logger.info(String.format("End auto block cards service. Processed %s cards", counter));
    }

    public void saveLastCardActivity(Session session, Long idOfCard, CardActivityType type) {
        if (idOfCard == null) return;
        Criteria criteria = session.createCriteria(CardActivity.class);
        criteria.add(Restrictions.eq("idOfCard", idOfCard));
        criteria.add(Restrictions.eq("type", type));
        CardActivity cardActivity = (CardActivity)criteria.uniqueResult();
        if (cardActivity == null) {
            cardActivity = new CardActivity(idOfCard, type);
        }
        cardActivity.setLastUpdate(new Date());
        session.saveOrUpdate(cardActivity);
    }

    public static class NotificationJob implements StatefulJob {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(CardBlockService.class).run();
        }
    }
}
