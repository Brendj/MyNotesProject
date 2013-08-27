/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ClientAuthenticator;
import ru.axetta.ecafe.processor.core.client.ClientPasswordRecover;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.EventProcessor;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.elecsnet.ElecsnetConfig;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.partner.sbrt.SBRTConfig;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.payment.PaymentLogger;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodComplaintIterationStatus;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodComplaintPossibleCauses;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.AutoReportPostman;
import ru.axetta.ecafe.processor.core.report.AutoReportProcessor;
import ru.axetta.ecafe.processor.core.service.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.sms.ClientSmsDeliveryStatusUpdater;
import ru.axetta.ecafe.processor.core.sms.MessageIdGenerator;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.altarix.AltarixSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.atompark.AtomparkSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.smpp.SMPPClient;
import ru.axetta.ecafe.processor.core.sms.teralect.TeralectSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.SyncProcessor;
import ru.axetta.ecafe.processor.core.sync.manager.IntegroLogger;
import ru.axetta.ecafe.processor.core.updater.DBUpdater;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CxfContextCapture;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
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

@Component
@Scope("singleton")
public class RuntimeContext implements ApplicationContextAware {


    public static FinancialOpsManager getFinancialOpsManager() {
        return getAppContext().getBean(FinancialOpsManager.class);
    }

    public static boolean isTestRunning() {
        return getAppContext().containsBean("testDBInit");
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

    private static ApplicationContext applicationContext;

    private static final String PROCESSOR_PARAM_BASE = "ecafe.processor";
    public static final String PARAM_NAME_DB_MAINTANANCE_HOUR = PROCESSOR_PARAM_BASE + ".dbmaintanance.hour";
    public static final String PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM = PROCESSOR_PARAM_BASE + ".processor.hiddenPages";
    private static final String AUTO_REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".autoreport";
    private static final String REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".report";
    private static final String REPORT_PARAM_BASE_KEY = REPORT_PARAM_BASE + ".";
    private static final String EVENT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".event";
    private static final String AUTO_REPORT_MAIL_PARAM_BASE = AUTO_REPORT_PARAM_BASE + ".mail";
    private static final String SMS_SERVICE_PARAM_BASE = PROCESSOR_PARAM_BASE + ".sms.service";
    private static final String SMS_SERVICE_PARAM_CHECK_DELIVERY = SMS_SERVICE_PARAM_BASE + ".checkDelivery";
    private static final String SUPPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".support";
    private static final String SUPPORT_MAIL_PARAM_BASE = SUPPORT_PARAM_BASE + ".mail";
    private static final String CLIENT_SMS_PARAM_BASE = PROCESSOR_PARAM_BASE + ".client.sms";
    private static final String WS_CRYPTO_BASE = PROCESSOR_PARAM_BASE + ".ws.crypto";
    private static final String INSTANCE_NAME = PROCESSOR_PARAM_BASE + ".instance";
    private static final String NODE_INFO_FILE = PROCESSOR_PARAM_BASE + ".nodeInfoFile";

    public final static int NODE_ROLE_MAIN = 1, NODE_ROLE_PROCESSOR = 2;
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeContext.class);
    // Lock for global instance anchor
    private static final Object INSTANCE_LOCK = new Object();

    private String instanceName;
    private int roleNode = NODE_ROLE_MAIN;
    private String nodeName;
    // Application wide executor service
    private ExecutorService executorService;
    // Application wide job scheduler
    private Scheduler scheduler;
    private SyncProcessor syncProcessor;
    private SyncLogger syncLogger;
    private PaymentLogger paymentLogger;
    private PaymentProcessor paymentProcessor;
    private IntegroLogger integroLogger;
    private Processor processor;


    private OnlinePaymentProcessor onlinePaymentProcessor;
    private ClientPaymentOrderProcessor clientPaymentOrderProcessor;
    private PrivateKey syncPrivateKey;
    private PrivateKey paymentPrivateKey;
    private ClientAuthenticator clientAuthenticator;

    private ClientPasswordRecover clientPasswordRecover;

    private RuleProcessor autoReportProcessor;
    private EventProcessor eventProcessor;
    private AutoReportGenerator autoReportGenerator;
    private String payformUrl;
    private String payformGroupUrl;
    private ISmsService smsService;
    private Postman postman;
    private ClientSmsDeliveryStatusUpdater clientSmsDeliveryStatusUpdater;
    private MessageIdGenerator messageIdGenerator;
    private ContractIdGenerator clientContractIdGenerator;
    private CardManager cardManager;
    private OrderCancelProcessor orderCancelProcessor;

    private RBKMoneyConfig partnerRbkMoneyConfig;
    ////////////////////////////////////////////
    private ChronopayConfig partnerChronopayConfig;
    ///////////////////////////////////////////
    private SBRTConfig partnerSbrtConfig;
    private ElecsnetConfig partnerElecsnetConfig;
    private StdPayConfig partnerStdPayConfig;
    private IntegraPartnerConfig integraPartnerConfig;

    public static RuntimeContext getInstance() throws NotInitializedException {
        return getAppContext().getBean(RuntimeContext.class);
    }

    public SBRTConfig getPartnerSBRTConfig() {
        return partnerSbrtConfig;
    }

    public ElecsnetConfig getPartnerElecsnetConfig() {
        return partnerElecsnetConfig;
    }

    public IntegraPartnerConfig getIntegraPartnerConfig() {
        return integraPartnerConfig;
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

    public ISmsService getSmsService() {
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

    public IntegroLogger getIntegroLogger() {
        return integroLogger;
    }

    public void setIntegroLogger(IntegroLogger integroLogger) {
        this.integroLogger = integroLogger;
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

    public ClientPasswordRecover getClientPasswordRecover() {
        return clientPasswordRecover;
    }

    public Postman getPostman() {
        return postman;
    }

    public RBKMoneyConfig getPartnerRbkMoneyConfig() {
        return partnerRbkMoneyConfig;
    }

    ///////////////////////////////////////////////////////////


    public String getInstanceName() {
        return instanceName;
    }

    public String getInstanceNameDecorated() {
        return StringUtils.isEmpty(instanceName) ? ""
                : (" (" + instanceName + ")") + (StringUtils.isEmpty(nodeName) ? "" : "[" + nodeName + "]");
    }

    ///////////////////////////////////////////////////////////


    public ChronopayConfig getPartnerChronopayConfig() {
        return partnerChronopayConfig;
    }

    //////////////////////////////////////////////////////////////
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


    boolean criticalErrors;

    Properties configProperties;

    @PostConstruct
    public void init() throws Exception {
        // to run in transaction
        applicationContext.getBean(this.getClass()).initDB();

        String basePath = "/";
        Properties properties = loadConfig();
        if (properties == null) {
            properties = new Properties();
        }
        configProperties = properties;
        Scheduler scheduler = null;
        ExecutorService executorService = null;
        ProcessLogger processLogger = null;
        RuleProcessor ruleProcessor = null;
        EventNotificator eventNotificator = null;
        ISmsService smsService = null;
        //sessionFactory = ((Session)entityManagerFactory.createEntityManager()).getSessionFactory();
        //logger.info("sf = "+sessionFactory);
        try {

            loadDataFiles();

            instanceName = properties.getProperty(INSTANCE_NAME);

            String nodeRoleFile = properties.getProperty(NODE_INFO_FILE);
            if (nodeRoleFile != null) {
                try {
                    BufferedReader buf = new BufferedReader(new FileReader(nodeRoleFile));
                    String role = buf.readLine();
                    nodeName = buf.readLine();
                    if (role.equalsIgnoreCase("main")) {
                        roleNode = NODE_ROLE_MAIN;
                    } else if (role.equalsIgnoreCase("processor")) {
                        roleNode = NODE_ROLE_PROCESSOR;
                    } else {
                        throw new Exception("Unknown node role: " + role + " (valid: main, processor)");
                    }
                    logger.info("CLUSTER NODE ROLE: " + role + "; NAME: " + nodeName);
                } catch (FileNotFoundException x) {
                    nodeName = "UNKNOWN";
                    roleNode = NODE_ROLE_PROCESSOR;
                } catch (Exception e) {
                    logger.error("Failed to load node info", e);
                    throw e;
                }
            }

            if (!isTestRunning()) {

                executorService = createExecutorService(properties);
                this.executorService = executorService;

                scheduler = createScheduler(properties);
                this.scheduler = scheduler;

                postman = createPostman(properties, sessionFactory);
            }


            ruleProcessor = createRuleHandler(properties, sessionFactory, postman, postman);
            this.autoReportProcessor = ruleProcessor;
            this.eventProcessor = ruleProcessor;

            smsService = createSmsService(properties);
            this.smsService = smsService;

            this.partnerRbkMoneyConfig = new RBKMoneyConfig(properties, PROCESSOR_PARAM_BASE,
                    getOptionValueDouble(Option.OPTION_RBK_RATE), getOptionValueBool(Option.OPTION_RBK_SECTION));
            //////////////////////////////////////////////////////////////////////////////////////////////
            this.partnerChronopayConfig = new ChronopayConfig(properties, PROCESSOR_PARAM_BASE,
                    getOptionValueDouble(Option.OPTION_CHRONOPAY_RATE),
                    getOptionValueBool(Option.OPTION_CHRONOPAY_SECTION));
            /////////////////////////////////////////////////////////////////////////////////////////////
            this.partnerSbrtConfig = new SBRTConfig(properties, PROCESSOR_PARAM_BASE);
            this.partnerElecsnetConfig = new ElecsnetConfig(properties, PROCESSOR_PARAM_BASE);
            try {
                this.partnerStdPayConfig = new StdPayConfig(properties, PROCESSOR_PARAM_BASE);
            } catch (Exception e) {
                logger.error("Failed to load std pay config: " + e);
                criticalErrors = true;
            }
            try {
                this.integraPartnerConfig = new IntegraPartnerConfig(properties, PROCESSOR_PARAM_BASE);
            } catch (Exception e) {
                logger.error("Failed to load partner config: " + e);
                criticalErrors = true;
            }

            this.syncPrivateKey = loadSyncPrivateKeyData(properties);
            this.paymentPrivateKey = loadPaymentPrivateKeyData(properties);

            processLogger = createProcessLogger(basePath, properties);
            this.syncLogger = processLogger;
            this.paymentLogger = processLogger;
            this.integroLogger = processLogger;

            eventNotificator = createEventNotificator(properties, executorService, sessionFactory, ruleProcessor);

            processor = createProcessor(properties, sessionFactory, eventNotificator);
            this.cardManager = processor;
            this.syncProcessor = processor;
            this.paymentProcessor = processor;
            this.clientPaymentOrderProcessor = processor;
            this.orderCancelProcessor = processor;

            this.onlinePaymentProcessor = new OnlinePaymentProcessor(processor);

            this.clientAuthenticator = createClientAuthenticator(sessionFactory);

            this.clientPasswordRecover = createClientPasswordRecover(sessionFactory);

            this.autoReportGenerator = createAutoReportGenerator(basePath, properties, executorService, scheduler,
                    sessionFactory, ruleProcessor);

            this.clientSmsDeliveryStatusUpdater = createClientSmsDeliveryStatusUpdater(properties, executorService,
                    scheduler, sessionFactory, smsService);

            this.payformUrl = buildPayformUrl(properties);
            this.payformGroupUrl = buildPayformGroupUrl(properties);
            this.messageIdGenerator = createMessageIdGenerator(properties, sessionFactory);
            this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);

            // Start background activities
            if (isMainNode()) {
                this.autoReportGenerator.start();
            }

            //
            String checkSMSDelivery = properties.getProperty(SMS_SERVICE_PARAM_CHECK_DELIVERY);
            if (checkSMSDelivery != null && (checkSMSDelivery.equals("1") || 0 == checkSMSDelivery
                    .compareToIgnoreCase("true")) && isMainNode()) {
                this.clientSmsDeliveryStatusUpdater.start();
            }

            //
            if (!isTestRunning()) {
                initWSCrypto();
            }
        } catch (Exception e) {
            destroy(executorService, scheduler);
            throw e;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Runtime context created.");
        }
    }

    private void initWSCrypto() {
        java.util.Properties signatureProps = (java.util.Properties) CxfContextCapture.getApplicationContextInstance()
                .getBean("wsCryptoProperties");
        String params[] = {
                "keystore.type", "keystore.password", "file", "truststore.type", "truststore.password",
                "truststore.file"};
        for (String s : params) {
            signatureProps.setProperty("org.apache.ws.security.crypto.merlin." + s,
                    configProperties.getProperty(WS_CRYPTO_BASE + "." + s, ""));
        }
        /*<prop key="org.apache.ws.secnurity.crypto.merlin.keystore.type">JKS</prop>
        <prop key="org.apache.ws.security.crypto.merlin.keystore.password">123456</prop>
        <prop key="org.apache.ws.security.crypto.merlin.file">/temp/certs/alice.jks</prop>
        <prop key="org.apache.ws.security.crypto.merlin.truststore.type">PKCS12</prop>
        <prop key="org.apache.ws.security.crypto.merlin.truststore.password">BCGG00</prop>
        <prop key="org.apache.ws.security.crypto.merlin.truststore.file">/temp/certs/ispp_agent_istk.pfx</prop>
        */
    }

    SchemaVersionInfo currentSchemaVersionInfo;

    public SchemaVersionInfo getCurrentDBSchemaVersion() {
        return currentSchemaVersionInfo;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DAOService daoService;

    @Autowired
    private DBUpdater updater;



    @Transactional
    public void initDB() throws Exception {
        // Configure runtime context
        try {
            // check DB version
            if(isMainNode()){
                currentSchemaVersionInfo = updater.checkDbVersion();
                //
                loadOptionValues();
                // Check for Operator, Budget, Client type of contragents existence
                boolean operatorExists = false;
                boolean budgetExists = false;
                boolean clientExists = false;
                final List<Integer> classId = Arrays.asList(Contragent.OPERATOR, Contragent.BUDGET, Contragent.CLIENT);
                List<Contragent> contragentList = daoService.getContragentsWithClassIds(classId);
                // Create if not
                for (Contragent contragent : contragentList) {
                    if (contragent.getClassId().equals(Contragent.OPERATOR)) {
                        operatorExists = true;
                        logger.info("Contragent with class \"Operator\" exists, name \"" + contragent.getContragentName()
                                + "\"");
                    }
                    if (contragent.getClassId().equals(Contragent.BUDGET)) {
                        budgetExists = true;
                        logger.info(
                                "Contragent with class \"Budget\" exists, name \"" + contragent.getContragentName() + "\"");
                    }
                    if (contragent.getClassId().equals(Contragent.CLIENT)) {
                        clientExists = true;
                        logger.info(
                                "Contragent with class \"Client\" exists, name \"" + contragent.getContragentName() + "\"");
                    }
                }

                if (!operatorExists) {
                    createContragent("Оператор", 3);
                }
                if (!budgetExists) {
                    createContragent("Бюджет", 4);
                }
                if (!clientExists) {
                    createContragent("Клиент", 5);
                }

                logger.info("Initialize default client category");
                HashMap<Long, String> initCategory = new HashMap<Long, String>();
                initCategory.put(-90L, "Начальные классы");
                initCategory.put(-91L, "Средние классы");
                initCategory.put(-92L, "Старшие классы");
                initCategory.put(-101L, "1 класс");
                initCategory.put(-102L, "2 класс");
                initCategory.put(-103L, "3 класс");
                initCategory.put(-104L, "4 класс");
                initCategory.put(-105L, "5 класс");
                initCategory.put(-106L, "6 класс");
                initCategory.put(-107L, "7 класс");
                initCategory.put(-108L, "8 класс");
                initCategory.put(-109L, "9 класс");
                initCategory.put(-110L, "10 класс");
                initCategory.put(-111L, "11 класс");
                initCategory.put(-200L, "Сотрудник");
                initCategory.put(-201L, "Ученик");
                for (Map.Entry me : initCategory.entrySet()) {
                    CategoryDiscount cd = DAOUtils.getCategoryDiscount(entityManager, (Long) me.getKey());
                    if (cd == null) {
                        createCategoryDiscount((Long) me.getKey(), String.valueOf(me.getValue()), "", "");
                    }
                }

                DAOUtils.clearGoodComplaintIterationStatus(entityManager);
                for (GoodComplaintIterationStatus iterationStatus : GoodComplaintIterationStatus.values()) {
                    entityManager.persist(iterationStatus);
                }

                DAOUtils.clearGoodComplaintPossibleCauses(entityManager);
                for (GoodComplaintPossibleCauses possibleCauses : GoodComplaintPossibleCauses.values()) {
                    entityManager.persist(possibleCauses);
                }

                /**
                 *  Дополняем всем клиентам guid у тех у кого они пусты
                 *  */
                //List<Long> clients = DAOUtils.extractIdFromClientsByGUIDIsNull(entityManager);
                //logger.info("Generate update uuid in client: "+clients.size());
                //daoService.updateClientSetGUID(clients);

                /**
                 * Инициализируем список ролей для комплексов
                 * */
                logger.info("Initialize default complex role");
                for (long i = 0L; i < 50L; i++) {
                    ComplexRole complexRole = entityManager.find(ComplexRole.class, i);
                    if (complexRole == null) {
                        complexRole = new ComplexRole(i, String.format("Комплекс %d", i));
                        entityManager.merge(complexRole);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to init application.", e);
            throw e;
        }
    }



    public void createCategoryDiscount(Long idOfCategoryDiscount, String categoryName, String discountRules,
            String description) {
        Date currentTime = new Date();
        CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, discountRules,
                description, currentTime, currentTime);
        categoryDiscount.setCategoryType(CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
        entityManager.persist(categoryDiscount);
        logger.info("Category with name \"" + categoryName + "\" created");
    }

    public void createContragent(String contragentName, Integer classId) throws Exception {
        Person contactPerson = new Person("Иван", "Иванов", "");
        entityManager.persist(contactPerson);
        Date currentTime = new Date();
        Contragent contragent = new Contragent(contactPerson, contragentName, classId, 1, "", "", currentTime,
                currentTime, "", "", "", false);
        entityManager.persist(contragent);
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
            Option option = (Option) criteria.uniqueResult();
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
        String intgeroRequestLogPath = null;
        String intgeroResponseLogPath = null;
        try {
            intgeroRequestLogPath = restoreFilename(basePath,
                    properties.getProperty(PROCESSOR_PARAM_BASE + ".org.integro.in.log.path"));
        } catch (Exception e) {
            logger.error("IntegRO input files not saved.");
        }
        try {
            intgeroResponseLogPath = restoreFilename(basePath,
                    properties.getProperty(PROCESSOR_PARAM_BASE + ".org.integro.out.log.path"));
        } catch (Exception e) {
            logger.error("IntegRO output files not saved.");
        }

        ProcessLogger processLogger = new ProcessLogger(syncRequsetPath, syncResponsePath, paymentRequsetPath,
                paymentResponsePath, intgeroRequestLogPath, intgeroResponseLogPath);
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

    private static ClientPasswordRecover createClientPasswordRecover(SessionFactory sessionFactory) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating client authenticator.");
        }
        ClientPasswordRecover passwordRecover = new ClientPasswordRecover(sessionFactory);
        if (logger.isDebugEnabled()) {
            logger.debug("Client authenticator created.");
        }
        return passwordRecover;
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
        Postman.MailSettings reportMailSettings = readMailSettings(properties, AUTO_REPORT_MAIL_PARAM_BASE);
        Postman.MailSettings supportMailSettings = readMailSettings(properties, SUPPORT_MAIL_PARAM_BASE);
        Postman postman = new Postman(reportMailSettings, supportMailSettings);
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
            ExecutorService executorService, Scheduler scheduler, SessionFactory sessionFactory, ISmsService smsService)
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

    private static ISmsService createSmsService(Properties properties) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SMS service.");
        }
        String serviceType = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".type", "atompark");
        String serviceUrl = properties
                .getProperty(SMS_SERVICE_PARAM_BASE + ".url", "http://atompark.com/members/sms/xml.php");
        String userName = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".user", "kolpakov.igor@gmail.com");
        String password = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".password", "nkjngnnwdv");
        String sender = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".sender", "Novshkola");
        String timeZone = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".timeZone", "GMT-1");
        String userServiceId = properties.getProperty(SMS_SERVICE_PARAM_BASE + ".serviceId", "14");

        ISmsService.Config config = new ISmsService.Config(serviceUrl, userName, password, sender, timeZone);
        ISmsService smsService = null;
        if (serviceType.equalsIgnoreCase("atompark")) {
            smsService = new AtomparkSmsServiceImpl(config);
        } else if (serviceType.equalsIgnoreCase("teralect")) {
            smsService = new TeralectSmsServiceImpl(config);
        } else if (serviceType.equalsIgnoreCase("altarix")) {
            smsService = new AltarixSmsServiceImpl(config, userServiceId);
        } else if (serviceType.equalsIgnoreCase("smpp")) {
            smsService = new SMPPClient(config, properties, SMS_SERVICE_PARAM_BASE);
        } else {
            throw new Exception("Invalid SMS service type: " + serviceType);
        }

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
        return Integer.parseInt(configProperties.getProperty(name, "" + defaultValue));
    }

    HashMap<Integer, String> optionsValues;

    public void loadOptionValues() {
        optionsValues = new HashMap<Integer, String>();
        for (int n = 0; n < Option.OPTIONS_INITIALIZER.length; n += 2) {
            Integer nOption = (Integer) Option.OPTIONS_INITIALIZER[n];
            String v = DAOUtils.getOptionValue(entityManager, nOption, (String) Option.OPTIONS_INITIALIZER[n + 1]);
            optionsValues.put(nOption, v);
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
        o = entityManager.merge(o);
        entityManager.persist(o);
    }

    public void setOptionValue(int optionId, double value) {
        setOptionValue(optionId, value + "");
    }

    @Transactional
    public void saveOptionValues() {
        for (Map.Entry<Integer, String> e : optionsValues.entrySet()) {
            Option o = new Option((long) e.getKey(), e.getValue());
            o = entityManager.merge(o);
            entityManager.persist(o);
        }
    }

    public boolean getCriticalErrors() {
        return criticalErrors;
    }

    public final static int TYPE_S = 0, TYPE_P = 1, TYPE_B = 2;
    public static int permittedCountS, permittedCountP, permittedCountB;
    public static HashSet<Long> orgS = new HashSet<Long>(), orgP = new HashSet<Long>(), orgB = new HashSet<Long>();

    public boolean isPermitted(long orgId, int type) {
        if (type == TYPE_S) {
            orgS.add(orgId);
            return orgS.size() <= 1 || orgS.size() <= permittedCountS;
        }
        if (type == TYPE_P) {
            orgP.add(orgId);
            return orgP.size() <= 1 || orgP.size() <= permittedCountP;
        }
        if (type == TYPE_B) {
            orgB.add(orgId);
            return orgB.size() <= 1 || orgB.size() <= permittedCountB;
        }
        return false;
    }

    public void loadDataFiles() {
        String dir = System.getProperty("lacinsidar".replaceAll("i", "e").replaceAll("a", "i"), "");
        if (dir != null && !dir.isEmpty()) {
            File[] files = new File(dir).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".lic"));
                }
            });
            for (File f : files) {
                processDataFile(f);
            }
        }
    }

    private static String base64crt = "MIICMTCCAZqgAwIBAgIQgEacs/dm35tGB5jLKRy6GDANBgkqhkiG9w0BAQQFADAi\n"
            + "MSAwHgYDVQQDExdsaWNlbnNlLm5vdmF5YXNoa29sYS5ydTAeFw0xMjA4MDMwNjAz\n"
            + "MDBaFw0zNTEyMzEyMDAwMDBaMCIxIDAeBgNVBAMTF2xpY2Vuc2Uubm92YXlhc2hr\n"
            + "b2xhLnJ1MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9pdRyozu+dRELgRpb\n"
            + "kDrIc9RHRiNAj4LS1HQjZmGPbtjdRyC8AcdaeO3M1fGUe+iiON/9ldZLwsCB6hTh\n"
            + "VkJijZ+6gsqLEiMxN/Wo5THFXYGDSYvq6t1dlgt/K5/ctXR86carj1beZ3eCPE5G\n"
            + "rZ6eUbIaDHU0NgMd8p8L4H0aRQIDAQABo2gwZjAPBgNVHRMBAf8EBTADAQH/MFMG\n"
            + "A1UdAQRMMEqAEJvi89wDT2YruumkBLTUaaehJDAiMSAwHgYDVQQDExdsaWNlbnNl\n"
            + "Lm5vdmF5YXNoa29sYS5ydYIQgEacs/dm35tGB5jLKRy6GDANBgkqhkiG9w0BAQQF\n"
            + "AAOBgQBJxDdetDvHdUrzztZoHhfJwDOGYx/bp1zNtd75RVfvM/+Gwu4AiW6CQfLB\n"
            + "qc085KjxxnQZ2Si7FoDhwJ3gCEEERs5YrA/O/Lde+kdUPT15GlZcguJHB5Jk83Ir\n"
            + "GmtI6Yxjlvzt1zcqpq4MZM3HTLdz4gibDBPGG3cd692TYkHeFg==";
    private static X509Certificate rtCert = null; // корневой сертификат

    private static X509Certificate getRootCert() throws Exception {
        if (rtCert == null) {
            byte bytes[] = Base64.decode(base64crt);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            rtCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bytes));
        }
        return rtCert;
    }

    private static Set<TrustAnchor> getTrustStore() throws Exception {
        Set<TrustAnchor> resultSet = new HashSet<TrustAnchor>(1);
        resultSet.add(new TrustAnchor(getRootCert(), null));
        return resultSet;
    }

    public static class DataInfo {

        public String org;
        public String dn;
        public int sCount, pCount, bCount;
        public String id, location;
        public Date validTo, issued;
        public boolean valid;

        public String getOrg() {
            return org;
        }

        public String getId() {
            return id;
        }

        public String getLocation() {
            return location;
        }

        public String getInfo() {
            return sCount + "/" + pCount + "/" + bCount;
        }

        public String getExpiryDate() {
            return CalendarUtils.dateToString(validTo);
        }

        public String getIssuedDate() {
            return CalendarUtils.dateToString(issued);
        }

        public String getValidInfo() {
            return valid ? "Валидна" : "Невалидна";
        }
    }

    public LinkedList<DataInfo> dataInfos = new LinkedList<DataInfo>();

    public void processDataFile(File file) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            FileInputStream fis = new FileInputStream(file);
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
            fis.close();
            Date currentDate = new Date();
            boolean isValid = true;
            if (currentDate.after(cert.getNotAfter())) {
                isValid = false;
            }
            if (currentDate.before(cert.getNotBefore())) {
                isValid = false;
            }
            List<X509Certificate> mylist = new ArrayList<X509Certificate>();
            mylist.add(cert);
            CertPath cp = cf.generateCertPath(mylist);
            PKIXParameters pkiXParameters = new PKIXParameters(getTrustStore());
            pkiXParameters.setRevocationEnabled(false);
            CertPathValidator cpv = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
            PKIXCertPathValidatorResult pkixCertPathValidatorResult = (PKIXCertPathValidatorResult) cpv
                    .validate(cp, pkiXParameters);
            isValid = getRootCert().equals(pkixCertPathValidatorResult.getTrustAnchor().getTrustedCert());
            DataInfo dataInfo = new DataInfo();
            // проверили сертификат
            String dn = cert.getSubjectDN().getName();
            dataInfo.dn = dn;
            dataInfo.org = getDNField(dn, "O");
            for (DataInfo di : dataInfos) {
                // проверяем не был ли сертификат уже загружен
                if (di.dn.equals(dn)) {
                    return;
                }
                // проверяем чтобы была одинаковая организация и локация
                if (!di.org.equals(dataInfo.org)) {
                    return;
                }
                if (!di.location.equals(dataInfo.location)) {
                    return;
                }
            }
            dataInfo.id = getDNField(dn, "OU");
            dataInfo.location = getDNField(dn, "L");
            dataInfo.issued = cert.getNotBefore();
            dataInfo.validTo = cert.getNotAfter();
            dataInfo.valid = isValid;
            if (isValid) {
                String cn = getDNField(dn, "CN");
                String words[] = cn.split("-");
                for (String w : words) {
                    if (w.isEmpty()) {
                        continue;
                    }
                    int pos = w.indexOf('_');
                    if (pos == -1) {
                        continue;
                    }
                    String count = w.substring(0, pos), type = w.substring(pos + 1);
                    int lcount = Integer.parseInt(count);
                    if (type.equals("STD".replaceAll("T", "K"))) {
                        permittedCountS += (dataInfo.sCount = lcount);
                    } else if (type.equals("PXT".replaceAll("X", "I"))) {
                        permittedCountP += (dataInfo.pCount = lcount);
                    } else if (type.equals("BOB".replaceAll("O", "I"))) {
                        permittedCountB += (dataInfo.bCount = lcount);
                    }
                }
            }
            dataInfos.add(dataInfo);
        } catch (Exception e) {
            if (!System.getProperty("lacinsierror".replaceAll("i", "e").replaceAll("a", "i"), "").equals("")) {
                logger.error("Error loading file: " + file.getAbsolutePath(), e);
            }
        }
    }

    private String getDNField(String dn, String field) {
        int cnPos = dn.indexOf(field);
        if (cnPos != -1) {
            int cnEqPos = dn.indexOf("=", cnPos);
            int cnEndPos = dn.indexOf(",", cnPos);
            if (cnEndPos == -1) {
                cnEndPos = dn.length();
            }
            return dn.substring(cnEqPos + 1, cnEndPos);
        }
        return "";
    }

    public LinkedList<DataInfo> getDataInfos() {
        return dataInfos;
    }

    public Processor getProcessor() {
        return processor;
    }

    public boolean isMainNode() {
        return roleNode == NODE_ROLE_MAIN;
    }
}

