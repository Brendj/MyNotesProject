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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
@Service
public class ProcessorUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtils.class);

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
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = sessionFactory.openSession();
            persistenceTransaction = session.beginTransaction();
            LastProcessSectionsDates lastProcessSectionsDate = DAOUtils.findLastProcessSectionsDate(session,
                    new CompositeIdOfLastProcessSectionsDates(idOfOrg, sectionType.getType()));
            if (lastProcessSectionsDate == null){
                lastProcessSectionsDate = new LastProcessSectionsDates(new CompositeIdOfLastProcessSectionsDates(idOfOrg,
                        sectionType.getType()), new Date());
                session.save(lastProcessSectionsDate);
            } else {
                lastProcessSectionsDate.setDate(new Date());
                session.update(lastProcessSectionsDate);
            }
            session.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error saving LastProcessSectionDate", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
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

}
