/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;


import org.apache.commons.io.FilenameUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
//
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Scope("singleton")

public class RuntimeContext implements ApplicationContextAware {



    public static class NotInitializedException extends RuntimeException {

        public NotInitializedException() {
            super("Runtime context was not initialized.");
        }
    }

    public static class AlreadyInitializedException extends RuntimeException {

        public AlreadyInitializedException() {
            super("Runtime context has been initialized already.");
        }
    }

    private static ApplicationContext applicationContext;

    private static final String PROCESSOR_PARAM_BASE = "ecafe.processor";
    public  static final String PARAM_NAME_DB_MAINTANANCE_HOUR=PROCESSOR_PARAM_BASE+".dbmaintanance.hour";
    public  static final String PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM=PROCESSOR_PARAM_BASE+".processor.hiddenPages";
    private static final String AUTO_REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".autoreport";
    private static final String REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".report";
    private static final String REPORT_PARAM_BASE_KEY = REPORT_PARAM_BASE + ".";
    private static final String EVENT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".event";
    private static final String AUTO_REPORT_MAIL_PARAM_BASE = AUTO_REPORT_PARAM_BASE + ".mail";
    private static final String SMS_SERVICE_PARAM_BASE = PROCESSOR_PARAM_BASE + ".sms.service";
    private static final String SMS_SERVICE_PARAM_CHECK_DELIVERY = SMS_SERVICE_PARAM_BASE+".checkDelivery";
    private static final String SUPPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".support";
    private static final String SUPPORT_MAIL_PARAM_BASE = SUPPORT_PARAM_BASE + ".mail";
    private static final String CLIENT_SMS_PARAM_BASE = PROCESSOR_PARAM_BASE + ".client.sms";
    private static final String WS_CRYPTO_BASE=PROCESSOR_PARAM_BASE+".ws.crypto";

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);
    // Lock for global instance anchor
    private static final Object INSTANCE_LOCK = new Object();

    // Application wide executor service
    private ExecutorService executorService;
    // Application wide job scheduler





    private PrivateKey syncPrivateKey;
    private PrivateKey paymentPrivateKey;

    private String payformUrl;
    private String payformGroupUrl;

    public static RuntimeContext getInstance() throws NotInitializedException {
        return getAppContext().getBean(RuntimeContext.class);
    }



    public String getPayformUrl() {
        return payformUrl;
    }

    public String getPayformGroupUrl() {
        return payformGroupUrl;
    }

    public Session createPersistenceSession() throws Exception {
       /* sessionFactory = ((Session)entityManagerFactory.createEntityManager()).getSessionFactory();*/
        return sessionFactory.openSession();
    }



    public PrivateKey getSyncPrivateKey() {
        return syncPrivateKey;
    }



    public PrivateKey getPaymentPrivateKey() {
        return paymentPrivateKey;
    }



    public TimeZone getSysTimeZone() throws Exception {
        return TimeZone.getTimeZone("Europe/Moscow");
    }

    public TimeZone getLocalTimeZone(HttpSession httpSession) throws Exception {
        return TimeZone.getTimeZone("Europe/Moscow");
    }

    public Calendar getLocalCalendar(HttpSession httpSession) throws Exception {
        Calendar localCalendar = Calendar.getInstance(getLocalTimeZone(httpSession));
        localCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        return localCalendar;
    }


    public TimeZone getDefaultLocalTimeZone(HttpSession httpSession) {
        try {
            return getLocalTimeZone(httpSession);
        } catch (Exception e) {
            logger.error("Failed to get TimeZone", e);
            return TimeZone.getTimeZone("UTC");
        }
    }

    public Calendar getDefaultLocalCalendar(HttpSession httpSession) {
        try {
            return getLocalCalendar(httpSession);
        } catch (Exception e) {
            logger.error("Failed to get Calendar", e);
            return Calendar.getInstance();
        }
    }

    static SessionFactory sessionFactory;

    public static void setSessionFactory(SessionFactory sessionFactory) {
        RuntimeContext.sessionFactory = sessionFactory;
    }

    


    @PersistenceContext
    EntityManager em;
    


    private static String restoreFilename(String defaultPath, String filename) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            return FilenameUtils.concat(defaultPath, filename);
        }
        return filename;
    }



    private static String buildPayformUrl(Properties properties) throws Exception {
        return properties.getProperty(AUTO_REPORT_PARAM_BASE + ".payform.url");
    }

    private static String buildPayformGroupUrl(Properties properties) throws Exception {
        return properties.getProperty(AUTO_REPORT_PARAM_BASE + ".payformgroup.url");
    }



    private static class ShutdownExecutorServiceTask implements Runnable {

        private final ExecutorService executorService;
        private final long timeout;
        private final CountDownLatch doneSignal;

        public ShutdownExecutorServiceTask(ExecutorService executorService, long timeout, CountDownLatch doneSignal) {
            this.executorService = executorService;
            this.timeout = timeout;
            this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                if (null != executorService) {
                    try {
                        if (!executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                            logger.debug("Termination awaiting timed out");
                            executorService.shutdownNow();
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Application-wide executor service stopped.");
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.error("Interrupted during wait for the executor service termination", e);
                        try {
                            executorService.shutdownNow();
                            if (logger.isDebugEnabled()) {
                                logger.debug("Application-wide executor service stopped.");
                            }
                            Thread.currentThread().interrupt();
                        } catch (Exception ignored) {
                            logger.error("Failed to shutdown executor service", ignored);
                        }
                    }
                }
            } finally {
                doneSignal.countDown();
            }
        }
    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RuntimeContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return applicationContext;
    }

    public static String getBeanName(Class cls) {
        return "#{" + cls.getSimpleName().substring(0, 1).toLowerCase() + cls.getSimpleName().substring(1) + "}";
    }



    HashMap<Integer, String> optionsValues;



    public boolean getOptionValueBool(int optionId) {
        return getOptionValueString(optionId).equals("1");
    }

    public int getOptionValueInt(int optionId) {
        return Integer.parseInt(getOptionValueString(optionId));
    }

    public double getOptionValueDouble(int optionId) {
        return Double.parseDouble(getOptionValueString(optionId));
    }

    public String getOptionValueString(int optionId) {
        return optionsValues.get(optionId);
    }

    public void setOptionValue(int optionId, String value) {
        optionsValues.put(optionId, value);
    }
    public void setOptionValue(int optionId, Boolean value) {
        setOptionValue(optionId, value?"1":"0");
    }
    public void setOptionValue(int optionId, int value) {
        setOptionValue(optionId, value+"");
    }

    public void setOptionValue(int optionId, double value) {
        setOptionValue(optionId, value+"");
    }


}

