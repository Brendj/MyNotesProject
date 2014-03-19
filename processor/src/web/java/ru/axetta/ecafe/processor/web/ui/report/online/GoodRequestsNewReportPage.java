/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.*;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestsNewReportPage extends OnlineReportWithContragentPage {

    private final static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportPage.class);

    private String htmlReport = null;
    private final PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
    private Date generateBeginDate = new Date();
    private Date generateEndDate = new Date();
    private Boolean hideMissedColumns = true;
    private Boolean hideDailySamplesCount = false;
    private Boolean hideGeneratePeriod = false;
    private String nameFiler;
    private final OrgRequestFilterConverter orgRequest = new OrgRequestFilterConverter();

    public GoodRequestsNewReportPage() {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public void onReportPeriodChanged(ActionEvent event) {
        htmlReport = null;
        switch (periodTypeMenu.getPeriodType()){
            case ONE_DAY: {
                setEndDate(startDate);
            } break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            } break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            } break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            } break;
        }
    }

    public void onGeneratePeriodChanged(ActionEvent event) {
        generateEndDate = new Date(generateBeginDate.getTime()+60*60*1000);
    }

    public void onEndDateSpecified(ActionEvent event) {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(end, -1).equals(CalendarUtils.addDays(startDate, -1))){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noofdays=(int)(diff/(24*60*60*1000));
            switch (noofdays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
    }

    @Override
    public String getContragentStringIdOfOrgList() {
        return idOfContragentOrgList.toString().replaceAll("[^0-9,]", "");
    }

    public Object buildReportHTML() {
        if(CollectionUtils.isEmpty(idOfOrgList) && CollectionUtils.isEmpty(idOfContragentOrgList)){
            printError("Выберите список организаций или поставщиков");
            return null;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = GoodRequestsNewReport.class.getSimpleName() + "_summary.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            BasicReportJob report =  builder.build(session,startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (report != null) {
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
            }
            printMessage("Сводный отчет по заявкам построен");
        } catch (Exception e) {
            printError("Ошибка при построении отчета: "+e.getMessage());
            logger.error("Failed build report ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent){
        if(CollectionUtils.isEmpty(idOfOrgList) && CollectionUtils.isEmpty(idOfContragentOrgList)){
            printError("Выберите список организаций или поставщиков");
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = GoodRequestsNewReport.class.getSimpleName() + "_summary.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return;
        }
        Date generateTime = new Date();
        GoodRequestsNewReport.Builder builder = new GoodRequestsNewReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            GoodRequestsNewReport goodRequestsNewReport = (GoodRequestsNewReport) builder.build(session,startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            String filename = buildFileName(generateTime, goodRequestsNewReport);
            response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, goodRequestsNewReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String sourceMenuOrgId = StringUtils.join(idOfContragentOrgList.iterator(), ",");
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG, sourceMenuOrgId);
        String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        properties.setProperty(GoodRequestsNewReport.P_HIDE_GENERATE_PERIOD, Boolean.toString(hideGeneratePeriod));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_BEGIN_DATE, Long.toString(generateBeginDate.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_GENERATE_END_DATE, Long.toString(generateEndDate.getTime()));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_MISSED_COLUMNS, Boolean.toString(hideMissedColumns));
        properties.setProperty(GoodRequestsNewReport.P_HIDE_DAILY_SAMPLE_COUNT, Boolean.toString(hideDailySamplesCount));
        properties.setProperty(GoodRequestsNewReport.P_NAME_FILTER, nameFiler);
        properties.setProperty(GoodRequestsNewReport.P_ORG_REQUEST_FILTER, Integer.toString(orgRequest.getOrgRequestFilterEnum().ordinal()));
        return properties;
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

    public void setHideDailySamplesCount(Boolean hideDailySamplesCount) {
        this.hideDailySamplesCount = hideDailySamplesCount;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
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

    public Boolean getHideGeneratePeriod() {
        return hideGeneratePeriod;
    }

    public void setHideGeneratePeriod(Boolean hideGeneratePeriod) {
        this.hideGeneratePeriod = hideGeneratePeriod;
    }
}
