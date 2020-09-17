/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.AutoReportPostman;
import ru.axetta.ecafe.processor.core.report.AutoReportProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static final String PROCESSOR_PARAM_BASE = "ecafe.processor";
    private static final String REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".report";
    private static final String REPORT_PARAM_BASE_KEY = REPORT_PARAM_BASE + ".";
    private static final String AUTO_REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".autoreport";
    private static final String AUTO_REPORT_MAIL_PARAM_BASE = AUTO_REPORT_PARAM_BASE + ".mail";
    private static final String SUPPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".support";
    private static final String SUPPORT_MAIL_PARAM_BASE = SUPPORT_PARAM_BASE + ".mail";

    @PersistenceContext(unitName = "processorPU")
    EntityManager em;
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);
    // Lock for global instance anchor
    static SessionFactory sessionFactory;
    private static ApplicationContext applicationContext;
    private ContractIdGenerator clientContractIdGenerator;
    private AutoReportGenerator autoReportGenerator;
    private ExecutorService executorService;
    private Scheduler scheduler;
    private RuleProcessor autoReportProcessor;
    private Postman postman;




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

    private static Scheduler createScheduler(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating application-wide job scheduler.");
        }
        Object threadPoolClass = properties.get("org.quartz.threadPool.class");
        if (threadPoolClass == null || ((String) threadPoolClass).length() < 1) {
            properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.put("org.quartz.threadPool.threadCount", "4");
            properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        }
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        if (logger.isDebugEnabled()) {
            logger.debug("Application-wide job scheduler created.");
        }
        return scheduler;
    }

    private static ExecutorService createExecutorService(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating application-wide executor service.");
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        if (logger.isDebugEnabled()) {
            logger.debug("Application-wide executor service created.");
        }
        return executorService;
    }

    private static AutoReportGenerator createAutoReportGenerator(String basePath, Properties properties,
            ExecutorService executorService, Scheduler scheduler,
            AutoReportProcessor autoReportProcessor) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating auto report generator.");
        }
        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar calendar = Calendar.getInstance(localTimeZone);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        Properties reportProperties = new Properties();
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            if (StringUtils.startsWith(key, REPORT_PARAM_BASE_KEY)) {
                String value = properties.getProperty(key);
                reportProperties.setProperty(StringUtils.substringAfter(key, REPORT_PARAM_BASE_KEY), value);
            }
        }

        AutoReportGenerator generator = new AutoReportGenerator(basePath, executorService, scheduler, sessionFactory,
                autoReportProcessor, calendar, properties.getProperty(AUTO_REPORT_PARAM_BASE + ".path"), dateFormat,
                timeFormat, reportProperties);

        if (logger.isDebugEnabled()) {
            logger.debug("Auto report generator created.");
        }
        return generator;
    }

    private static RuleProcessor createRuleHandler(Properties properties, SessionFactory sessionFactory,
            AutoReportPostman autoReportPostman, Postman eventNotificationPostman) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating discountrule processor.");
        }
        RuleProcessor ruleProcessor = new RuleProcessor(sessionFactory, autoReportPostman, eventNotificationPostman);
        ruleProcessor.loadAutoReportRules();
        ruleProcessor.loadEventNotificationRules();
        if (logger.isDebugEnabled()) {
            logger.debug("Rule processor created.");
        }
        return ruleProcessor;
    }

    private static Postman.MailSettings readMailSettings(Properties properties, String baseParam) throws Exception {
        String smtpBaseParam = baseParam + ".smtp";
        String startTLS = properties.getProperty(smtpBaseParam + ".starttls");
        Postman.SmtpSettings smtpSettings = new Postman.SmtpSettings(properties.getProperty(smtpBaseParam + ".host"),
                Integer.parseInt(properties.getProperty(smtpBaseParam + ".port", "25")),
                StringUtils.equals(startTLS, "true"), properties.getProperty(smtpBaseParam + ".user"),
                properties.getProperty(smtpBaseParam + ".password"));
        String copyAddress = properties.getProperty(baseParam + ".copy");
        return new Postman.MailSettings(smtpSettings, new InternetAddress(properties.getProperty(baseParam + ".from")),
                null == copyAddress ? null : new InternetAddress(copyAddress));
    }

    private static Postman createPostman(Properties properties, SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating postman.");
        }
        Postman.MailSettings reportMailSettings = readMailSettings(properties, AUTO_REPORT_MAIL_PARAM_BASE);
        Postman.MailSettings supportMailSettings = readMailSettings(properties, SUPPORT_MAIL_PARAM_BASE);
        Postman postman = new Postman(reportMailSettings, supportMailSettings);
        if (logger.isDebugEnabled()) {
            logger.debug("Postman created.");
        }
        return postman;
    }






    /*******************************************************************************************************************
     *                                     Менеджеры
     ******************************************************************************************************************/
    @PostConstruct
    public void init() throws Exception {
        applicationContext.getBean(this.getClass()).initDB();

        String basePath = "/";

        Properties properties = new Properties();


        /*executorService = createExecutorService(properties);
        scheduler = createScheduler(properties);
        postman = createPostman(properties, sessionFactory);
        autoReportProcessor = createRuleHandler(properties, sessionFactory, postman, postman);*/


        this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);
        /*this.autoReportGenerator = createAutoReportGenerator(basePath, properties, executorService, scheduler,
                autoReportProcessor);*/
    }

    public ContractIdGenerator getClientContractIdGenerator() {
        return clientContractIdGenerator;
    }

    public AutoReportGenerator getAutoReportGenerator() {
        return autoReportGenerator;
    }

    private static ContractIdGenerator createClientContractIdGenerator(Properties properties,
            SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating client contract ID generator.");
        }
        ContractIdGenerator contractIdGenerator = new ContractIdGenerator(properties, sessionFactory);
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
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("processorPU");
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

    public RuleProcessor getAutoReportProcessor() {
        return autoReportProcessor;
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
