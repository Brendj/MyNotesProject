/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfLastProcessSectionsDates;
import ru.axetta.ecafe.processor.core.persistence.LastProcessSectionsDates;
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

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
public class ProcessorUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtils.class);

    @Async
    private static void runRegularPayments(SyncRequest request) {
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

    public static void runRegularPaymentsIfEnabled(SyncRequest request) {
        if (RuntimeContext.getInstance().isMainNode() && RuntimeContext.getInstance().getSettingsConfig()
                .isEcafeAutopaymentBkEnabled()) {
            runRegularPayments(request);
        }
    }

    public static void saveLastProcessSectionDate(SessionFactory sessionFactory, Long idOfOrg, SectionType sectionType){
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

}
