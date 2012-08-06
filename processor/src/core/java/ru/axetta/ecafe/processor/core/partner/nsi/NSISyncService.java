/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class NSISyncService {
    Logger logger = LoggerFactory.getLogger(NSISyncService.class);

    @Resource
    MskNSIService mskNSIService;
    @Resource
    DAOService daoService;
    @PersistenceContext
    EntityManager em;

    @Transactional
    public void doSync() {
        String v = DAOUtils.getOptionValue(em, Option.OPTION_NSI_LAST_SYNC_TIME, null);
        long lastSyncTime = System.currentTimeMillis();
        if (v!=null) lastSyncTime = Long.parseLong(v);
        
        List<Org> orgList = DAOUtils.getAllOrgWithGuid(em);
        
        Long now = System.currentTimeMillis();

        try {
            logger.info("NSI sync started");
            for (Org org : orgList) {
                List<MskNSIService.PupilInfo> changedRecs = mskNSIService.getPupilsByOrgGUID(org.getGuid(), null, lastSyncTime);
                processChangedRecs(org, changedRecs);
            }
            RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_NSI_LAST_SYNC_TIME, now);
            logger.info("NSI sync completed");
        } catch (Exception e) {
            logger.error("Failed to sync NSI", e);
        }
    }

    private void processChangedRecs(Org org, List<MskNSIService.PupilInfo> changedRecs) throws Exception {
        for (MskNSIService.PupilInfo pi : changedRecs) {
            Client c = DAOUtils.findClientByGuid(em, pi.getGuid());
            if (c!=null) {
                logger.info("Changed client in NSI - guid already registered: "+pi.getGuid());
            } else {
                logger.info("Registering new client from NSI: "+pi.getGuid());
                ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
                fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, pi.getGuid());
                fieldConfig.setValue(ClientManager.FieldId.SURNAME, pi.getFamilyName());
                fieldConfig.setValue(ClientManager.FieldId.NAME, pi.getFirstName());
                fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, pi.getSecondName());
                fieldConfig.setValue(ClientManager.FieldId.GROUP, pi.getGroup());
                fieldConfig.setValue(ClientManager.FieldId.COMMENTS, "Зарегистрирован из Реестра");

                ClientManager.registerClient(org.getIdOfOrg(), fieldConfig, false);
            }
        }
    }
    
    public static class SyncJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(NSISyncService.class).doSync();
        }
    }
    
    final static String JOB_NAME="sync";

    public void scheduleSync() throws Exception {
        mskNSIService.init();
        
        String syncSchedule = RuntimeContext.getInstance().getNsiServiceConfig().syncSchedule;
        logger.info("Scheduling NSI sync job: "+syncSchedule);
        JobDetail jobDetail = new JobDetail(JOB_NAME,Scheduler.DEFAULT_GROUP, SyncJob.class);

        CronTrigger trigger = new CronTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
        //trigger.setStartTime(new Date());
        //trigger.setEndTime(new Date(new Date().getTime() + 10 * 60 * 1000));
        trigger.setCronExpression(syncSchedule);

        SchedulerFactory sfb = new StdSchedulerFactory();
        Scheduler scheduler = sfb.getScheduler();
        if (scheduler.getTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP)!=null) {
            scheduler.deleteJob(JOB_NAME, Scheduler.DEFAULT_GROUP);
        }
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}
