/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.PropertyUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("ESZMigrantsUpdateService")
@Scope("singleton")
public class ESZMigrantsUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ESZMigrantsUpdateService.class);
    public final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.esz.migrants.update.cronExpression";

    public void run() throws Exception {
        if (!isOn()) {
            return;
        }
        updateMigrants();
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties()
                .getProperty("ecafe.processor.esz.migrants.update.service.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void updateMigrants() throws Exception {
        Long idOfESZOrg = PropertyUtils.getIdOfESZOrg();

        Date currentDate = new Date();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            session.setFlushMode(FlushMode.COMMIT);

            logger.info("Start updating esz migrants");

            Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
            criteria.createAlias("migrant", "m");
            criteria.createAlias("m.clientMigrate", "client");
            criteria.createAlias("client.org", "o");
            criteria.add(Restrictions.eq("o.idOfOrg", idOfESZOrg));
            criteria.add(Restrictions.ne("client.idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue()));
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.le("m.visitEndDate", currentDate));
            disjunction.add(Restrictions.eq("resolution", VisitReqResolutionHist.RES_CANCELED));
            disjunction.add(Restrictions.eq("resolution", VisitReqResolutionHist.RES_REJECTED));
            criteria.add(disjunction);
            criteria.setProjection(
                    Projections.projectionList().add(Projections.distinct(Projections.property("m.clientMigrate")))
                            .add(Projections.property("m.compositeIdOfMigrant")));

            List list = criteria.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Client client = (Client) row[0];
                CompositeIdOfMigrant idOfMigrant = (CompositeIdOfMigrant) row[1];
                closeMigrantRequest(session, idOfMigrant, client);
                ClientManager.ClientFieldConfigForUpdate fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
                fieldConfig
                        .setValue(ClientManager.FieldId.GROUP, ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                ClientManager.modifyClientTransactionFree(fieldConfig, null, "", client, session);
                addGroupHistory(session, client, ClientGroup.Predefined.CLIENT_LEAVING);
            }

            transaction.commit();
            transaction = null;

            logger.info("End updating esz migrants");
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void closeMigrantRequest(Session session, CompositeIdOfMigrant idOfMigrant, Client client) {
        Migrant migrant = (Migrant) session.load(Migrant.class, idOfMigrant);
        if (null != migrant) {
            migrant.setSyncState(Migrant.CLOSED);
            session.merge(migrant);
            session.save(ImportMigrantsService.createResolutionHistory(session, client, idOfMigrant.getIdOfRequest(),
                    VisitReqResolutionHist.RES_OVERDUE_SERVER, new Date()));
        }
    }

    private void addGroupHistory(Session session, Client client, ClientGroup.Predefined predefinedClientGroup)
            throws Exception {
        ClientGroupMigrationHistory migrationHistory = new ClientGroupMigrationHistory(client.getOrg(), client);
        migrationHistory.setComment(ClientGroupMigrationHistory.MODIFY_IN_ISPP);
        if (client.getClientGroup() != null) {
            migrationHistory.setOldGroupId(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup());
            migrationHistory.setOldGroupName(client.getClientGroup().getGroupName());
        }

        ClientGroup clientGroup = DAOUtils.findClientGroup(session,
                new CompositeIdOfClientGroup(client.getOrg().getIdOfOrg(), predefinedClientGroup.getValue()));
        if (null == clientGroup) {
            logger.error(String.format("Unable to find client group: idOfOrg=%d, idOfClientGroup=%d",
                    client.getOrg().getIdOfOrg(), predefinedClientGroup.getValue()));
            return;
        }
        migrationHistory.setNewGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        migrationHistory.setNewGroupName(clientGroup.getGroupName());

        session.save(migrationHistory);
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling update esz migrants service job: " + syncSchedule);
            JobDetail job = new JobDetail("ImportMigrantsFile", Scheduler.DEFAULT_GROUP, ESZMigrantsUpdateJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            CronTrigger trigger = new CronTrigger("ESZMigrantsUpdateService", Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression(syncSchedule);
            if (scheduler.getTrigger("ESZMigrantsUpdateService", Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob("ESZMigrantsUpdateService", Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed to schedule update esz migrants service job:", e);
        }
    }

    public static class ESZMigrantsUpdateJob implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ESZMigrantsUpdateService.class).run();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to run update esz migrants service job:", e);
            }
        }
    }
}
