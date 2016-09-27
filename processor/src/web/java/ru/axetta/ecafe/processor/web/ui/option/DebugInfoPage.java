/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianNotificationSetting;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.service.SummaryCalculationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Component
@Scope("session")
public class DebugInfoPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(DebugInfoPage.class);

    private Date startDate = new Date(System.currentTimeMillis());
    private Date endDate = new Date(System.currentTimeMillis());
    private String result = "";

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

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

    public void runTest2() throws Exception {
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
        service.run(new Date(1455062400000L), new Date(1455148799000L), ClientNotificationSetting.Predefined.SMS_NOTIFY_SUMMARY_DAY.getValue());
    }

    public void runTestAISReestr() throws Exception {
        OrgMskNSIService service = RuntimeContext.getAppContext().getBean(OrgMskNSIService.class);
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
        }
    }

    @Transactional
    public void runQuickTest() throws Exception {
        //List<Client> list = DAOService.getInstance().findClientsForOrgAndFriendly(57L, false);
        //System.out.println(list.size());
        EntityManager em = entityManager.getEntityManagerFactory().createEntityManager();
        Session session = em.unwrap(Session.class);

        List<Client> clients = DAOService.getInstance().findClientsForOrgAndFriendly(20L, false);
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
                VirtualFile zzz = VFS.getChild(surl.substring(5));
                if (zzz.isFile()) {
                    res += "!" + zzz.getPhysicalFile().getAbsolutePath() + "\n";
                }
                List<VirtualFile> virtualFiles = zzz.getChildrenRecursively();
                for (VirtualFile vf : virtualFiles) {
                    if (vf.isFile()) {
                        res += vf.getPhysicalFile().getAbsolutePath() + "\n";
                    }
                }
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
}
