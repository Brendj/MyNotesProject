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
import ru.axetta.ecafe.processor.core.logic.*;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.order.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.partner.acquiropay.AcquiropaySystemConfig;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.elecsnet.ElecsnetConfig;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.mesh.card.taskexecutor.MeshCardNotifyTaskExecutor;
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
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.RegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.sms.ClientSmsDeliveryStatusUpdater;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.MessageIdGenerator;
import ru.axetta.ecafe.processor.core.sms.altarix.AltarixSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.atompark.AtomparkSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.smpp.SMPPClient;
import ru.axetta.ecafe.processor.core.sms.teralect.TeralectSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.SyncProcessor;
import ru.axetta.ecafe.processor.core.sync.manager.IntegroLogger;
import ru.axetta.ecafe.processor.core.updater.DBUpdater;
import ru.axetta.ecafe.processor.core.utils.*;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.stat.Statistics;
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
import javax.net.ssl.SSLContext;
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

@Component
@Scope("singleton")
public class RuntimeContext implements ApplicationContextAware {

    public static FinancialOpsManager getFinancialOpsManager() {
        return getAppContext().getBean(FinancialOpsManager.class);
    }

    public static boolean isTestRunning() {
        return getAppContext().containsBean("testDBInit");
    }

    public static boolean isOrgRoomRunning() {
        return getAppContext().containsBean("orgRoomCommonBean");
    }

    public static SessionFactory getReportsSessionFactory() {
        return reportsSessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public boolean isTestMode() {
        return Boolean.parseBoolean((String)configProperties.get("ecafe.processor.testMode"));
    }


    public String getScudLogin() {
        return scudLogin;
    }

    public void setScudLogin(String scudLogin) {
        this.scudLogin = scudLogin;
    }

    public String getScudPassword() {
        return scudPassword;
    }

    public void setScudPassword(String scudPassword) {
        this.scudPassword = scudPassword;
    }

    public IAuthorizeUserBySms getSmsUserCodeSender() {
        return smsUserCodeSender;
    }

    public String getGeoplanerApiKey() {
        return geoplanerApiKey;
    }

    public boolean isUsePriceSms() {
        return usePriceSms;
    }

    public void setUsePriceSms(boolean usePriceSms) {
        this.usePriceSms = usePriceSms;
    }

    public boolean isLogInfoService() {
        return logInfoService;
    }

    public void setLogInfoService(boolean logInfoService) {
        this.logInfoService = logInfoService;
    }

    public String[] getMethodsInfoService() {
        return methodsInfoService;
    }

    public void setMethodsInfoService(String[] methodsInfoService) {
        this.methodsInfoService = methodsInfoService;
    }

    public String getExtendCardServiceApiKey() {
        return extendCardServiceApiKey;
    }

    public void setExtendCardServiceApiKey(String extendCardServiceApiKey) {
        this.extendCardServiceApiKey = extendCardServiceApiKey;
    }

    public boolean isUseQueueForAllSyncs() {
        return useQueueForAllSyncs;
    }

    public void setUseQueueForAllSyncs(boolean useQueueForAllSyncs) {
        this.useQueueForAllSyncs = useQueueForAllSyncs;
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

    public String getOkuApiKey() {
        return okuApiKey;
    }

    public String getFrontControllerApiKey() {
        return frontControllerApiKey;
    }

    private static ApplicationContext applicationContext;

    public static final String PROCESSOR_PARAM_BASE = "ecafe.processor";
    public static final String PARAM_NAME_TIME_ZONE = PROCESSOR_PARAM_BASE + ".time.zone";
    public static final String PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM = PROCESSOR_PARAM_BASE + ".processor.hiddenPages";
    private static final String AUTO_REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".autoreport";
    private static final String REPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".report";
    private static final String REPORT_PARAM_BASE_KEY = REPORT_PARAM_BASE + ".";
    private static final String EVENT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".event";
    private static final String AUTO_REPORT_MAIL_PARAM_BASE = AUTO_REPORT_PARAM_BASE + ".mail";
    public static final String SMS_SERVICE_PARAM_BASE = PROCESSOR_PARAM_BASE + ".sms.service";
    private static final String SMS_SERVICE_PARAM_CHECK_DELIVERY = SMS_SERVICE_PARAM_BASE + ".checkDelivery";
    private static final String SUPPORT_PARAM_BASE = PROCESSOR_PARAM_BASE + ".support";
    private static final String SUPPORT_MAIL_PARAM_BASE = SUPPORT_PARAM_BASE + ".mail";
    private static final String CLIENT_SMS_PARAM_BASE = PROCESSOR_PARAM_BASE + ".client.sms";
    private static final String WS_CRYPTO_BASE = PROCESSOR_PARAM_BASE + ".ws.crypto";
    private static final String INSTANCE_NAME = PROCESSOR_PARAM_BASE + ".instance";
    private static final String NODE_INFO_FILE = PROCESSOR_PARAM_BASE + ".nodeInfoFile";
    public static final String ISTK_WEBSERVICE = PROCESSOR_PARAM_BASE + ".istk.webservice";
    public static final String IMAGE_CONFIG = PROCESSOR_PARAM_BASE + ".image";
    public static final String IMAGE_DIRECTORY = IMAGE_CONFIG + ".path";
    public static final String IMAGE_VALIDATION = IMAGE_CONFIG + ".validation";
    public static final String REGISTRY = PROCESSOR_PARAM_BASE + ".registry";
    public static final String REGISTRY_LOGIN = REGISTRY + ".login";
    public static final String REGISTRY_PASSWORD = REGISTRY + ".password";
    public static final String MEAL = PROCESSOR_PARAM_BASE + ".meal";
    public static final String MEAL_LOGIN = MEAL + ".login";
    public static final String MEAL_PASSWORD = MEAL + ".password";
    public static final String SCUD = PROCESSOR_PARAM_BASE + ".scud";
    public static final String SCUD_LOGIN = SCUD + ".login";
    public static final String SCUD_PASSWORD = SCUD + ".password";
    private static final String OKU_API_KEY = PROCESSOR_PARAM_BASE + ".oku.api.key";
    private static final String FRONT_CONTROLLER_API_KEY = PROCESSOR_PARAM_BASE + ".frontController.api.key";
    private static final String EXTEND_CARD_SERVICE_API_KEY = PROCESSOR_PARAM_BASE + ".extendCardService.api.key";

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
    private SyncCollector syncCollector = SyncCollector.getInstance();
    private PaymentLogger paymentLogger;
    private PaymentProcessor paymentProcessor;
    private IntegroLogger integroLogger;
    private Processor processor;
    private IAuthorizeUserBySms smsUserCodeSender;

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

    private RegistryType registryType;
    private String registryLogin;
    private String registryPassword;

    private String mealLogin;
    private String mealPassword;

    private String scudLogin;
    private String scudPassword;

    private String geoplanerApiKey;

    private String okuApiKey;
    private String extendCardServiceApiKey;
    private String frontControllerApiKey;

    private RBKMoneyConfig partnerRbkMoneyConfig;
    ////////////////////////////////////////////
    private ChronopayConfig partnerChronopayConfig;
    ///////////////////////////////////////////
    private SBRTConfig partnerSbrtConfig;
    private ElecsnetConfig partnerElecsnetConfig;
    private StdPayConfig partnerStdPayConfig;
    private IntegraPartnerConfig integraPartnerConfig;
    private AcquiropaySystemConfig acquiropaySystemConfig;
    private static SessionFactory sessionFactory;
    private static SessionFactory reportsSessionFactory;
    static SessionFactory externalServicesSessionFactory;
    private RegularPaymentSubscriptionService regularPaymentSubscriptionService;
    boolean criticalErrors;
    Properties configProperties;
    SchemaVersionInfo currentSchemaVersionInfo;
    private boolean usePriceSms;
    private boolean useQueueForAllSyncs;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    @Autowired
    private DBUpdater updater;
    public final static int TYPE_S = 0, TYPE_P = 1, TYPE_B = 2;
    public static int permittedCountS, permittedCountP, permittedCountB;
    public static HashSet<Long> orgS = new HashSet<Long>(), orgP = new HashSet<Long>(), orgB = new HashSet<Long>();
    HashMap<Integer, String> optionsValues;
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

    private boolean logInfoService;
    private String[] methodsInfoService;

    private SettingsConfig settingsConfig;

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

    public RegistryType getRegistryType() {
        return registryType;
    }

    public Session createPersistenceSession() throws Exception {
        if (isOrgRoomRunning()) {
            initiateOrgRoomEntityManager();
        return sessionFactory.openSession();
    }
        return sessionFactory.openSession();
    }

    public void initiateOrgRoomEntityManager () {
        if (entityManager == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("processorPU");
            entityManager = emf.createEntityManager();
            setSessionFactory (((Session) entityManager.getDelegate()).getSessionFactory());
        }
        if (sessionFactory == null) {
            setSessionFactory (((Session) entityManager.getDelegate()).getSessionFactory());
    }
    }

    public Session createReportPersistenceSession() {
        Session session = reportsSessionFactory.openSession();
        session.setDefaultReadOnly(true);
        session.setFlushMode(FlushMode.MANUAL);
        return session;
    }

    public Session createExternalServicesPersistenceSession() {
        Session session = externalServicesSessionFactory.openSession();
        session.setDefaultReadOnly(true);
        session.setFlushMode(FlushMode.MANUAL);
        return session;
    }

    //hibernate cache for processorPU
    public static Statistics statisticPersistenceSession() {
        return sessionFactory.getStatistics();
    }

    //hibernate cache for reportPU
    public static Statistics statisticReportPersistenceSession() {
        return reportsSessionFactory.getStatistics();
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

    public SyncCollector getSyncCollector() {
        return syncCollector;
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

    public AcquiropaySystemConfig getAcquiropaySystemConfig() {
        return acquiropaySystemConfig;
    }

    public String getRegistryLogin() {
        return registryLogin;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public String getMealPassword() {
        return mealPassword;
    }

    public String getMealLogin() {
        return mealLogin;
    }

    ///////////////////////////////////////////////////////////


    public String getInstanceName() {
        return instanceName;
    }

    public String getInstanceNameDecorated() {
        return StringUtils.isEmpty(instanceName) ? ""
                : (" (" + instanceName + ")") + (StringUtils.isEmpty(nodeName) ? "" : "[" + nodeName + "]");
    }

    public String getNodeName() {
        return nodeName;
    }

    ///////////////////////////////////////////////////////////


    public ChronopayConfig getPartnerChronopayConfig() {
        return partnerChronopayConfig;
    }

    //////////////////////////////////////////////////////////////
    public StdPayConfig getPartnerStdPayConfig() {
        return partnerStdPayConfig;
    }

    public TimeZone getLocalTimeZone(HttpSession httpSession) {
        String timeZone = getPropertiesValue(RuntimeContext.PARAM_NAME_TIME_ZONE, "Europe/Moscow");
        if (timeZone.equals("default")) {
            return TimeZone.getDefault();
        } else {
            return TimeZone.getTimeZone(timeZone);
        }
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

    public static void setSessionFactory(SessionFactory sessionFactory) {
        RuntimeContext.sessionFactory = sessionFactory;
    }

    public static void setReportsSessionFactory(SessionFactory reportsSessionFactory) {
        RuntimeContext.reportsSessionFactory = reportsSessionFactory;
    }

    public static void setExternalServicesSessionFactory(SessionFactory externalServicesSessionFactory) {
        RuntimeContext.externalServicesSessionFactory = externalServicesSessionFactory;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    @PostConstruct
    public void init() throws Exception {
        // to run in transaction
        applicationContext.getBean(this.getClass()).initDB();

        String basePath = "/";
        if (isOrgRoomRunning()) {
            initiateOrgRoomEntityManager();
            Properties properties = loadConfig();
            this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);
            /* Для autoreportgenerator */
            postman = createPostman(properties, sessionFactory);
            RuleProcessor ruleProcessor = createRuleHandler(properties, sessionFactory, postman, postman);
            this.autoReportProcessor = ruleProcessor;
            this.autoReportGenerator = createAutoReportGenerator(basePath, properties, executorService, scheduler,
                    reportsSessionFactory, ruleProcessor);
            return;
        }
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
            if (System.getProperty(NODE_INFO_FILE)!=null) {
                nodeRoleFile = System.getProperty(NODE_INFO_FILE);
            }
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

            String registryType = properties.getProperty(RuntimeContext.REGISTRY, "msk");
            if(registryType.equals(RegistryType.MSK.getName())) {
                this.registryType = RegistryType.MSK;
            } else if(registryType.equals(RegistryType.SPB.getName())) {
                this.registryType = RegistryType.SPB;
            }

            this.registryLogin = properties.getProperty(REGISTRY_LOGIN);
            this.registryPassword = properties.getProperty(REGISTRY_PASSWORD);

            this.mealLogin = properties.getProperty(MEAL_LOGIN);
            this.mealPassword = properties.getProperty(MEAL_PASSWORD);

            this.scudLogin = properties.getProperty(SCUD_LOGIN);
            this.scudPassword = properties.getProperty(SCUD_PASSWORD);

            this.geoplanerApiKey = properties.getProperty(PROCESSOR_PARAM_BASE + ".geoplaner.apikey");

            this.okuApiKey = properties.getProperty(OKU_API_KEY);
            this.extendCardServiceApiKey = properties.getProperty(EXTEND_CARD_SERVICE_API_KEY);

            this.frontControllerApiKey = properties.getProperty(FRONT_CONTROLLER_API_KEY);

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
            this.acquiropaySystemConfig = new AcquiropaySystemConfig(properties, PROCESSOR_PARAM_BASE);

            this.syncPrivateKey = loadSyncPrivateKeyData(properties);
            this.paymentPrivateKey = loadPaymentPrivateKeyData(properties);

            processLogger = createProcessLogger(basePath, properties);
            this.syncLogger = processLogger;
            this.paymentLogger = processLogger;
            this.integroLogger = processLogger;

            this.syncCollector.setProperties(getOptionValueBool(Option.OPTION_SAVE_SYNC_CALC));

            eventNotificator = createEventNotificator(properties, executorService, sessionFactory, ruleProcessor);

            smsUserCodeSender = createUserCodeSender(properties);

            processor = createProcessor(sessionFactory, eventNotificator);
            this.syncProcessor = processor;
            this.cardManager = createCardManagerProcessor(properties, sessionFactory, eventNotificator);
            paymentProcessor = createPaymentProcessor(properties, sessionFactory, eventNotificator);
            clientPaymentOrderProcessor = createClientPaymentOrderProcessor(properties, sessionFactory, eventNotificator);
            orderCancelProcessor = createOrderCancelProcessor(properties, sessionFactory, eventNotificator);

            this.onlinePaymentProcessor = new OnlinePaymentProcessor(processor);

            this.clientAuthenticator = createClientAuthenticator(sessionFactory);

            this.clientPasswordRecover = createClientPasswordRecover(sessionFactory);

            this.autoReportGenerator = createAutoReportGenerator(basePath, properties, executorService, scheduler,
                    reportsSessionFactory, ruleProcessor);

            this.clientSmsDeliveryStatusUpdater = createClientSmsDeliveryStatusUpdater(properties, executorService,
                    scheduler, sessionFactory, smsService);

            this.payformUrl = buildPayformUrl(properties);
            this.payformGroupUrl = buildPayformGroupUrl(properties);
            this.messageIdGenerator = createMessageIdGenerator(properties, sessionFactory);
            this.clientContractIdGenerator = createClientContractIdGenerator(properties, sessionFactory);
            // Start background activities
            if (isMainNode() && !isTestRunning()) {
                this.autoReportGenerator.start();
            }

            //
            String checkSMSDelivery = properties.getProperty(SMS_SERVICE_PARAM_CHECK_DELIVERY);
            if (checkSMSDelivery != null && (checkSMSDelivery.equals("1") || 0 == checkSMSDelivery
                    .compareToIgnoreCase("true")) && isMainNode()) {
                this.clientSmsDeliveryStatusUpdater.start();
            }

            String bkEnabled = (String) getConfigProperties().get("ecafe.autopayment.bk.enabled");

            if(bkEnabled!=null && Boolean.valueOf(bkEnabled) ){
                regularPaymentSubscriptionService = (RegularPaymentSubscriptionService) RuntimeContext.getAppContext()
                        .getBean("bk_regularPaymentSubscriptionService");
            }else{
                regularPaymentSubscriptionService = (RegularPaymentSubscriptionService) RuntimeContext.getAppContext()
                        .getBean("regularPaymentSubscriptionService");
            }

            RuntimeContext.getAppContext().getBean(SummaryCalculationService.class).scheduleSync();

            RuntimeContext.getAppContext().getBean(ImportMigrantsFileService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(ApplicationForFoodProcessingService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(ETPMVService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(ESZMigrantsUpdateService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(BenefitService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(MaintenanceService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(CardBlockUnblockService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(MeshCardNotifyTaskExecutor.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(PreorderCancelNotificationService.class).scheduleSync();
            RuntimeContext.getAppContext().getBean(ArchivedExeptionService.class).scheduleSync();
            //
            if (!isTestRunning()) {
                initWSCrypto();
            }

            usePriceSms = getPropertiesValue("ecafe.processor.sms.usePrice", "true").equals("true");
            useQueueForAllSyncs = getPropertiesValue("ecafe.processor.sync.useQueueForAllSyncs", "false").equals("true");

            runCheckSums();

            try {
                SSLContext ctx = SSLContext.getInstance("TLSv1.2");
                ctx.init(null, null, null);
                SSLContext.setDefault(ctx);
            } catch (Exception e) {
                logger.error("Error in setting tls 1.2 (ignore on java 6): ", e);
            }

        } catch (Exception e) {
            destroy(executorService, scheduler);
            throw e;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Runtime context created.");
        }
    }


    private void runCheckSums() {
        logger.info("Compute checksum started");
        try {
            CheckSumsMessageDigitsService checkSumsMessageDigitsService = new CheckSumsMessageDigitsService();
            String[] md5s = checkSumsMessageDigitsService.getCheckSum();
            checkSumsMessageDigitsService.saveCheckSumToDB(md5s[0], md5s[1]);
        } catch (Exception e) {
            logger.error("Error getting checksum", e);
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

    public Properties getConfigProperties() {
        return configProperties;
    }

    public SchemaVersionInfo getCurrentDBSchemaVersion() {
        return currentSchemaVersionInfo;
    }

    @Transactional
    public void initDB() throws Exception {
        if (isOrgRoomRunning()) {
            try {
                //getAppContext().getBean(RuntimeContext.class).initiateEntityManager();
                getAppContext().getBean(RuntimeContext.class).loadOptionValues();
            } catch (Exception e) {
                logger.error("Failed to init application.", e);
                throw e;
            }
            return;
        }

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

                //Session session = entityManager.unwrap(Session.class);
                //List<Long> ids = Arrays.asList(
                //      76877938L,
                //      78243442L,
                //      79956602L,
                //      73398642L,
                //      72300018L,
                //      68407770L,
                //      75941754L,
                //      68605850L,
                //      73260466L,
                //      71904938L,
                //      69696474L,
                //      76900250L,
                //      82162722L,
                //      76997338L,
                //      2361081787L,
                //      3973791419L,
                //      741371593L,
                //      2335903428L,
                //      1796869828L,
                //      272970932L,
                //      177739572L,
                //      126558275L,
                //      129646482L,
                //      1253182916L,
                //      1590433588L,
                //      962461748L,
                //      1809454900L,
                //      3037597380L,
                //      2524446772L,
                //      1520047924L,
                //      387539139L,
                //      3360030772L,
                //      1160452148L,
                //      477525884L,
                //      3526823620L,
                //      1575947716L,
                //      1209473076L,
                //      116850884L,
                //      126578765L,
                //      4225754920L,
                //      3444572212L,
                //      51058740L,
                //      3826388676L,
                //      2686127812L,
                //      1356142388L,
                //      3812101828L,
                //      737086916L,
                //      2563708724L,
                //      3301054260L,
                //      2788706148L,
                //      2502105140L,
                //      2146113332L,
                //      178263860L,
                //      1157044020L,
                //      2316308164L,
                //      74065866L,
                //      2629134025L
                //);
                //Criteria criteria = session.createCriteria(Card.class);
                //criteria.add(Restrictions.in("cardNo", ids));
                //List list = criteria.list();
                //for (Object obj: list){
                //    Card card = (Card) obj;
                //    session.delete(card);
                //}
                //Criteria criteriat = session.createCriteria(CardTemp.class);
                //criteriat.add(Restrictions.in("cardNo", ids));
                //List list1 = criteriat.list();
                //for (Object obj: list1){
                //    CardTemp card = (CardTemp) obj;
                //    session.delete(card);
                //}
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
                description, currentTime, currentTime, false,false);
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
        Session persistenceSession = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            // Get configuration from CF_Options
            Criteria criteria = persistenceSession.createCriteria(Option.class);
            criteria.add(Restrictions.eq("idOfOption", 1L));
            Option option = (Option) criteria.uniqueResult();
            String optionText = option.getOptionText();
            StringReader stringReader = new StringReader(optionText);
            Properties properties = new Properties();
            properties.load(stringReader);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return properties;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
        if (isOrgRoomRunning()){
            Object threadPoolClass = properties.get("org.quartz.threadPool.class");
            if (threadPoolClass == null || ((String) threadPoolClass).length() < 1) {
                properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
                properties.put("org.quartz.threadPool.threadCount", "4");
                properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            }
        }
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        if (logger.isDebugEnabled()) {
            logger.debug("Application-wide job scheduler created.");
        }
        return scheduler;
    }


    private static Processor createProcessor(SessionFactory sessionFactory,
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

    private static CardManager createCardManagerProcessor(Properties properties, SessionFactory sessionFactory,
            EventNotificator eventNotificator) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating CardManagerProcessor.");
        }
        CardManager processor = new CardManagerProcessor(sessionFactory, eventNotificator);
        if (logger.isDebugEnabled()) {
            logger.debug("CardManagerProcessor created.");
        }
        return processor;
    }

    private static OrderCancelProcessor createOrderCancelProcessor(Properties properties, SessionFactory sessionFactory,
            EventNotificator eventNotificator) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating OrderCancelProcessor.");
        }
        OrderCancelProcessor processor = new OrderCancelProcessorImpl(sessionFactory, eventNotificator);
        if (logger.isDebugEnabled()) {
            logger.debug("OrderCancelProcessor created.");
        }
        return processor;
    }

    private static PaymentProcessor createPaymentProcessor(Properties properties, SessionFactory sessionFactory,
            EventNotificator eventNotificator) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating PaymentProcessor.");
        }
        PaymentProcessor processor = new PaymentProcessorImpl(sessionFactory, eventNotificator);
        if (logger.isDebugEnabled()) {
            logger.debug("PaymentProcessor created.");
        }
        return processor;
    }

    private static ClientPaymentOrderProcessor createClientPaymentOrderProcessor(Properties properties,
            SessionFactory sessionFactory, EventNotificator eventNotificator) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating ClientPaymentOrderProcessor.");
        }
        ClientPaymentOrderProcessor processor = new ClientPaymentOrderProcessorImpl(sessionFactory, eventNotificator);
        if (logger.isDebugEnabled()) {
            logger.debug("ClientPaymentOrderProcessor created.");
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
            logger.warn("IntegRO input files not saved.");
        }
        try {
            intgeroResponseLogPath = restoreFilename(basePath,
                    properties.getProperty(PROCESSOR_PARAM_BASE + ".org.integro.out.log.path"));
        } catch (Exception e) {
            logger.warn("IntegRO output files not saved.");
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

    private IAuthorizeUserBySms createUserCodeSender(Properties properties) {
        IAuthorizeUserBySms service = null;
        if (properties.getProperty("ecafe.processor.userCode.service", "").equals("EMP")) {
            service = applicationContext.getBean(EMPAuthorizeUserBySmsService.class);
        }
        else
        {
            service = applicationContext.getBean(EMPSendSmsToUserService.class);
        }
        return service;
    }

    private static AutoReportGenerator createAutoReportGenerator(String basePath, Properties properties,
            ExecutorService executorService, Scheduler scheduler, SessionFactory sessionFactory,
            AutoReportProcessor autoReportProcessor) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating auto report generator.");
        }
        TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
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
        TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
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
        ContractIdGenerator contractIdGenerator = new ContractIdGenerator(properties, sessionFactory);
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
        } else if(serviceType.equalsIgnoreCase("emp")) {
            smsService = EMPSmsServiceImpl.getInstance(config);
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
        if (configProperties == null) {
            return defaultValue;
        }
        return configProperties.getProperty(name, defaultValue);
    }

    public int getPropertiesValue(String name, int defaultValue) {
        return Integer.parseInt(configProperties.getProperty(name, "" + defaultValue));
    }

    public void loadOptionValues() {
        optionsValues = new HashMap<Integer, String>();
        for (int n = 0; n < Option.OPTIONS_INITIALIZER.length; n += 2) {
            Integer nOption = (Integer) Option.OPTIONS_INITIALIZER[n];
            String v = DAOUtils.getOptionValue(entityManager, nOption, (String) Option.OPTIONS_INITIALIZER[n + 1]);
            optionsValues.put(nOption, v);
        }
        logInfoService = getOptionValueBool(Option.OPTION_LOG_INFOSERVICE);
        methodsInfoService = getOptionValueString(Option.OPTION_METHODS_INFOSERVICE).split(",");
    }

    public boolean actionIsOnByNode(String parameterName) {
        String instance = getNodeName();
        String reqInstance = getConfigProperties().getProperty(parameterName);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public boolean groupActionIsOnByNode(String parameter) {
        String nodes = RuntimeContext.getInstance().getPropertiesValue(parameter, "");
        if (nodes.equals("ALL")) {
            return true;
        } else if (nodes.equals("")) {
            return false;
        }
        String[] strs = nodes.split(",");
        List<String> nodesList = new ArrayList<String>(Arrays.asList(strs));
        if (nodesList.contains(RuntimeContext.getInstance().getNodeName()))
            return true;
        else
            return false;
    }

    public boolean isNSI3() {
        return getOptionValueString(Option.OPTION_NSI_VERSION).equals(Option.NSI3);
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
            if (e.getKey().equals(Option.OPTION_SVERKA_ENABLED)) continue;
            Option o = new Option((long) e.getKey(), e.getValue());
            o = entityManager.merge(o);
            entityManager.persist(o);
        }
    }

    public boolean getCriticalErrors() {
        return criticalErrors;
    }

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
            dataInfo.id = getDNField(dn, "OU");
            dataInfo.location = getDNField(dn, "L");
            dataInfo.issued = cert.getNotBefore();
            dataInfo.validTo = cert.getNotAfter();
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
            else {
                logger.error("Error validating license file: " + file.getAbsolutePath(), e);
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

    public RegularPaymentSubscriptionService getRegularPaymentSubscriptionService() {
        return regularPaymentSubscriptionService;
    }

    public SettingsConfig getSettingsConfig() {
        if (settingsConfig == null){
            settingsConfig = new SettingsConfig();
        }
        return settingsConfig;
    }

    public void setSettingsConfig(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    public static enum RegistryType {
        MSK("msk"),
        SPB("spb");

        private String name;

        RegistryType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static boolean isMsk() {
            return RuntimeContext.getInstance().getRegistryType().equals(MSK);
        }

        public static boolean isSpb() {
            return RuntimeContext.getInstance().getRegistryType().equals(SPB);
        }
    }
}

