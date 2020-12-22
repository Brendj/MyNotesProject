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
import org.hibernate.*;
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
import java.util.LinkedList;
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
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Обработка мигрантов (перевод в выбывшие) по расписанию");
        clientsMobileHistory.setShowing("ЕСЗ");
        updateMigrants(clientsMobileHistory);
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

    public void updateMigrants(ClientsMobileHistory clientsMobileHistory) throws Exception {
        Long idOfESZOrg = PropertyUtils.getIdOfESZOrg();

        Date currentDate = new Date();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            session.setFlushMode(FlushMode.COMMIT);

            logger.info("Start updating esz migrants");

            Criteria clientsCriteria = createMigrantCriteria(session, idOfESZOrg, currentDate, null);

            List<Client> list = clientsCriteria.list();
            list.addAll(createClientQuery(session, idOfESZOrg).list());
            for (Client client : list) {
                Criteria migrantsCriteria = createMigrantCriteria(session, idOfESZOrg, currentDate, client);
                List<Migrant> migrantList = migrantsCriteria.list();

                boolean allMigrantRequestClosed = true;
                for (Migrant migrant : migrantList) {
                    VisitReqResolutionHist lastHistory;
                    LinkedList<VisitReqResolutionHist> histList = new LinkedList<>(
                            migrant.getVisitReqResolutionHists());
                    if (histList.isEmpty()) {
                        logger.error(String.format(
                                "Empty resolutions history for migrant request: idOfOrgRegistry=%d, idOfRequest=%d",
                                migrant.getCompositeIdOfMigrant().getIdOfOrgRegistry(),
                                migrant.getCompositeIdOfMigrant().getIdOfRequest()));
                        allMigrantRequestClosed = false;
                        continue;
                    }
                    lastHistory = histList.getLast();
                    if (Migrant.CLOSED != migrant.getSyncState() && (
                            migrant.getVisitEndDate().getTime() <= currentDate.getTime() || (
                                    lastHistory.getResolution().equals(VisitReqResolutionHist.RES_CANCELED)
                                            || (lastHistory.getResolution()
                                            .equals(VisitReqResolutionHist.RES_REJECTED))))) {
                        closeMigrantRequest(session, migrant, client);
                    }

                    allMigrantRequestClosed &= (migrant.getSyncState().equals(Migrant.CLOSED));
                }

                if (allMigrantRequestClosed) {
                    ClientManager.ClientFieldConfigForUpdate fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
                    fieldConfig.setValue(ClientManager.FieldId.GROUP,
                            ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    ClientManager.modifyClientTransactionFree(fieldConfig, null, "", client, session, clientsMobileHistory);
                    addGroupHistory(session, client, ClientGroup.Predefined.CLIENT_LEAVING.getValue());
                }
            }

            transaction.commit();
            transaction = null;

            logger.info("End updating esz migrants");
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private Criteria createMigrantCriteria(Session session, Long idOfESZOrg, Date currentDate, Client client) {
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.createAlias("migrant", "migrant");
        criteria.createAlias("migrant.clientMigrate", "clientMigrate");
        criteria.createAlias("clientMigrate.org", "org");
        criteria.add(Restrictions.eq("org.idOfOrg", idOfESZOrg));
        criteria.add(
                Restrictions.ne("clientMigrate.idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue()));
        if (null == client) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.le("migrant.visitEndDate", currentDate));
            disjunction.add(Restrictions.eq("resolution", VisitReqResolutionHist.RES_CANCELED));
            disjunction.add(Restrictions.eq("resolution", VisitReqResolutionHist.RES_REJECTED));
            criteria.add(disjunction);
            criteria.add(Restrictions.ne("syncState", Migrant.CLOSED));
            criteria.setProjection(Projections.distinct(Projections.property("migrant.clientMigrate")));
        } else {
            criteria.add(Restrictions.eq("migrant.clientMigrate", client));
            criteria.setProjection(Projections.distinct(Projections.property("migrant")));
        }
        return criteria;
    }

    // достаем клиентов оо есз, у которых нет заявок
    private Query createClientQuery(Session session, Long idOfESZOrg) {
        Query query = session.createQuery(
                "select c from Migrant m right join m.clientMigrate c join c.org o where m.compositeIdOfMigrant is null and o.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfESZOrg);
        return query;
    }

    private void closeMigrantRequest(Session session, Migrant migrant, Client client) {
        if (null != migrant) {
            migrant.setSyncState(Migrant.CLOSED);
            session.merge(migrant);
            session.save(ImportMigrantsService
                    .createResolutionHistory(session, client, migrant.getCompositeIdOfMigrant().getIdOfRequest(),
                            VisitReqResolutionHist.RES_OVERDUE_SERVER, new Date()));
        }
    }

    public static void addGroupHistory(Session session, Client client, Long idOfClientGroup)
            throws Exception {
        ClientGroup clientGroup = DAOUtils.findClientGroup(session,
                new CompositeIdOfClientGroup(client.getOrg().getIdOfOrg(), idOfClientGroup));
        if (null == clientGroup) {
            logger.error(String.format("Unable to find client group: idOfOrg=%d, idOfClientGroup=%d",
                    client.getOrg().getIdOfOrg(), idOfClientGroup));
            return;
        }
        ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                ClientGroupMigrationHistory.MODIFY_IN_ISPP);
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling update esz migrants service job: " + syncSchedule);
            JobDetail job = new JobDetail("ESZMigrantsUpdate", Scheduler.DEFAULT_GROUP, ESZMigrantsUpdateJob.class);

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
