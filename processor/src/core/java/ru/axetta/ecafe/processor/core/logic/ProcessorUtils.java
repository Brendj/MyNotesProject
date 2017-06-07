/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfLastProcessSectionsDates;
import ru.axetta.ecafe.processor.core.persistence.LastProcessSectionsDates;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.bk.BKRegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.sync.SectionType;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
@Service
public class ProcessorUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtils.class);
    private static final List<LastProcessRecord> sectionDatesToSave = Collections.synchronizedList(new ArrayList<LastProcessRecord>());
    private static final int MAX_SECTION_DATES_TO_SAVE_SIZE = 10;

    @Async
    public void runRegularPayments(SyncRequest request) {
        try {
            long time = System.currentTimeMillis();
            logger.info("runRegularPayments run");
            BKRegularPaymentSubscriptionService regularPaymentSubscriptionService = (BKRegularPaymentSubscriptionService) RuntimeContext
                    .getInstance().getRegularPaymentSubscriptionService();
            regularPaymentSubscriptionService.checkClientBalances(request.getIdOfOrg());
            logger.info("runRegularPayments stop" + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            logger.warn("catch BKRegularPaymentSubscriptionService exc");
        }
    }

    @Async
    public void createSyncHistoryException(SessionFactory sessionFactory, long idOfOrg, SyncHistory syncHistory, String s) {
        if (syncHistory == null) return;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.createSyncHistoryException(persistenceSession, idOfOrg, syncHistory, s);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("createSyncHistoryException exception: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Async
    public void saveLastProcessSectionDate(SessionFactory sessionFactory, Long idOfOrg, SectionType sectionType){
        List<LastProcessRecord> sectionDates = null;
        boolean doSaveSectionDates = false;
        synchronized (sectionDatesToSave) {
            if (sectionDatesToSave.size() > MAX_SECTION_DATES_TO_SAVE_SIZE) {
                sectionDates = new ArrayList<LastProcessRecord>(sectionDatesToSave);
                sectionDatesToSave.clear();
                doSaveSectionDates = true;
            }
        }
        if (doSaveSectionDates) {
            saveLastProcessSectionsBatchUpdateSynchronized(sessionFactory, sectionDates);
        } else {
            LastProcessRecord record = new LastProcessRecord(
                    new CompositeIdOfLastProcessSectionsDates(idOfOrg, sectionType.getType()), new Date());
            addToSectionDatesToSaveSynchronized(record);
        }
    }

    private void addToSectionDatesToSaveSynchronized(LastProcessRecord record) {
        synchronized (sectionDatesToSave) {
            for (LastProcessRecord exRecord : sectionDatesToSave) {
                if (exRecord.getId().getIdOfOrg().equals(record.getId().getIdOfOrg()) && exRecord.getId().getType()
                        .equals(record.getId().getType())) {
                    exRecord.setDatetime(record.getDatetime());
                    return;
                }
            }
            sectionDatesToSave.add(record);
        }
    }

    private void saveLastProcessSectionsBatchUpdateSynchronized(SessionFactory sessionFactory, List<LastProcessRecord> sectionDates) {
        if (sectionDates == null) return;
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = sessionFactory.openSession();
            persistenceTransaction = session.beginTransaction();
            int size = sectionDates.size();
            String sql = "";
            for (int i = 1; i <= size; i++) {
                sql += String.format("select %s as date, %s as type, %s as idoforg", ":date"+i, ":type"+i, ":org"+i);
                if (i < size) sql += " union ";
            }
            Query query = session.createSQLQuery(String.format(
                    "update cf_lastprocesssectionsdates p set date = q.date from (%s) q where p.idoforg = q.idoforg and p.type = q.type", sql));

            for (int i = 1; i <= size; i++) {
                query.setParameter("date"+i, sectionDates.get(i-1).getDatetime().getTime());
                query.setParameter("type"+i, sectionDates.get(i-1).getId().getType());
                query.setParameter("org"+i, sectionDates.get(i-1).getId().getIdOfOrg());
            }

            int result = query.executeUpdate();
            if (result < size) {
                //если количество обновленных записей меньше размера списка, то обновляем записи по одной как в старом варианте
                logger.error("Warning: batch update lastProcessSectionDates failed. Try run update in loop.");
                for (LastProcessRecord record : sectionDates) {
                    saveLastProcessSectionCustomDateTransactionFree(session, record.getId().getIdOfOrg(), SectionType.fromInteger(
                            record.getId().getType()), record.getDatetime());
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error saving LastProcessSectionDate", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void saveLastProcessSectionCustomDate(SessionFactory sessionFactory, Long idOfOrg, SectionType sectionType) {
        Session session = null;
        Transaction persistenceTransaction = null;
        Date date = new Date();
        try {
            session = sessionFactory.openSession();
            persistenceTransaction = session.beginTransaction();
            saveLastProcessSectionCustomDateTransactionFree(session, idOfOrg, sectionType, date);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error saving LastProcessSectionDate", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void saveLastProcessSectionCustomDateTransactionFree(Session session, Long idOfOrg, SectionType sectionType, Date date) {
        Query query = session.createQuery("update LastProcessSectionsDates p set p.date = :date where p.compositeIdOfLastProcessSectionsDates = :compositeId");
        query.setParameter("date", date);
        query.setParameter("compositeId", new CompositeIdOfLastProcessSectionsDates(idOfOrg, sectionType.getType()));
        int result = query.executeUpdate();
        if (result == 0) {
            LastProcessSectionsDates lastProcessSectionsDate = new LastProcessSectionsDates(new CompositeIdOfLastProcessSectionsDates(idOfOrg,
                    sectionType.getType()), date);
            session.persist(lastProcessSectionsDate);
        }
    }

    public Date getLastProcessSectionDate(Session session, Long idOfOrg, SectionType sectionType) {
        try {
            Query query = session.createQuery("select s.date from LastProcessSectionsDates s "
                    + "where s.compositeIdOfLastProcessSectionsDates.idOfOrg = :idOfOrg and s.compositeIdOfLastProcessSectionsDates.type = :type");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("type", sectionType.getType());
            return (Date) query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error retrieving lastProcessSectionDate", e);
            return null;
        }
    }

    public static void refreshOrg(SessionFactory sessionFactory, Org org){
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = sessionFactory.openSession();
            persistenceTransaction = session.beginTransaction();
            session.refresh(org);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static class LastProcessRecord {
        private Date datetime;
        private CompositeIdOfLastProcessSectionsDates id;

        public LastProcessRecord(CompositeIdOfLastProcessSectionsDates id, Date datetime) {
            this.setId(id);
            this.setDatetime(datetime);
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public CompositeIdOfLastProcessSectionsDates getId() {
            return id;
        }

        public void setId(CompositeIdOfLastProcessSectionsDates id) {
            this.id = id;
        }
    }

}
