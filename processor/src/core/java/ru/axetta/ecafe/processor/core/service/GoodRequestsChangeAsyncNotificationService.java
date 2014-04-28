/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;


import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.UserOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.GoodRequestsNewReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodRequestsChangeAsyncNotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodRequestsChangeAsyncNotificationService.class);

    private Map<Long, ContragentItem> contragentItems;
    private Map<Long, OrgItem> orgItems = new HashMap<Long, OrgItem>();
    private RuntimeContext runtimeContext;
    private int maxNumDays;
    private boolean enableNotify;
    private boolean isHideMissedCol;

    public static GoodRequestsChangeAsyncNotificationService getInstance() {
        return RuntimeContext.getAppContext().getBean(GoodRequestsChangeAsyncNotificationService.class);
    }

    @PostConstruct
    public void init(){
        runtimeContext = RuntimeContext.getInstance();
        maxNumDays = runtimeContext.getOptionValueInt(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        enableNotify =runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE);
        isHideMissedCol = runtimeContext.getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);
        refreshAllInformation();
    }

    @Async
    public void notifyOrg(final Long idOfOrg, final Date beginGenerateTime, final Date endGenerateTime) {
        //if (!runtimeContext.isMainNode()) {
        //    return;
        //}
        if(!enableNotify) return;
        if(orgItems.containsKey(idOfOrg)){
            Calendar localCalendar = runtimeContext.getDefaultLocalCalendar(null);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            CalendarUtils.truncateToDayOfMonth(localCalendar);
            Date startDate = localCalendar.getTime();
            localCalendar.add(Calendar.DATE, maxNumDays);
            localCalendar.add(Calendar.MILLISECOND, -1);
            Date endDate = localCalendar.getTime();
            /* проверим есть ли измененые заявки на неделю */
            boolean isEmptyRequests = true;
            try {
                try {
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    Criteria requestCriteria = persistenceSession.createCriteria(GoodRequestPosition.class);
                    requestCriteria.createAlias("goodRequest", "gr");
                    requestCriteria.add(Restrictions.between("gr.doneDate", startDate, endDate));
                    requestCriteria.add(Restrictions.eq("gr.orgOwner", idOfOrg));
                    requestCriteria.add(Restrictions.isNotNull("good"));
                    Disjunction dateDisjunction = Restrictions.disjunction();
                    dateDisjunction.add(Restrictions.between("createdDate", beginGenerateTime, endGenerateTime));
                    dateDisjunction.add(Restrictions.between("lastUpdate", beginGenerateTime, endGenerateTime));
                    requestCriteria.add(dateDisjunction);
                    List goodRequestPositionList = requestCriteria.list();
                    isEmptyRequests = goodRequestPositionList.isEmpty();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, LOGGER);
                    HibernateUtils.close(persistenceSession, LOGGER);
                }
            } catch (Exception e) {
                LOGGER.error("Failed export report : ", e);
            }
            /* если заявок на данный период нет ничего не делаем */
            if(isEmptyRequests) return;

            OrgItem item = orgItems.get(idOfOrg);
            AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
            String templateShortFileName = GoodRequestsNewReport.class.getSimpleName() + "_notify.jasper";
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
            GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
            Properties properties = new Properties();
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, Long.toString(item.getIdOfOrg()));
            properties.setProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, Boolean.toString(true));
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, Long.toString(beginGenerateTime.getTime()));
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(endGenerateTime.getTime()));
            properties.setProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(isHideMissedCol));
            properties.setProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, Boolean.toString(false));
            properties.setProperty(GoodRequestsNewReport.P_HIDE_LAST_VALUE, Boolean.toString(false));
            properties.setProperty(GoodRequestsNewReport.P_HIDE_TOTAL_ROW, Boolean.toString(true));
            builder.setReportProperties(properties);
            BasicReportJob reportJob = null;
            /* создаем отчет */
            String htmlReport="";
            try {
                try {
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    reportJob = builder.build(persistenceSession, startDate, endDate, localCalendar);
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }  finally {
                    HibernateUtils.rollback(persistenceTransaction, LOGGER);
                    HibernateUtils.close(persistenceSession, LOGGER);
                }
            } catch (Exception e) {
                LOGGER.error("Failed export report : ", e);
            }

            String emailSubject = String.format("Уведомление об изменении заявки \"%s\" - \"%s\"",item.getShortName(), item.getAddress());
            if (reportJob != null) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    JRHtmlExporter exporter = new JRHtmlExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportJob.getPrint());
                    exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                    exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                    exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                    exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                    exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                    exporter.exportReport();
                    htmlReport = os.toString("UTF-8");
                    os.close();
                } catch (Exception e) {
                    LOGGER.error("Failed build report ",e);
                }
            }
            //boolean sended = false;
            if(StringUtils.isNotEmpty(htmlReport)){
                //try {
                //    String fileName = getClass().getSimpleName() + "-" + System.currentTimeMillis() + ".html";
                //    File file = new File(fileName);
                //    FileOutputStream outputStream = new FileOutputStream(file);
                //    outputStream.write(htmlReport.getBytes());
                //    outputStream.flush();
                //    outputStream.close();
                //    LOGGER.info(String.format("save report file '%s'", fileName));
                //} catch (Exception e){
                //    LOGGER.error("Cannot save report file", e);
                //}
                //String[] values = {"address", item.address, "shortOrgName", item.shortName, "reportValues", htmlReport};
                List<String> strings = Arrays
                        .asList(StringUtils.split(item.getDefaultSupplier().requestNotifyMailList, ";"));
                List<String> addresses = new ArrayList<String>(strings);

                /* Закладываем почтовые ящики ответсвенных по питанию в школе если таковые имеются */
                try {
                    try {
                        persistenceSession = runtimeContext.createReportPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        addEmailFromClient(persistenceSession, idOfOrg, addresses);
                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    }  finally {
                        HibernateUtils.rollback(persistenceTransaction, LOGGER);
                        HibernateUtils.close(persistenceSession, LOGGER);
                    }
                } catch (Exception e) {
                    LOGGER.error("Find email from clients : ", e);
                }

                try {
                    try {
                        persistenceSession = runtimeContext.createReportPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        addEmailFromUser(persistenceSession, idOfOrg, addresses);
                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    }  finally {
                        HibernateUtils.rollback(persistenceTransaction, LOGGER);
                        HibernateUtils.close(persistenceSession, LOGGER);
                    }
                } catch (Exception e) {
                    LOGGER.error("Find email from user : ", e);
                }

                LOGGER.debug("addresses "+ ((ArrayList<String>) addresses).toString());
                //boolean sended = false;
                for (String address : addresses) {
                    if (StringUtils.trimToNull(address) != null) {
                         //sended |= eventNotificationService.sendEmail(address, EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                        try {
                            runtimeContext.getPostman().postNotificationEmail(address, emailSubject, htmlReport);
                        } catch (Exception e) {
                            LOGGER.error("Failed to post event", e);
                        }
                    }
                }
            }
        }
    }

    public void refreshAllInformation() {
        updateContragentItems();
        updateOrgItems();
    }

    public void updateContragentItems(){
        contragentItems = getInstance().findContragentItems();
    }

    public void updateContragentItem(Session session, Contragent contragent) throws Exception{
        final Long idOfContragent = contragent.getIdOfContragent();
        ContragentItem item = contragentItems.get(idOfContragent);
        if(item ==null){
            item = new ContragentItem(idOfContragent, contragent.getRequestNotifyMailList());
            contragentItems.put(idOfContragent, item);
        } else {
            item.setRequestNotifyMailList(contragent.getRequestNotifyMailList());
        }
    }

    public void updateOrgItems(){
        orgItems = getInstance().findOrgItems();
    }

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;


    public BasicReportJob buildReport(Session persistenceSession, GoodRequestsNewReport.Builder builder, Date startDate, Date endDate, Calendar localCalendar){
        BasicReportJob reportJob = null;
        try {
            reportJob = builder.build(persistenceSession, startDate, endDate, localCalendar);
        } catch (Exception e) {
            LOGGER.error("Failed export report : ", e);
        }
        return reportJob;
    }

    public void addEmailFromClient(Session persistenceSession, Long idOfOrg, List<String> addresses){
        DetachedCriteria staffClientQuery = DetachedCriteria.forClass(Staff.class);
        staffClientQuery.add(Restrictions.eq("orgOwner", idOfOrg));
        staffClientQuery.add(Restrictions.eq("idOfRole", 0L));
        staffClientQuery.setProjection(Property.forName("idOfClient"));
        Criteria subCriteria = staffClientQuery.getExecutableCriteria(persistenceSession);
        Integer countResult = subCriteria.list().size();
        if (countResult > 0) {
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Property.forName("idOfClient").in(staffClientQuery));
            clientCriteria.setProjection(Property.forName("email"));
            List<String> address = clientCriteria.list();
            addresses.addAll(address);
        }
    }

    public void addEmailFromUser(Session persistenceSession, Long idOfOrg, List<String> addresses){
        Criteria criteria = persistenceSession.createCriteria(UserOrgs.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        List list = criteria.list();
        for (Object o: list){
            UserOrgs userOrgs = (UserOrgs) o;
            if(userOrgs.getUser()!=null && StringUtils.isNotEmpty(userOrgs.getUser().getEmail())){
                List<String> strings = Arrays.asList(StringUtils.split(userOrgs.getUser().getEmail(), ";"));
                addresses.addAll(strings);
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, ContragentItem> findContragentItems(){
        Map<Long, ContragentItem> items = new HashMap<Long, ContragentItem>();
        Query query = entityManager.createQuery("select c.idOfContragent, c.requestNotifyMailList from Contragent c where c.classId=2");
        List res = query.getResultList();
        for (Object obj: res){
            Object[] row = (Object[]) obj;
            String requestNotifyMailList = row[1]==null?"":row[1].toString();
            Long idOfContragent = Long.valueOf(row[0].toString());
            if(!items.containsKey(idOfContragent)) {
                items.put(idOfContragent, new ContragentItem(idOfContragent, requestNotifyMailList));
            }
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Map<Long, OrgItem> findOrgItems(){
        Map<Long, OrgItem> items = new HashMap<Long, OrgItem>();
        Query query = entityManager.createQuery("select o.idOfOrg, o.shortName, o.officialName, o.defaultSupplier.id, o.address from Org o");
        List res = query.getResultList();
        for (Object obj: res){
            Object[] row = (Object[]) obj;
            Long idOfOrg = Long.valueOf(row[0].toString());
            if(!items.containsKey(idOfOrg)){
                Long idOfContragent = Long.valueOf(row[3].toString());
                if(contragentItems.containsKey(idOfContragent)){
                    ContragentItem contragentItem = contragentItems.get(idOfContragent);
                    items.put(idOfOrg, new OrgItem(idOfOrg,row[1].toString(),row[2].toString(), contragentItem, row[4].toString()));
                }
            }

        }
        return items;
    }

    protected static class OrgItem {
        private long idOfOrg;
        private String shortName;
        private String officialName;
        private ContragentItem defaultSupplier;
        private String address;

        public OrgItem(long idOfOrg,  String shortName, String officialName, ContragentItem defaultSupplier, String address) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.officialName = officialName;
            this.defaultSupplier = defaultSupplier;
            this.address = address;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public ContragentItem getDefaultSupplier() {
            return defaultSupplier;
        }

        public void setDefaultSupplier(ContragentItem defaultSupplier) {
            this.defaultSupplier = defaultSupplier;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    protected static class ContragentItem {
        private long idOfContragent;
        private String requestNotifyMailList;

        public ContragentItem(long idOfContragent, String requestNotifyMailList) {
            this.idOfContragent = idOfContragent;
            this.requestNotifyMailList = requestNotifyMailList;
        }

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public void setIdOfContragent(long idOfContragent) {
            this.idOfContragent = idOfContragent;
        }

        public String getRequestNotifyMailList() {
            return requestNotifyMailList;
        }

        public void setRequestNotifyMailList(String requestNotifyMailList) {
            this.requestNotifyMailList = requestNotifyMailList;
        }
    }

}
