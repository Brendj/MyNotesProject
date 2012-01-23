/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 24.02.2010
 * Time: 15:41:07
 * To change this template use File | Settings | File Templates.
 */
public class UpdateClientSmsDeliveryStatusJob extends ExecutorServiceWrappedJob {

    private static final long DELIVERY_STATUS_TIMEOUT = 10L * 24L * 60L * 60L * 1000L; // 10 days 

    public static class ExecuteEnvironment {

        private final ExecutorService executorService;
        private final SessionFactory sessionFactory;
        private final SmsService smsService;

        public ExecuteEnvironment(ExecutorService executorService, SessionFactory sessionFactory,
                SmsService smsService) {
            this.executorService = executorService;
            this.sessionFactory = sessionFactory;
            this.smsService = smsService;
        }

        public ExecutorService getExecutorService() {
            return executorService;
        }

        public SessionFactory getSessionFactory() {
            return sessionFactory;
        }

        public SmsService getSmsService() {
            return smsService;
        }
    }

    public static final String ENVIRONMENT_JOB_PARAM = ExecuteEnvironment.class.getCanonicalName();

    private static final Logger logger = LoggerFactory.getLogger(UpdateClientSmsDeliveryStatusJob.class);

    protected ExecutorService getExecutorService(JobExecutionContext context) throws Exception {
        final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                .get(ENVIRONMENT_JOB_PARAM);
        return executeEnvironment.getExecutorService();
    }

    protected Runnable getRunnable(JobExecutionContext context) {
        final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                .get(ENVIRONMENT_JOB_PARAM);
        return new Runnable() {
            public void run() {
                UpdateClientSmsDeliveryStatusJob
                        .run(executeEnvironment.getSessionFactory(), executeEnvironment.getSmsService());
            }
        };
    }

    public static void run(SessionFactory sessionFactory, SmsService smsService) {
        if (logger.isDebugEnabled()) {
            logger.debug("SMS delivery status update started.");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            updateSmsDeliveryStatus(persistenceSession, smsService);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to update SMS delivery status.", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SMS delivery status update finished.");
        }
    }

    private static void updateSmsDeliveryStatus(Session persistenceSession, SmsService smsService) throws Exception {
        Date currentTime = new Date();
        Criteria clientSmsCriteria = persistenceSession.createCriteria(ClientSms.class);
        clientSmsCriteria.add(Restrictions.or(Restrictions.eq("deliveryStatus", ClientSms.SENT_TO_SERVICE),
                Restrictions.eq("deliveryStatus", ClientSms.SEND_TO_RECIPENT)));
        List clientSmsList = clientSmsCriteria.list();
        for (Object currObject : clientSmsList) {
            ClientSms clientSms = (ClientSms) currObject;
            DeliveryResponse deliveryResponse = null;
            try {
                deliveryResponse = smsService.getDeliveryStatus(clientSms.getIdOfSms());
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Failed to get SMS delivery status, clientSms = %s", clientSms), e);
                }
            }
            updateSmsDeliveryStatus(currentTime, persistenceSession, clientSms, deliveryResponse);
        }
    }

    private static void updateSmsDeliveryStatus(Date currentTime, Session session, ClientSms clientSms,
            DeliveryResponse deliveryResponse) throws Exception {
        boolean updated = false;
        if (null == deliveryResponse) {
            updated = updateDeliveryStatusByTimeout(currentTime, clientSms);
        } else {
            Date sendDate = deliveryResponse.getSentDate();
            switch (deliveryResponse.getStatusCode()) {
                case DeliveryResponse.DELIVERED:
                    if (null != sendDate && null == clientSms.getSendTime()) {
                        clientSms.setSendTime(sendDate);
                    }
                    clientSms.setDeliveryStatus(ClientSms.DELIVERED_TO_RECIPENT);
                    clientSms.setDeliveryTime(deliveryResponse.getDoneDate());
                    updated = true;
                    break;
                case DeliveryResponse.NOT_DELIVERED:
                    if (null != sendDate && null == clientSms.getSendTime()) {
                        clientSms.setSendTime(sendDate);
                    }
                case DeliveryResponse.NOT_ALLOWED:
                case DeliveryResponse.INVALID_DESTINATION_ADDRESS:
                case DeliveryResponse.INVALID_SOURCE_ADDRESS:
                    clientSms.setDeliveryStatus(ClientSms.NOT_DELIVERED_TO_RECIPENT);
                    updated = true;
                    break;
                default:
                    updated = updateDeliveryStatusByTimeout(currentTime, clientSms);
                    break;
            }
        }
        if (updated) {
            session.update(clientSms);
        }
    }

    private static boolean updateDeliveryStatusByTimeout(Date currentTime, ClientSms clientSms) throws Exception {
        Date serviceSendTime = clientSms.getServiceSendTime();
        if (currentTime.after(serviceSendTime)) {
            if (currentTime.getTime() - serviceSendTime.getTime() >= DELIVERY_STATUS_TIMEOUT) {
                clientSms.setDeliveryStatus(ClientSms.NOT_DELIVERED_TO_RECIPENT);
                return true;
            }
        }
        return false;
    }
}