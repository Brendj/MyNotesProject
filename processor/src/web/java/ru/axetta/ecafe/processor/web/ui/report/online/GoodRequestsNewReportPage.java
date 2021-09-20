/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.UserOrgs;
import ru.axetta.ecafe.processor.core.persistence.UserReportSetting;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.GoodRequestsNewReport;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.converter.OrgRequestFilterConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestsNewReportPage extends OnlineReportWithContragentPage {

    private final static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportPage.class);
    private final static String generateBeginDateKey = "goodRequestsReport.generateBeginDate";
    //private final static String generateEndDateKey = "goodRequestsReport.generateEndDate";

    private String htmlReport = null;
    private Date generateBeginDate = new Date();
    private Date generateEndDate = new Date();
    private Boolean hideMissedColumns = true;
    private Boolean hideDailySamplesCount = false;
    private Boolean applyUserSettings = false;
    private Boolean hideGeneratePeriod = false;
    private Boolean hideLastValue = false;
    private String nameFiler;
    private OrgRequestFilterConverter orgRequest = new OrgRequestFilterConverter();
    private User currentUser;
    //private String lastGoodRequestUpdateDateTiem;
    private Date lastGoodRequestUpdateDateTime;
    private List<SelectItem> preorderTypeItems = readAllPreordersPresenceItems();
    private PreordersPresenceTypeEnum preorderType = PreordersPresenceTypeEnum.WITH_PREORDERS;

    public enum PreordersPresenceTypeEnum {
        WITH_PREORDERS("Включить предзаказы"),
        WITHOUT_PREORDERS("Исключить предзаказы"),
        ONLY_PREORDERS("Только предзаказы");

        private final String description;

        private PreordersPresenceTypeEnum(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public void applyOfOrgList(ActionEvent ae) {
        if (applyUserSettings) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                try {
                    persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    //persistenceSession.refresh(currentUser);
                    User user = (User) persistenceSession.get(User.class, currentUser.getIdOfUser());

                    idOfOrgList = new ArrayList<Long>();
                    if (user.getUserOrgses().isEmpty()){
                        filter = "Не выбрано";
                        printMessage("Список организаций рассылки не заполнен");
                        idOfOrgList = null;

                    }
                    else {
                        filter = "";
                        for(UserOrgs userOrgs: user.getUserOrgses()) {
                            idOfOrgList.add(userOrgs.getOrg().getIdOfOrg());
                            filter = filter.concat(userOrgs.getOrg().getShortName() + "; ");
                        }
                        filter = filter.substring(0, filter.length() - 1);
                    }
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        } else {
            filter = "Не выбрано";
        }
    }

    // Транзакционный метод
    @Override
    public void fill(Session persistenceSession, User currentUser) throws Exception {
        this.currentUser = currentUser;
        generateBeginDate = new Date();
        generateEndDate = new Date();
        Properties properties = DAOUtils.extractPropertiesByUserReportSetting(persistenceSession, currentUser,
                UserReportSetting.GOOD_REQUEST_REPORT);
        if(!properties.isEmpty()){
            Date dateTime = new Date();
            String generateBeginDateStr = properties.getProperty(generateBeginDateKey,
                    CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateTime));
            generateBeginDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(generateBeginDateStr);
            //String generateEndDateStr = properties.getProperty(generateEndDateKey, CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateTime));
            //generateBeginDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(generateEndDateStr);
            generateEndDate = new Date();
        }
        //periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
        //orgRequest = new OrgRequestFilterConverter();
        htmlReport = null;
    }

    public GoodRequestsNewReportPage() {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public void onGeneratePeriodChanged(ActionEvent event) {
        generateEndDate = new Date(generateBeginDate.getTime()+60*60*1000);
    }

    public void onEndDateSpecified(ActionEvent event) {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noOfDays=(int)(diff/(24*60*60*1000));
            switch (noOfDays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    @Override
    public String getContragentStringIdOfOrgList() {
        return idOfContragentOrgList.toString().replaceAll("[^0-9,]", "");
    }

    public Object reportHTMLSendEmail() {
        if (validateFormData())  return null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_notify.jasper");
        //String templateFilename = checkIsExistFile("_summary.jasper");

        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                report =  builder.build(persistenceSession, startDate, endDate, localCalendar);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }

        if (report != null) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter1 = new JRHtmlExporter();
                exporter1.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                exporter1.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter1.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter1.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter1.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter1.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                //exporter1.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporter1.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter1.exportReport();
                String[] values = {"address", "Адрес орга", "shortOrgName", "vjz jhuf", "reportValues", os.toString("UTF-8")};
                EventNotificationService eventNotificationService = RuntimeContext.getAppContext().getBean(EventNotificationService.class);
                //eventNotificationService.sendEmailAsync("kadyrov@axetta.ru",
                //        EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
                //eventNotificationService.sendEmailAsync("dizzarg@mail.ru",
                //        EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, values);
            } catch (Exception e) {
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    public Object buildReportHTML() {
        if (validateFormData())  return null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_allmenus.jasper");
        //String templateFilename = checkIsExistFile("_summary.jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                report =  builder.build(persistenceSession, startDate, endDate, localCalendar);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }

        if (report != null) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
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
                if(hideGeneratePeriod){
                    try {
                        // идет запись
                        persistenceSession = runtimeContext.createPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        Properties properties = new Properties();
                        String endGenerateDateStr = CalendarUtils.toStringFullDateTimeWithLocalTimeZone(generateEndDate);
                        properties.setProperty(generateBeginDateKey, endGenerateDateStr);
                        DAOUtils.saveReportSettings(persistenceSession, currentUser, UserReportSetting.GOOD_REQUEST_REPORT,
                                properties);
                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    } finally {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
                    }
                }
            } catch (Exception e) {
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = GoodRequestsNewReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private boolean validateFormData() {
        if(CollectionUtils.isEmpty(idOfOrgList) && CollectionUtils.isEmpty(idOfContragentOrgList)){
            printError("Выберите список организаций или поставщиков");
            return true;
        }
        if(startDate==null){
            printError("Не указано дата выборки от");
            return true;
        }
        if(endDate==null){
            printError("Не указано дата выборки до");
            return true;
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
            return true;
        }
        if(hideGeneratePeriod){
            if(generateBeginDate==null){
                printError("Не указано время генерации от");
                return true;
            }
            if(generateEndDate==null){
                printError("Не указано время генерации до");
                return true;
            }
            if(generateBeginDate.after(generateEndDate)){
                printError("Время генерации от меньше время генерации до");
                return true;
            }
        }
        return false;
    }

    public void exportToXLS(ActionEvent actionEvent){
        if (validateFormData()) return;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_export.jasper");
        if (StringUtils.isEmpty(templateFilename)) return ;
        Date generateTime = new Date();
        GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            report =  builder.build(persistenceSession, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        if(report!=null){
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(generateTime, report);
                response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
                if(hideGeneratePeriod){
                    try {
                        // идет запись
                        persistenceSession = runtimeContext.createPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        Properties properties = new Properties();
                        String endGenerateDateStr = CalendarUtils.toStringFullDateTimeWithLocalTimeZone(generateEndDate);
                        properties.setProperty(generateBeginDateKey, endGenerateDateStr);
                        DAOUtils.saveReportSettings(persistenceSession, currentUser, UserReportSetting.GOOD_REQUEST_REPORT,
                                properties);
                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    } finally {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
                    }
                }
                printMessage("Сводный отчет по заявкам построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }

    public Object clear(){
        idOfOrg=null;
        filter=null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        htmlReport = null;
        return null;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String sourceMenuOrgId = StringUtils.join(idOfContragentOrgList.iterator(), ",");
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG, sourceMenuOrgId);
        String idOfOrgString = "";
        if(idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        properties.setProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, Boolean.toString(hideGeneratePeriod));
        if(hideGeneratePeriod){
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, Long.toString(generateBeginDate.getTime()));
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(generateEndDate.getTime()));
        } else {
            // ставит текущее значение оно все равно не участвет в выборке и формировании отчета
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, Long.toString(System.currentTimeMillis()));
            properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(System.currentTimeMillis()));
        }
        properties.setProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(hideMissedColumns));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, Boolean.toString(hideDailySamplesCount));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_LAST_VALUE, Boolean.toString(hideLastValue));
        properties.setProperty(GoodRequestsNewReport.P_NAME_FILTER, nameFiler);
        properties.setProperty(GoodRequestsNewReport.P_ORG_REQUEST_FILTER, Integer.toString(orgRequest.getOrgRequestFilterEnum().ordinal()));
        properties.setProperty(GoodRequestsNewReport.P_NEED_FULL_GOOD_NAMES, Boolean.toString(false));

        boolean hidePreorders = false, preordersOnly = false;
        switch (preorderType) {
            case ONLY_PREORDERS: preordersOnly = true; hidePreorders = false; break;
            case WITH_PREORDERS: preordersOnly = false; hidePreorders = false; break;
            case WITHOUT_PREORDERS: preordersOnly = false; hidePreorders = true; break;
            default: break;
        }

        properties.setProperty(GoodRequestsNewReport.P_HIDE_PREORDERS, Boolean.toString(hidePreorders));
        properties.setProperty(GoodRequestsNewReport.P_PREORDERS_ONLY, Boolean.toString(preordersOnly));
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "GoodRequestsReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/good_requests_new_report";
    }

    /* Getter and Setters */
    public OrgRequestFilterConverter getOrgRequest() {
        return orgRequest;
    }

    public Boolean getHideDailySamplesCount() {
        return hideDailySamplesCount;
    }

    public Date getLastGoodRequestUpdateDateTime() {
        return lastGoodRequestUpdateDateTime;
    }

    public void setHideDailySamplesCount(Boolean hideDailySamplesCount) {
        this.hideDailySamplesCount = hideDailySamplesCount;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public Date getGenerateBeginDate() {
        return generateBeginDate;
    }

    public void setGenerateBeginDate(Date generateBeginDate) {
        this.generateBeginDate = generateBeginDate;
    }

    public Date getGenerateEndDate() {
        return generateEndDate;
    }

    public void setGenerateEndDate(Date generateEndDate) {
        this.generateEndDate = generateEndDate;
    }

    public Boolean getHideMissedColumns() {
        return hideMissedColumns;
    }

    public void setHideMissedColumns(Boolean hideMissedColumns) {
        this.hideMissedColumns = hideMissedColumns;
    }

    public void setNameFiler(String nameFiler) {
        this.nameFiler = nameFiler;
    }

    public String getNameFiler() {
        return nameFiler;
    }

    public Boolean getApplyUserSettings() {
        return applyUserSettings;
    }

    public void setApplyUserSettings(Boolean applyUserSettings) {
        this.applyUserSettings = applyUserSettings;
    }

    public Boolean getHideGeneratePeriod() {
        return hideGeneratePeriod;
    }

    public void setHideGeneratePeriod(Boolean hideGeneratePeriod) {
        this.hideGeneratePeriod = hideGeneratePeriod;
    }

    public Boolean getHideLastValue() {
        return hideLastValue;
    }

    public void setHideLastValue(Boolean hideLastValue) {
        this.hideLastValue = hideLastValue;
    }

    public List<SelectItem> getPreorderTypeItems() {
        return preorderTypeItems;
    }

    public void setPreorderTypeItems(List<SelectItem> preorderTypeItems) {
        this.preorderTypeItems = preorderTypeItems;
    }

    public PreordersPresenceTypeEnum getPreorderType() {
        return preorderType;
    }

    public void setPreorderType(PreordersPresenceTypeEnum preorderType) {
        this.preorderType = preorderType;
    }

    private static List<SelectItem> readAllPreordersPresenceItems() {
        PreordersPresenceTypeEnum[] preordersPresenceTypeEnums = PreordersPresenceTypeEnum.values();
        List<SelectItem> items = new ArrayList<SelectItem>(preordersPresenceTypeEnums.length);
        for (PreordersPresenceTypeEnum type : preordersPresenceTypeEnums) {
            items.add(new SelectItem(type, type.toString()));
        }
        return items;
    }
}
