/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.PrivateKey;
import java.security.cert.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class RuntimeContext implements ApplicationContextAware {

    @PersistenceContext
    EntityManager em;
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);
    // Lock for global instance anchor
    static SessionFactory sessionFactory;
    private static ApplicationContext applicationContext;
    private ContractIdGenerator clientContractIdGenerator;




    /*******************************************************************************************************************
     *                                     Системные
     ******************************************************************************************************************/
    public static ApplicationContext getAppContext() {
        return applicationContext;
    }

    public static RuntimeContext getInstance() throws NotInitializedException {
        return getAppContext().getBean(RuntimeContext.class);
    }

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RuntimeContext.applicationContext = applicationContext;
    }






    /*******************************************************************************************************************
     *                                     Менеджеры
     ******************************************************************************************************************/
    @PostConstruct
    public void init() throws Exception {
        applicationContext.getBean(this.getClass()).initDB();

        Properties properties = new Properties();
        this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);
    }

    public ContractIdGenerator getClientContractIdGenerator() {
        return clientContractIdGenerator;
    }

    private static ContractIdGenerator createClientContractIdGenerator(Properties properties,
            SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating client contract ID generator.");
        }
        ContractIdGenerator contractIdGenerator = new ContractIdGenerator(sessionFactory);
        if (logger.isDebugEnabled()) {
            logger.debug("Client contract ID generator created.");
        }
        return contractIdGenerator;
    }





    /*******************************************************************************************************************
     *                                     БД
     ******************************************************************************************************************/
    public void initiateEntityManager () {
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org_room");
            em = emf.createEntityManager();
            setSessionFactory (((Session) em.getDelegate()).getSessionFactory());
        }
    }

    public Session createPersistenceSession() throws Exception {
        initiateEntityManager ();
        return sessionFactory.openSession();
        /* sessionFactory = ((Session)entityManagerFactory.createEntityManager()).getSessionFactory();
        return sessionFactory.openSession();*/
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        RuntimeContext.sessionFactory = sessionFactory;
    }

    @Transactional
    public void initDB() throws Exception {

        try {
            //getAppContext().getBean(RuntimeContext.class).initiateEntityManager();
            getAppContext().getBean(RuntimeContext.class).loadOptionValues();
        } catch (Exception e) {
            logger.error("Failed to init application.", e);
            throw e;
        }
    }


    HashMap<Integer, String> optionsValues;

    public void loadOptionValues() {
        optionsValues = new HashMap<Integer, String>();
        for (int n = 0; n < Option.OPTIONS_INITIALIZER.length; n += 2) {
            Integer nOption = (Integer) Option.OPTIONS_INITIALIZER[n];
            String v = DAOUtils.getOptionValue(em, nOption, (String) Option.OPTIONS_INITIALIZER[n + 1]);
            optionsValues.put(nOption, v);
            //optionsValues.put(nOption, "" + nOption);
        }
    }

    public boolean getOptionValueBool(int optionId) {
        return getOptionValueString(optionId).equals("1");
    }

    public int getOptionValueInt(int optionId) {
        return Integer.parseInt(getOptionValueString(optionId));
    }

    public long getOptionValueLong(int optionId) {
        return Long.parseLong(getOptionValueString(optionId));
    }

    public double getOptionValueDouble(int optionId) {
        return Double.parseDouble(getOptionValueString(optionId));
    }

    public String getOptionValueString(int optionId) {
        String src = optionsValues.get(optionId);
        if (src == null) {
            return src;
        }
        Map<String, String> env = System.getenv();
        while (src.indexOf("${") > -1) {
            int idx = src.indexOf("${");
            String key = src.substring(idx + 2, src.indexOf("}", idx + 2));
            String val = env.get(key);
            if (val == null) {
                val = "";
            }
            src = src.replaceAll("[$]\\{" + key + "\\}", val);
        }
        return src;
        //return optionsValues.get(optionId);
    }

    public void setOptionValue(int optionId, String value) {
        optionsValues.put(optionId, value);
    }

    public void setOptionValue(int optionId, Boolean value) {
        setOptionValue(optionId, value ? "1" : "0");
    }

    public void setOptionValue(int optionId, int value) {
        setOptionValue(optionId, value + "");
    }

    public void setOptionValue(int optionId, long value) {
        setOptionValue(optionId, value + "");
    }

    @Transactional
    public void setOptionValueWithSave(int optionId, Object value) {
        setOptionValue(optionId, value + "");
        Option o = new Option((long) optionId, value + "");
        o = em.merge(o);
        em.persist(o);
    }

    public void setOptionValue(int optionId, double value) {
        setOptionValue(optionId, value + "");
    }

    @Transactional
    public void saveOptionValues() {
        for (Map.Entry<Integer, String> e : optionsValues.entrySet()) {
            Option o = new Option((long) e.getKey(), e.getValue());
            o = em.merge(o);
            em.persist(o);
        }
    }




    /*******************************************************************************************************************
     *                                     Вспомогательные
     ******************************************************************************************************************/
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
}
