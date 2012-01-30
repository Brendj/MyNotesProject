/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ClientAuthenticator;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.event.EventNotificationPostman;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.EventProcessor;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.partner.elecsnet.ElecsnetConfig;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointConfig;
import ru.axetta.ecafe.processor.core.partner.paypoint.PayPointProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.partner.sbrt.SBRTConfig;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.payment.PaymentLogger;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.AutoReportPostman;
import ru.axetta.ecafe.processor.core.report.AutoReportProcessor;
import ru.axetta.ecafe.processor.core.service.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.sms.ClientSmsDeliveryStatusUpdater;
import ru.axetta.ecafe.processor.core.sms.ClientSmsProcessor;
import ru.axetta.ecafe.processor.core.sms.MessageIdGenerator;
import ru.axetta.ecafe.processor.core.sms.SmsService;
import ru.axetta.ecafe.processor.core.support.SupportEmailSender;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.SyncProcessor;
import ru.axetta.ecafe.processor.core.updater.DBUpdater;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.StringReader;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 25.05.2009
 * Time: 10:48:56
 * To change this template use File | Settings | File Templates.
 */

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
    public static final String PARAM_NAME_DB_MAINTANANCE_HOUR=PROCESSOR_PARAM_BASE+".dbmaintanance.hour";
    private static final String AUTO_REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".autoreport";
    private static final String REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".report";
    private static final String REPORT_PARAM_BASE_KEY = REPORT_PARAM_BASE + ".";
    private static final String EVENT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".event";
    private static final String AUTO_REPORT_MAIL_PARAM_BASE = AUTO_REPORT_PARAM_BASE + ".mail";
    private static final String SMS_SERVICE_PARAM_BASE = PROCESSOR_PARAM_BASE + ".sms.service";
    private static final String SUPPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".support";
    private static final String SUPPORT_MAIL_PARAM_BASE = SUPPORT_PARAM_BASE + ".mail";
    private static final String CLIENT_SMS_PARAM_BASE = PROCESSOR_PARAM_BASE + ".client.sms";

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);
    // Lock for global instance anchor
    private static final Object INSTANCE_LOCK = new Object();

    // Application wide executor service
    private ExecutorService executorService;
    // Application wide job scheduler
    private Scheduler scheduler;
    private SyncProcessor syncProcessor;
    private SyncLogger syncLogger;
    private PaymentLogger paymentLogger;
    private PaymentProcessor paymentProcessor;


    private OnlinePaymentProcessor onlinePaymentProcessor;
    private ClientPaymentOrderProcessor clientPaymentOrderProcessor;
    private PrivateKey syncPrivateKey;
    private PrivateKey paymentPrivateKey;
    private ClientAuthenticator clientAuthenticator;
    private AutoReportPostman autoReportPostman;
    private EventNotificationPostman eventNotificationPostman;
    private RuleProcessor autoReportProcessor;
    private EventProcessor eventProcessor;
    private AutoReportGenerator autoReportGenerator;
    private String payformUrl;
    private String payformGroupUrl;
    private SmsService smsService;
    private SupportEmailSender supportEmailSender;
    private ClientSmsDeliveryStatusUpdater clientSmsDeliveryStatusUpdater;
    private MessageIdGenerator messageIdGenerator;
    private ClientSmsProcessor clientSmsProcessor;
    private ContractIdGenerator clientContractIdGenerator;
    private CardManager cardManager;
    private OrderCancelProcessor orderCancelProcessor;
    private PayPointProcessor payPointProcessor;

    private RBKMoneyConfig partnerRbkMoneyConfig;
    private PayPointConfig partnerPayPointConfig;
    private SBRTConfig partnerSbrtConfig;
    private ElecsnetConfig partnerElecsnetConfig;
    private StdPayConfig partnerStdPayConfig;

    public static RuntimeContext getInstance() throws NotInitializedException {
        return getAppContext().getBean(RuntimeContext.class);
    }

    public PayPointConfig getPartnerPayPointConfig() {
        return partnerPayPointConfig;
    }

    public SBRTConfig getPartnerSBRTConfig() {
        return partnerSbrtConfig;
    }

    public ElecsnetConfig getPartnerElecsnetConfig() {
        return partnerElecsnetConfig;
    }

    public PayPointProcessor getPayPointProcessor() {
        return payPointProcessor;
    }

    public AutoReportGenerator getAutoReportGenerator() {
        return autoReportGenerator;
    }

    public OrderCancelProcessor getOrderCancelProcessor() {
        return orderCancelProcessor;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public ContractIdGenerator getClientContractIdGenerator() {
        return clientContractIdGenerator;
    }

    public MessageIdGenerator getMessageIdGenerator() {
        return messageIdGenerator;
    }

    public ClientSmsProcessor getClientSmsProcessor() {
        return clientSmsProcessor;
    }

    public SmsService getSmsService() {
        return smsService;
    }

    public String getPayformUrl() {
        return payformUrl;
    }

    public String getPayformGroupUrl() {
        return payformGroupUrl;
    }

    public Session createPersistenceSession() throws Exception {
        return sessionFactory.openSession();
    }

    public SyncProcessor getSyncProcessor() {
        return syncProcessor;
    }

    public SyncLogger getSyncLogger() {
        return syncLogger;
    }

    public PrivateKey getSyncPrivateKey() {
        return syncPrivateKey;
    }

    public PaymentLogger getPaymentLogger() {
        return paymentLogger;
    }

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public RuleProcessor getAutoReportProcessor() {
        return autoReportProcessor;
    }

    public EventProcessor getEventProcessor() {
        return eventProcessor;
    }

    public ClientPaymentOrderProcessor getClientPaymentOrderProcessor() {
        return clientPaymentOrderProcessor;
    }

    public OnlinePaymentProcessor getOnlinePaymentProcessor() {
        return onlinePaymentProcessor;
    }

    public PrivateKey getPaymentPrivateKey() {
        return paymentPrivateKey;
    }

    public ClientAuthenticator getClientAuthenticator() {
        return clientAuthenticator;
    }

    public SupportEmailSender getSupportEmailSender() {
        return supportEmailSender;
    }

    public RBKMoneyConfig getPartnerRbkMoneyConfig() {
        return partnerRbkMoneyConfig;
    }

    public StdPayConfig getPartnerStdPayConfig() {
        return partnerStdPayConfig;
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


    Properties configProperties;

    @PostConstruct
    public void init() throws Exception {
        // to run in transaction
        applicationContext.getBean(this.getClass()).initDB();

        String basePath="/";
        Properties properties = loadConfig();
        configProperties = properties;
        Scheduler scheduler = null;
        ExecutorService executorService = null;
        Postman postman = null;
        ProcessLogger processLogger = null;
        RuleProcessor ruleProcessor = null;
        EventNotificator eventNotificator = null;
        Processor processor = null;
        SmsService smsService = null;
        //sessionFactory = ((Session)entityManagerFactory.createEntityManager()).getSessionFactory();
        //logger.info("sf = "+sessionFactory);
        try {

            executorService = createExecutorService(properties);
            this.executorService = executorService;

            scheduler = createScheduler(properties);
            this.scheduler = scheduler;

            postman = createPostman(properties, sessionFactory);
            this.supportEmailSender = postman;
            this.autoReportPostman = postman;
            this.eventNotificationPostman = postman;

            ruleProcessor = createRuleHandler(properties, sessionFactory, postman, postman);
            this.autoReportProcessor = ruleProcessor;
            this.eventProcessor = ruleProcessor;

            smsService = createSmsService(properties);
            this.smsService = smsService;

            this.partnerRbkMoneyConfig = new RBKMoneyConfig(properties, PROCESSOR_PARAM_BASE);
            this.partnerPayPointConfig = new PayPointConfig(properties, PROCESSOR_PARAM_BASE);
            this.partnerSbrtConfig = new SBRTConfig(properties, PROCESSOR_PARAM_BASE);
            this.partnerElecsnetConfig = new ElecsnetConfig(properties, PROCESSOR_PARAM_BASE);
            this.partnerStdPayConfig = new StdPayConfig(properties, PROCESSOR_PARAM_BASE);

            this.syncPrivateKey = loadSyncPrivateKeyData(properties);
            this.paymentPrivateKey = loadPaymentPrivateKeyData(properties);

            processLogger = createProcessLogger(basePath, properties);
            this.syncLogger = processLogger;
            this.paymentLogger = processLogger;

            eventNotificator = createEventNotificator(properties, executorService, sessionFactory, ruleProcessor);

            processor = createProcessor(properties, sessionFactory, eventNotificator);
            this.cardManager = processor;
            this.syncProcessor = processor;
            this.paymentProcessor = processor;
            this.clientPaymentOrderProcessor = processor;
            this.clientSmsProcessor = processor;
            this.orderCancelProcessor = processor;
            this.payPointProcessor = processor;

            this.onlinePaymentProcessor= new OnlinePaymentProcessor(processor);

            this.clientAuthenticator = createClientAuthenticator(sessionFactory);

            this.autoReportGenerator = createAutoReportGenerator(basePath, properties, executorService, scheduler,
                    sessionFactory, ruleProcessor);

            this.clientSmsDeliveryStatusUpdater = createClientSmsDeliveryStatusUpdater(properties, executorService,
                    scheduler, sessionFactory, smsService);

            this.payformUrl = buildPayformUrl(properties);
            this.payformGroupUrl = buildPayformGroupUrl(properties);
            this.messageIdGenerator = createMessageIdGenerator(properties, sessionFactory);
            this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);

            // Start background activities
            this.autoReportGenerator.start();
            this.clientSmsDeliveryStatusUpdater.start();
        } catch (Exception e) {
            destroy(executorService, scheduler);
            throw e;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Runtime context created.");
        }
    }

    SchemaVersionInfo currentSchemaVersionInfo;
    public SchemaVersionInfo getCurrentDBSchemaVersion() {
        return currentSchemaVersionInfo;
    }

    @PersistenceContext
    EntityManager em;
    
    @Transactional
    public void initDB() throws Exception {
        // Configure runtime context
        try {
            //persistenceSession = sessionFactory.openSession();
            //persistenceTransaction = persistenceSession.beginTransaction();
            // check DB version
            currentSchemaVersionInfo = getAppContext().getBean(DBUpdater.class).checkDbVersion();
            //
            loadOptionValues();
            // Check for Operator, Budget, Client type of contragents existence
            boolean operatorExists = false;
            boolean budgetExists = false;
            boolean clientExists = false;
            List<Contragent> contragentList = DAOUtils.getContragentsWithClassIds(em, new Integer[]{Contragent.OPERATOR, Contragent.BUDGET, Contragent.CLIENT});
            // Create if not
            for (Contragent contragent : contragentList) {
                if (contragent.getClassId().equals(Contragent.OPERATOR)) {
                    operatorExists = true;
                    logger.info("Contragent with class \"Operator\" exists, name \"" + contragent.getContragentName() + "\"");
                }
                if (contragent.getClassId().equals(Contragent.BUDGET)) {
                    budgetExists = true;
                    logger.info("Contragent with class \"Budget\" exists, name \"" + contragent.getContragentName() + "\"");
                }
                if (contragent.getClassId().equals(Contragent.CLIENT)) {
                    clientExists = true;
                    logger.info("Contragent with class \"Client\" exists, name \"" + contragent.getContragentName() + "\"");
                }
            }

            if (!operatorExists)
                createContragent("Оператор", 3);
            if (!budgetExists)
                createContragent("Бюджет", 4);
            if (!clientExists)
                createContragent("Клиент", 5);

            HashMap<Long, String> initCategory = new HashMap<Long, String>();
                initCategory.put(-90L,"Начальные классы");
                initCategory.put(-91L,"Средние классы");
                initCategory.put(-92L,"Старшие классы");
                initCategory.put(-101L,"1 класс");
                initCategory.put(-102L,"2 класс");
                initCategory.put(-103L,"3 класс");
                initCategory.put(-104L,"4 класс");
                initCategory.put(-105L,"5 класс");
                initCategory.put(-106L,"6 класс");
                initCategory.put(-107L,"7 класс");
                initCategory.put(-108L,"8 класс");
                initCategory.put(-109L,"9 класс");
                initCategory.put(-110L,"10 класс");
                initCategory.put(-111L,"11 класс");

            for (Map.Entry me : initCategory.entrySet()) {
                CategoryDiscount cd = DAOUtils.getCategoryDiscount(em, (Long)me.getKey());
                if (cd==null) {
                    createCategoryDiscount((Long) me.getKey(), String.valueOf(me.getValue()),"", "");
                }
            }

        } catch (Exception e) {
            logger.error("Failed to init application.", e);
            throw e;
        }
    }

    public void createCategoryDiscount(Long idOfCategoryDiscount, String categoryName, String discountRules, String description){
        Date currentTime = new Date();
        CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, discountRules, description, currentTime, currentTime);

        em.persist(categoryDiscount);
        logger.info("Category with name \"" + categoryName + "\" created");
    }

    public void createContragent(String contragentName, Integer classId) throws Exception {
        Person contactPerson = new Person("Иван", "Иванов", "");
        em.persist(contactPerson);
        Date currentTime = new Date();
        Contragent contragent = new Contragent(contactPerson, contragentName, classId, 1, "",
                "", currentTime, currentTime, "", false);
        em.persist(contragent);
        String className = Contragent.getClassAsString(classId);
        logger.info("Contragent with class \"" + className + "\" created, name \"" + contragentName + "\"");
    }

    private Properties loadConfig() throws Exception {
        Transaction persistenceTransaction = null;
        try {
            Session persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            // Get configuration from CF_Options
            Criteria criteria = persistenceSession.createCriteria(Option.class);
            criteria.add(Restrictions.eq("idOfOption", 1L));
            Option option = (Option)criteria.uniqueResult();
            String optionText = option.getOptionText();
            StringReader stringReader = new StringReader(optionText);
            Properties properties = new Properties();
            properties.load(stringReader);
            return properties;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
        }

    }

    private void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroying runtime context.");
        }
        destroy(this.executorService, this.scheduler);
        if (logger.isInfoEnabled()) {
            logger.info("Runtime context destroyed.");
        }
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

    private static Scheduler createScheduler(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating application-wide job scheduler.");
        }
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        if (logger.isDebugEnabled()) {
            logger.debug("Application-wide job scheduler created.");
        }
        return scheduler;
    }



    private static Processor createProcessor(Properties properties, SessionFactory sessionFactory,
            EventNotificator eventNotificator) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating processor.");
        }
        Processor processor = new Processor(sessionFactory, eventNotificator);
        if (logger.isDebugEnabled()) {
            logger.debug("Processor created.");
        }
        return processor;
    }

    private static ProcessLogger createProcessLogger(String basePath, Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating process logger.");
        }
        String syncRequsetPath = restoreFilename(basePath,
                properties.getProperty(PROCESSOR_PARAM_BASE + ".org.sync.in.log.path"));
        String syncResponsePath = restoreFilename(basePath,
                properties.getProperty(PROCESSOR_PARAM_BASE + ".org.sync.out.log.path"));
        String paymentRequsetPath = restoreFilename(basePath,
                properties.getProperty(PROCESSOR_PARAM_BASE + ".client.payment.in.log.path"));
        String paymentResponsePath = restoreFilename(basePath,
                properties.getProperty(PROCESSOR_PARAM_BASE + ".client.payment.out.log.path"));
        ProcessLogger processLogger = new ProcessLogger(syncRequsetPath, syncResponsePath, paymentRequsetPath,
                paymentResponsePath);
        if (logger.isDebugEnabled()) {
            logger.debug("Process logger created.");
        }
        return processLogger;
    }

    private static String restoreFilename(String defaultPath, String filename) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            return FilenameUtils.concat(defaultPath, filename);
        }
        return filename;
    }

    private static ClientAuthenticator createClientAuthenticator(SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating client authenticator.");
        }
        ClientAuthenticator authenticator = new ClientAuthenticator(sessionFactory);
        if (logger.isDebugEnabled()) {
            logger.debug("Client authenticator created.");
        }
        authenticator.initUserFunctions();
        return authenticator;
    }

    private static PrivateKey loadSyncPrivateKeyData(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading sync private key.");
        }
        String privateKeyText = properties.getProperty(PROCESSOR_PARAM_BASE + ".org.sync.privateKey");
        PrivateKey privateKey = DigitalSignatureUtils.convertToPrivateKey(privateKeyText);
        if (logger.isDebugEnabled()) {
            logger.debug("Sync private key loaded.");
        }
        return privateKey;
    }

    private static PrivateKey loadPaymentPrivateKeyData(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading payment private key.");
        }
        String privateKeyText = properties.getProperty(PROCESSOR_PARAM_BASE + ".client.payment.privateKey");
        PrivateKey privateKey = DigitalSignatureUtils.convertToPrivateKey(privateKeyText);
        if (logger.isDebugEnabled()) {
            logger.debug("Payment private key loaded.");
        }
        return privateKey;
    }

    private static Postman createPostman(Properties properties, SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating postman.");
        }
        Postman.MailSettings notificationMailSettings = readMailSettings(properties, AUTO_REPORT_MAIL_PARAM_BASE);
        Postman.MailSettings supportMailSettings = readMailSettings(properties, SUPPORT_MAIL_PARAM_BASE);
        Postman postman = new Postman(notificationMailSettings, supportMailSettings);
        if (logger.isDebugEnabled()) {
            logger.debug("Postman created.");
        }
        return postman;
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

    private static RuleProcessor createRuleHandler(Properties properties, SessionFactory sessionFactory,
            AutoReportPostman autoReportPostman, EventNotificationPostman eventNotificationPostman) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating rule processor.");
        }
        RuleProcessor ruleProcessor = new RuleProcessor(sessionFactory, autoReportPostman, eventNotificationPostman);
        ruleProcessor.loadAutoReportRules();
        ruleProcessor.loadEventNotificationRules();
        if (logger.isDebugEnabled()) {
            logger.debug("Rule processor created.");
        }
        return ruleProcessor;
    }

    private static AutoReportGenerator createAutoReportGenerator(String basePath, Properties properties,
            ExecutorService executorService, Scheduler scheduler, SessionFactory sessionFactory,
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

    private static ClientSmsDeliveryStatusUpdater createClientSmsDeliveryStatusUpdater(Properties properties,
            ExecutorService executorService, Scheduler scheduler, SessionFactory sessionFactory, SmsService smsService)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SMS delivery status updater.");
        }
        int period = Integer.parseInt(properties.getProperty(CLIENT_SMS_PARAM_BASE + ".deliveryStatusPeriod", "900"));
        ClientSmsDeliveryStatusUpdater updater = new ClientSmsDeliveryStatusUpdater(executorService, scheduler, period,
                sessionFactory, smsService);
        if (logger.isDebugEnabled()) {
            logger.debug("SMS delivery status updater created.");
        }
        return updater;
    }

    private static EventNotificator createEventNotificator(Properties properties, ExecutorService executorService,
            SessionFactory sessionFactory, EventProcessor eventProcessor) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating event notificator.");
        }
        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar calendar = Calendar.getInstance(localTimeZone);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        EventNotificator notificator = new EventNotificator(executorService, eventProcessor, sessionFactory,
                properties.getProperty(EVENT_PARAM_BASE + ".path"), dateFormat, timeFormat);
        if (logger.isDebugEnabled()) {
            logger.debug("Event notificator created.");
        }
        return notificator;
    }

    private static MessageIdGenerator createMessageIdGenerator(Properties properties, SessionFactory sessionFactory)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SMS message ID generator.");
        }
        MessageIdGenerator generator = new MessageIdGenerator(sessionFactory);
        if (logger.isDebugEnabled()) {
            logger.debug("SMS message ID generator created.");
        }
        return generator;
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

    private static String buildPayformUrl(Properties properties) throws Exception {
        return properties.getProperty(AUTO_REPORT_PARAM_BASE + ".payform.url");
    }

    private static String buildPayformGroupUrl(Properties properties) throws Exception {
        return properties.getProperty(AUTO_REPORT_PARAM_BASE + ".payformgroup.url");
    }

    private static SmsService createSmsService(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SMS service.");
        }
        String serviceUrl = properties
                .getProperty(SMS_SERVICE_PARAM_BASE + ".url", "http://atompark.com/members/sms/xml.php");
        String userName = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".user", "kolpakov.igor@gmail.com");
        String password = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".password", "nkjngnnwdv");
        String sender = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".sender", "Novshkola");
        String timeZone = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".timeZone", "GMT-1");
        SmsService smsService = new SmsService(new SmsService.Config(serviceUrl, userName, password, sender, timeZone));
        if (logger.isDebugEnabled()) {
            logger.debug("SMS service created.");
        }
        return smsService;
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

    private static class ShutdownSchedulerTask implements Runnable {

        private final Scheduler scheduler;
        private final CountDownLatch doneSignal;

        public ShutdownSchedulerTask(Scheduler scheduler, CountDownLatch doneSignal) {
            this.scheduler = scheduler;
            this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                if (null != scheduler) {
                    try {
                        scheduler.shutdown(false);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Application-wide job scheduler stopped.");
                        }
                    } catch (Exception e) {
                        logger.error("Failed to shutdown scheduler", e);
                    }
                }
            } finally {
                doneSignal.countDown();
            }
        }
    }

    private static void destroy(ExecutorService executorService, Scheduler scheduler) {
        if (logger.isDebugEnabled()) {
            logger.debug("Shutting down application-wide executor service and job scheduler.");
        }
        if (null != executorService) {
            executorService.shutdown();
        }
        CountDownLatch doneSignal = new CountDownLatch(2);
        new Thread(new ShutdownExecutorServiceTask(executorService, 300000, doneSignal)).start();
        new Thread(new ShutdownSchedulerTask(scheduler, doneSignal)).start();
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            logger.error("Failed during waiting for executors termination", e);
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

    public String getPropertiesValue(String name, String defaultValue) {
        return configProperties.getProperty(name, defaultValue);
    }

    public int getPropertiesValue(String name, int defaultValue) {
        return Integer.parseInt(configProperties.getProperty(name, ""+defaultValue));
    }

    HashMap<Integer, String> optionsValues;

    public void loadOptionValues() {
        optionsValues = new HashMap<Integer, String>();
        for (int nOption=2;nOption<=Option.OPTION_MAX;nOption++) {
            String v = DAOUtils.getOptionValue(em, nOption, Option.getDefaultValue(nOption));
            optionsValues.put(nOption, v);
        }
    }

    public boolean getOptionValueBool(int optionId) {
        return getOptionValueString(optionId).equals("1");
    }

    public int getOptionValueInt(int optionId) {
        return Integer.parseInt(getOptionValueString(optionId));
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

    @Transactional
    public void saveOptionValues() {
        for (Map.Entry<Integer, String> e : optionsValues.entrySet()) {
            Option o = new Option((long)e.getKey(), e.getValue());
            o = em.merge(o);
            em.persist(o);
        }
    }
}

