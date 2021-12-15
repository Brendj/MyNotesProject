/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.UserOrgs;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.RequestsAndOrdersReport;
import ru.axetta.ecafe.processor.core.report.model.requestsandorders.FeedingPlanType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 29.10.14
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
public class RequestsAndOrdersReportPage extends OnlineReportWithContragentPage {

    private final static Logger logger = LoggerFactory.getLogger(RequestsAndOrdersReportPage.class);
    private String htmlReport = null;
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
    private Boolean applyUserSettings = false; 
    private Boolean hideMissedColumns = true;
    private Boolean showOnlyDivergence = false;
    private Boolean useColorAccent = false;
    private String feedingPlanType = "Все";
    private Boolean noNullReport = false;
    private User currentUser;

    public RequestsAndOrdersReportPage() {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public void applyOfOrgList() {
        if (applyUserSettings) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                try {
                    persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    User user = (User) persistenceSession.get(User.class, currentUser.getIdOfUser());

                    idOfOrgList = new ArrayList<Long>();
                    if (user.getUserOrgses().isEmpty()) {
                        filter = "Не выбрано";
                        printMessage("Список организаций рассылки не заполнен");
                        idOfOrgList = null;
                    } else {
                        filter = "";
                        for (UserOrgs userOrgs : user.getUserOrgses()) {
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

    public void onReportPeriodChanged() {
        htmlReport = null;
        switch (periodTypeMenu.getPeriodType()) {
            case ONE_DAY: {
                setEndDate(startDate);
            }
            break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            }
            break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            }
            break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            }
            break;
        }
    }

    public void onEndDateSpecified() {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if (CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)) {
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff = end.getTime() - startDate.getTime();
            int noOfDays = (int) (diff / (24 * 60 * 60 * 1000));
            switch (noOfDays) {
                case 0:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
                    break;
                case 6:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
                    break;
                case 13:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK);
                    break;
                default:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
                    break;
            }
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    public void onHideMissedColumnsChange(ActionEvent event) {
        htmlReport = null;
        if (!hideMissedColumns) {
            showOnlyDivergence = false;
        }
    }

    public void onUseColorAccentChange(ActionEvent event) {
        htmlReport = null;
    }

    public void onShowOnlyDivergenceChange(ActionEvent event) {
        htmlReport = null;
        if (showOnlyDivergence) {
            hideMissedColumns = true;
        }
    }

    public void onFeedingPlanTypeChange(ActionEvent event) {
        htmlReport = null;
    }

    public void onNoNullReportChange(ActionEvent event) {
        htmlReport = null;
    }

    @Override
    public String getContragentStringIdOfOrgList() {
        return idOfContragentOrgList.toString().replaceAll("[^0-9,]", "");
    }

    public Object reportHTMLSendEmail() {
        if (invalidFormData()) {
            return null;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_notify.jasper");

        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        RequestsAndOrdersReport.Builder builder = new RequestsAndOrdersReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                report = builder.build(persistenceSession, startDate, endDate, localCalendar);
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
                exporter1.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter1.exportReport();
                String[] values = {
                        "address", "Адрес орга", "shortOrgName", "Моя орга", "reportValues", os.toString("UTF-8")};
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        } else {
            String errorMsg = String.format(
                    "Ошибка построения отчета \"%s\". В указанный период времени (\"%s - \"%s) данные по организации отсутствуют. Попробуйте изменить параметры отчета.",
                    this.getClass().getCanonicalName(), startDate.toString(), endDate.toString());
            logger.warn(errorMsg);
            printWarn(errorMsg);
        }
        return null;
    }

    public Object buildReportHTML() {
        htmlReport = null;
        if (invalidFormData()) {
            return null;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_summary.jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        RequestsAndOrdersReport.Builder builder = new RequestsAndOrdersReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        } else {
            String errorMsg = "Данные за указанный период отсутствуют. Попробуйте изменить параметры отчета.";
            logger.warn(errorMsg);
            printWarn(errorMsg);
        }
        return null;
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = RequestsAndOrdersReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private boolean invalidFormData() {
        if (CollectionUtils.isEmpty(idOfOrgList) && CollectionUtils.isEmpty(idOfContragentOrgList)) {
            printError("Выберите список организаций или поставщиков");
            return true;
        }
        if (startDate == null) {
            printError("Не указано дата выборки от");
            return true;
        }
        if (endDate == null) {
            printError("Не указано дата выборки до");
            return true;
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
            return true;
        }
        return false;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        if (invalidFormData()) {
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile("_export.jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return;
        }
        Date generateTime = new Date();
        RequestsAndOrdersReport.Builder builder = new RequestsAndOrdersReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        if (report != null) {
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
                printMessage("Сводный отчет по заявкам построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        } else {
            String errorMsg = String.format(
                    "Ошибка построения отчета \"%s\". В указанный период времени (\"%s - \"%s) данные по организации отсутствуют. Попробуйте изменить параметры отчета.",
                    this.getClass().getCanonicalName(), startDate.toString(), endDate.toString());
            logger.warn(errorMsg);
            printWarn(errorMsg);
        }
    }

    public List<SelectItem> getFeedingPlanTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem("Все"));
        items.add(new SelectItem(FeedingPlanType.REDUCED_PRICE_PLAN.toString()));
        items.add(new SelectItem(FeedingPlanType.PAY_PLAN.toString()));
        items.add(new SelectItem(FeedingPlanType.SUBSCRIPTION_FEEDING.toString()));
        return items;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String sourceMenuOrgId = StringUtils.join(idOfContragentOrgList.iterator(), ",");
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG, sourceMenuOrgId);
        String idOfOrgString = "";
        if (idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        properties.setProperty(RequestsAndOrdersReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(hideMissedColumns));
        properties.setProperty(RequestsAndOrdersReport.P_USE_COLOR_ACCENT, Boolean.toString(useColorAccent));
        properties.setProperty(RequestsAndOrdersReport.P_SHOW_ONLY_DIVERGENCE, Boolean.toString(showOnlyDivergence));
        if (feedingPlanType != null) {
            properties.setProperty(RequestsAndOrdersReport.P_FEEDING_PLAN_TYPE, feedingPlanType.toString());
        }
        properties.setProperty(RequestsAndOrdersReport.P_NO_NULL_REPORT, Boolean.toString(noNullReport));
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "RequestsAndOrdersReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/requests_and_orders_report";
    }

    /* Getter and Setters */

    public String getHtmlReport() {
        return htmlReport;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public Boolean getHideMissedColumns() {
        return hideMissedColumns;
    }

    public void setHideMissedColumns(Boolean hideMissedColumns) {
        this.hideMissedColumns = hideMissedColumns;
    }

    public Boolean getApplyUserSettings() {
        return applyUserSettings;
    }

    public void setApplyUserSettings(Boolean applyUserSettings) {
        this.applyUserSettings = applyUserSettings;
    }

    public Boolean getShowOnlyDivergence() {
        return showOnlyDivergence;
    }

    public void setShowOnlyDivergence(Boolean showOnlyDivergence) {
        this.showOnlyDivergence = showOnlyDivergence;
    }

    public Boolean getUseColorAccent() {
        return useColorAccent;
    }

    public void setUseColorAccent(Boolean useColorAccent) {
        this.useColorAccent = useColorAccent;
    }

    public String getFeedingPlanType() {
        return feedingPlanType;
    }

    public void setFeedingPlanType(String feedingPlanType) {
        this.feedingPlanType = feedingPlanType;
    }

    public Boolean getNoNullReport() {
        return noNullReport;
    }

    public void setNoNullReport(Boolean noNullReport) {
        this.noNullReport = noNullReport;
    }
}
