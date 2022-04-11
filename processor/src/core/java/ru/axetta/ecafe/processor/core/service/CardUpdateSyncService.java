package ru.axetta.ecafe.processor.core.service;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CalcChainDocument;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CardUpdateSyncService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CardUpdateSyncService.class);

    private static final String NODE_PROPERTY = "ecafe.processor.card.update.syncservice.node";
    private static final String DELTA_PROPERTY = "ecafe.processor.card.update.syncservice.delta";
    private static final String CRON_PROPERTY = "ecafe.processor.card.update.syncservice.cron";

    public boolean isOn() {
        return RuntimeContext.getInstance().actionIsOnByNode(NODE_PROPERTY);
    }

    public void run() {
        logger.info("Start CardUpdateSyncService");
        Date dateToSave = new Date();
        Date endDate = DAOReadonlyService.getInstance().getLastProcessedCardUpdate();
        int delta = RuntimeContext.getInstance().getPropertiesValue(DELTA_PROPERTY, 5);
        Date startDate = CalendarUtils.addMinute(endDate, -delta);
        endDate = CalendarUtils.addMinute(dateToSave, -delta);
        Set<Long> orgsToSync = new HashSet<>();
        try {
            List<Card> list = DAOReadonlyService.getInstance().getCardsUpdatedBetweenDate(startDate, endDate);
            for (Card card : list) {
                orgsToSync.add(card.getOrg().getIdOfOrg());
            }
            if (orgsToSync.isEmpty()) {
                logger.info("CardUpdateSyncService cards to sync not found");
                return;
            }
            List<Long> allOrgs = DAOReadonlyService.getInstance().findFriendlyOrgsIdsByListOrgs(orgsToSync);
            if (DAOService.getInstance().setOrgCardSyncParam(allOrgs) > 0) {
                DAOService.getInstance().setOnlineOptionValue("" + dateToSave.getTime(), Option.OPTION_LAST_PROCESSED_CARD_UPDATE);
            }
            logger.info(String.format("CardUpdateSyncService set directive for %s orgs", allOrgs.size()));
        } catch (Exception e) {
            logger.error("Error in CardUpdateSyncService run: ", e);
        }

        logger.info("End CardUpdateSyncService");
    }

    public void scheduleSync() {
        if (!isOn())
            return;
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties().getProperty(CRON_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling CardUpdateSyncService job: " + syncSchedule);
            JobDetail job = new JobDetail("CardUpdateSyncService", Scheduler.DEFAULT_GROUP, CardUpdateSyncService.CardUpdateSyncServiceJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            CronTrigger trigger = new CronTrigger("CardUpdateSyncService", Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression(syncSchedule);
            if (scheduler.getTrigger("CardUpdateSyncService", Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob("CardUpdateSyncService", Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed to schedule CardUpdateSyncService job:", e);
        }
    }

    public static class CardUpdateSyncServiceJob implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(CardUpdateSyncService.class).run();
            } catch (Exception e) {
                logger.error("Failed to run CardUpdateSyncService job: ", e);
            }
        }
    }
}
