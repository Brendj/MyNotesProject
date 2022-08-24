/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ApplicationForFoodProcessingService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.service.SummaryCalculationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PublicKey;
import java.util.*;

@Component
@Scope("session")
public class DebugInfoPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(DebugInfoPage.class);

    private Date startDate = new Date(System.currentTimeMillis());
    private Date endDate = new Date(System.currentTimeMillis());
    private String result = "";
    private String messageSocket;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private EventNotificationService eventNotificationService;

    @Override
    public String getPageFilename() {
        return "option/debug_info_page";
    }

    public void runTestRNIP() throws Exception {


        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        try {
            fileInputStream = new FileInputStream("/home/jbosser/processor/Debugs/rnip.txt");
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String s;

            Map<String, String> map = new HashMap<String, String>();
            while((s = bufferedReader.readLine()) != null) {
                if (s.substring(0,1).equals("-")) {
                    mapList.add(map);
                    map = new HashMap<String, String>();
                } else {
                    String[] arr = s.split("=");
                    map.put(arr[0], arr[1]);
                }
            }
            mapList.add(map);
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(fileInputStream);
        }
        if (mapList.size() > 0) {
            RNIPLoadPaymentsService rnip = RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class);
            rnip.addPaymentsToDb(mapList, true);
        }
    }

    private Org findOrg(RuntimeContext runtimeContext, Long idOfOrg) throws Exception {
        PublicKey publicKey;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            // Start data model transaction
            persistenceTransaction = persistenceSession.beginTransaction();
            // Find given org
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            if (null == org) {
                final String message = String.format("Unknown org with IdOfOrg == %s", idOfOrg);
                logger.error(message);
                throw new NullPointerException(message);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return org;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void runTest2() throws Exception {
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        Transaction transaction = session.beginTransaction();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            ApplicationForFood applicationForFood = DAOUtils.findApplicationForFoodByServiceNumber(session, "0001-9300120-100101-00030058/22");
            RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, GuardianshipValidationRequest.class);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        //RuntimeContext.getAppContext().getBean(ETPMVService.class).processIncoming("<ns4:CoordinateMessage xmlns:ns4=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:ns5=\"http://mos.ru/gu/service/100101/\"><ns4:CoordinateDataMessage><ns4:Service><ns4:RegNum>156356534</ns4:RegNum><ns4:RegDate>2022-07-27T17:05:58.000+03:00</ns4:RegDate><ns4:ServiceNumber>0001-9300120-100101-00030058/22</ns4:ServiceNumber><ns4:Responsible><ns4:LastName>оператор Портала</ns4:LastName><ns4:FirstName>оператор Портала</ns4:FirstName><ns4:MiddleName>оператор Портала</ns4:MiddleName><ns4:JobTitle>оператор Портала</ns4:JobTitle><ns4:Phone>+7 (495) 539-55-55</ns4:Phone><ns4:Email>cpgu@mos.ru</ns4:Email></ns4:Responsible><ns4:Department><ns4:Name>Департамент информационных технологий города Москвы</ns4:Name><ns4:Code>2043</ns4:Code><ns4:Inn>7710878000</ns4:Inn><ns4:Ogrn>1107746943347</ns4:Ogrn><ns4:RegDate>2019-09-12T00:00:00</ns4:RegDate><ns4:SystemCode>9300003</ns4:SystemCode></ns4:Department><ns4:CreatedByDepartment><ns4:Name>ПГУ</ns4:Name><ns4:Code>1</ns4:Code><ns4:Inn>7710878000</ns4:Inn><ns4:Ogrn>1107746943347</ns4:Ogrn><ns4:RegDate>2019-09-12T00:00:00</ns4:RegDate><ns4:SystemCode>1</ns4:SystemCode></ns4:CreatedByDepartment><ns4:OutputKind>Portal</ns4:OutputKind><ns4:PortalNum>156178271</ns4:PortalNum></ns4:Service><ns4:SignService Id=\"ead3a0c5-a1c3-48ae-843e-92dda7ffb72e\"><ns4:ServiceType><ns4:Code>100101</ns4:Code><ns4:Name>Подача заявления о предоставлении питания за счет средств бюджета города Москвы</ns4:Name></ns4:ServiceType><ns4:Contacts><ns4:BaseDeclarant xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Id=\"3fe9b3ba-2c33-454b-96c2-4fab0efdc504\" xsi:type=\"ns4:RequestContact\"><ns4:Type>Child</ns4:Type><ns4:Documents><ns4:ServiceDocument><ns4:DocKind><ns4:Code>20017</ns4:Code><ns4:Name>Свидетельство о рождении</ns4:Name></ns4:DocKind><ns4:DocSerie>XX-ОГ</ns4:DocSerie><ns4:DocNumber>354535</ns4:DocNumber><ns4:DocDate>2022-07-14T00:00:00</ns4:DocDate><ns4:WhoSign>УФМС г.Москва</ns4:WhoSign><ns4:ListCount xsi:nil=\"true\"/><ns4:CopyCount xsi:nil=\"true\"/></ns4:ServiceDocument></ns4:Documents><ns4:LastName>Арабова</ns4:LastName><ns4:FirstName>Алсу</ns4:FirstName><ns4:MiddleName>Ильдаровна</ns4:MiddleName><ns4:Gender>Female</ns4:Gender><ns4:BirthDate>2009-02-02</ns4:BirthDate><ns4:Snils>830-838-265 12</ns4:Snils><ns4:CitizenshipType xsi:nil=\"true\"/></ns4:BaseDeclarant><ns4:BaseDeclarant xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Id=\"9cd0f9b9-1add-44dd-86d8-da9770cc8a08\" xsi:type=\"ns4:RequestContact\"><ns4:Type>Declarant</ns4:Type><ns4:Documents><ns4:ServiceDocument><ns4:DocKind><ns4:Code>20001</ns4:Code><ns4:Name>Паспорт гражданина РФ</ns4:Name></ns4:DocKind><ns4:DocSerie>4556</ns4:DocSerie><ns4:DocNumber>445566</ns4:DocNumber><ns4:DocDate>2022-07-08T00:00:00</ns4:DocDate><ns4:WhoSign>УФМС г.Москва</ns4:WhoSign><ns4:ListCount xsi:nil=\"true\"/><ns4:CopyCount xsi:nil=\"true\"/><ns4:DivisionCode>655-656</ns4:DivisionCode></ns4:ServiceDocument></ns4:Documents><ns4:LastName>Тришин</ns4:LastName><ns4:FirstName>Сергей</ns4:FirstName><ns4:MiddleName>Константинович</ns4:MiddleName><ns4:Gender>Male</ns4:Gender><ns4:BirthDate>1968-12-19</ns4:BirthDate><ns4:Snils>514-590-282 79</ns4:Snils><ns4:MobilePhone>5555555555</ns4:MobilePhone><ns4:EMail>atrishin@inform-tb.ru</ns4:EMail><ns4:CitizenshipType xsi:nil=\"true\"/><ns4:SsoId>01f7de71-7c87-4d2f-a570-41807710e437</ns4:SsoId></ns4:BaseDeclarant></ns4:Contacts><ns4:CustomAttributes><ns5:ServiceProperties><ns5:IsLegalRepresentative>true</ns5:IsLegalRepresentative><ns5:Validity>false</ns5:Validity><ns5:ValidationGuardianship>false</ns5:ValidationGuardianship><ns5:EduName>ГБПОУ ТК № 21</ns5:EduName><ns5:IDLink>573e5d7f-8af8-4b68-96ea-966fb5e97b99</ns5:IDLink><ns5:PreferentialCategory><ns5:LargeFamily>true</ns5:LargeFamily><ns5:LowIncomeFamily>true</ns5:LowIncomeFamily><ns5:WithoutParentalCare>true</ns5:WithoutParentalCare></ns5:PreferentialCategory></ns5:ServiceProperties></ns4:CustomAttributes></ns4:SignService></ns4:CoordinateDataMessage></ns4:CoordinateMessage>");

        /*ApplicationForFoodProcessingService service = RuntimeContext.getAppContext().getBean(ApplicationForFoodProcessingService.class);
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        Transaction transaction = session.beginTransaction();
        logger.info(CalendarUtils.dateTimeToString(service.getTriggerDateByProductionCalendar(session, new Date(), 3)));
        transaction.rollback();
        session.close();*/
        /*RegularPaymentSubscriptionService notificationService = RuntimeContext.getInstance()
                .getRegularPaymentSubscriptionService();
        notificationService.checkClientBalances();*/
        /*for (Long w = 1499868946299L; w < 1499868946399L; w++) {
            DAOService.getInstance().registerSyncRequest(5, w.toString());
            Thread.sleep(500);
        }*/

        //RuntimeContext.getAppContext().getBean(OrgSyncLockService.class).clean();

        /*for (long i = 20; i <=300; i++) {
            try {
                Org org = findOrg(RuntimeContext.getInstance(), i);
                //Org org = DAOReadonlyService.getInstance().findOrg(i);

                Thread thread = new Thread(){
                    private Org org;
                    public void run(){
                        try {
                            List<AccountTransactionExtended> list = DAOReadonlyService.getInstance()
                                    .getAccountTransactionsForOrgSinceTimeV2(org, new Date(1401617600000L), new Date(1402617660000L));
                            System.out.println(Thread.currentThread().getId() + "---" + list.size());
                        }catch (Exception e) {
                            System.out.println("Error");
                        }
                    }
                    public Thread setOrg(Org org) {
                        this.org = org;
                        return this;
                    }
                }.setOrg(org);
                thread.start();
            } catch (Exception e) {
                continue;
            }
        }*/

        /*eventNotificationService.sendEmailAsync("ww",
                EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, new String[1]);*/

        /*SummaryCardsMSRService service = RuntimeContext.getAppContext().getBean(SummaryCardsMSRService.class);
        Date endDate = CalendarUtils.endOfDay(new Date());
        Date startDate = CalendarUtils.truncateToDayOfMonth(new Date());
        service.run(startDate, endDate);*/

        /*ProcessorUtils utils = RuntimeContext.getAppContext().getBean(ProcessorUtils.class);
        SessionFactory factory = ((Session)entityManager.getDelegate()).getSessionFactory();
        for (Long org = 3L; org < 900; org++) {
            try {
                utils.saveLastProcessSectionCustomDate(factory, org, SectionType.ACC_INC_REGISTRY);
            } catch (Exception e) {

            }
        }*/

        /*SyncLogger syncLogger = RuntimeContext.getInstance().getSyncLogger();
        for (Long org = 3L; org < 9; org++) {
            syncLogger.registerSyncRequestInDb(org, "15");
        }*/

        /*Session session = RuntimeContext.getInstance().createPersistenceSession();
        List<Long> list = DAOUtils.findFriendlyOrgIds(session, 16L);
        logger.info(list.toString());*/

        /*EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
        processor.runReceiveUpdates();*/

        /*ClientMskNSIService nsiService = RuntimeContext.getAppContext().getBean(ClientMskNSIService.class);
        MskNSIService.SearchPredicateInfo searchPredicateInfo = new MskNSIService.SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Вид представителя");
        List<Item> queryResults = nsiService.executeQuery(searchPredicateInfo, 1);
        System.out.println(queryResults.size());*/

        //CardService cardService = CardService.getInstance();
        //System.out.println(DAOService.getInstance().runDebugTest2());
        //RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsServiceV116.class).executeExportCatalog(DAOService.getInstance().getContragentById(16L), null, null);
        /*SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        service.run(getStartDate(), getEndDate(),
                ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue());*/

        /*RuntimeContext runtimeContext = RuntimeContext.getInstance();

        Scheduler scheduler = runtimeContext.getAutoReportGenerator().getScheduler();
        for (String groupName : scheduler.getJobGroupNames()) {

            //loop all jobs by groupname
            for (String jobName : scheduler.getJobNames(groupName)) {

                //get job's trigger
                Trigger[] triggers = scheduler.getTriggersOfJob(jobName,groupName);
                Date nextFireTime = triggers[0].getNextFireTime();

                System.out.println("[jobName] : " + jobName + " [groupName] : "
                        + groupName + " - " + nextFireTime);

            }
        }*/
    }

    private void testNewEMPFuncs() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        service.run(new Date(1455062400000L), new Date(1455148799000L),
                ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue(), false);
    }

    public void runTestAISReestr() throws Exception {
        /*OrgMskNSIService service = RuntimeContext.getAppContext().getBean(OrgMskNSIService.class);
        Set<String> guids = new HashSet<String>();
        guids.add("AD1CA6A0-7390-4270-8F76-ED31C1E52A2D");
        guids.add("9290FB1E-5512-41D3-A447-8A865D0E8215");
        guids.add("D88D3D17-16C6-497E-93CB-68DD209C937C");
        guids.add("D88D3D17-16C6-497E-93CB-68DD209C927C");
        guids.add("980D134B-0F61-418F-B09F-2502A532CC12");
        List<String> list = service.getBadGuids(guids);
        if (list != null) {
            for (String s : list) {
                System.out.println(String.format("Гуид %s не найден в АИС Реестр", s));
            }
        }  */
    }

    @Transactional
    public void runQuickTest() throws Exception {
        //List<Client> list = DAOService.getInstance().findClientsForOrgAndFriendly(57L, false);
        //System.out.println(list.size());
        EntityManager em = entityManager.getEntityManagerFactory().createEntityManager();
        Session session = em.unwrap(Session.class);

        List<Client> clients = DAOReadonlyService.getInstance().findClientsForOrgAndFriendly(20L, false);
        for (Client cc : clients) {
            //Client client = (Client)session.load(Client.class, cc.getIdOfClient());
            /*Set<ClientNotificationSetting> settings = client.getNotificationSettings();
            List<NotificationSettingItem> notificationSettings = new ArrayList<NotificationSettingItem>();
            for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
                if (predefined.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    continue;
                }
                notificationSettings.add(new NotificationSettingItem(ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY, settings));
            }
            client.getNotificationSettings().add(new ClientNotificationSetting(client, ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue()));*/
            ClientNotificationSetting setting = new ClientNotificationSetting(cc, ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue());
            session.save(setting);
            ClientNotificationSetting setting2 = new ClientNotificationSetting(cc, ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue());
            session.save(setting2);
        }
    }

    public void runEmpSummaryDay() throws Exception {
        SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        List<SummaryCalculationService.ClientEE> list = service.generateNotificationParams(getStartDate(), getEndDate(),
                ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue());
        String res = "";
        for (SummaryCalculationService.ClientEE client : list) {
            res += String.format("Ид клиента=%s\n", client.getIdOfClient());
            for (int i = 0; i < client.getValues().length-1; i=i+2) {
                res += client.getValues()[i] + " | " + client.getValues()[i+1] + "\n";
            }
            res += "\n";
        }
        result = res;
    }

    public void runEmpSummaryWeek() throws Exception {
        SummaryCalculationService service = RuntimeContext.getAppContext().getBean(SummaryCalculationService.class);
        List<SummaryCalculationService.ClientEE> list = service.generateNotificationParams(getStartDate(), getEndDate(),
                ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_WEEK.getValue());
        String res = "";
        for (SummaryCalculationService.ClientEE client : list) {
            res += String.format("Ид клиента=%s\n", client.getIdOfClient());
            for (int i = 0; i < client.getValues().length-1; i=i+2) {
                res += client.getValues()[i] + " | " + client.getValues()[i+1] + "\n";
            }
            res += "\n";
        }
        result = res;
    }

    public void runVFSCollapse() throws Exception {
        System.out.println("ss");
        //urls = Thread.currentThread().getContextClassLoader().getResources("config.properties");
        Enumeration<URL> en = getClass().getClassLoader().getResources(".");
        String res = "";
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            String surl = url.toString();
            res += surl + "\n";
            if (surl.startsWith("vfs:/") && surl.endsWith("/WEB-INF/classes/")) {
                /*VirtualFile zzz = VFS.getChild(surl.substring(5));
                if (zzz.isFile()) {
                    res += "!" + zzz.getPhysicalFile().getAbsolutePath() + "\n";
                }
                List<VirtualFile> virtualFiles = zzz.getChildrenRecursively();
                for (VirtualFile vf : virtualFiles) {
                    if (vf.isFile()) {
                        res += vf.getPhysicalFile().getAbsolutePath() + "\n";
                    }
                }*/
            }
        }
        result = res;
    }

    public Date getStartDate() {
        //Calendar calendar = new GregorianCalendar();
        //return CalendarUtils.calculateTodayStart(calendar, startDate);
        return CalendarUtils.truncateToDayOfMonth(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return CalendarUtils.endOfDay(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessageSocket() {
        return messageSocket;
    }

    public void setMessageSocket(String messageSocket) {
        this.messageSocket = messageSocket;
    }
}
