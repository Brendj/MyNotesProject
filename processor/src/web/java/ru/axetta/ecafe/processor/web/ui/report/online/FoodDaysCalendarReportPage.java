/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfClientGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.FoodDaysCalendarReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientGroupSelectPage;
import ru.axetta.ecafe.processor.web.ui.converter.OrgRequestFilterConverter;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class FoodDaysCalendarReportPage extends OnlineReportPage implements OrgSelectPage.CompleteHandler, ClientGroupSelectPage.CompleteHandler {
    private final static Logger logger = LoggerFactory.getLogger(FoodDaysCalendarReportPage.class);

    private String htmlReport = null;
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
    private Boolean applyUserSettings = false;
    private OrgRequestFilterConverter orgRequest = new OrgRequestFilterConverter();
    private String clientGroupName;
    private Long idOfClientGroup;
    private Boolean allOrg = false;
    private Long idOfOrgs;

    public void showOrgListSelectPage () {
        MainPage.getSessionInstance().showOrgListSelectPage();
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

    public Object clean(){
        clientGroupName = null;
        idOfClientGroup = null;
        return null;
    }

    public void completeClientGroupSelection(Session session, Long idOfClientGroup) throws Exception {
        if (null != idOfClientGroup) {
            this.idOfClientGroup = idOfClientGroup;
            this.clientGroupName = DAOUtils
                    .findClientGroup(session, new CompositeIdOfClientGroup(getIdOfOrgs(), idOfClientGroup))
                    .getGroupName();
        }
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

    public Object buildReportHTML() {
        htmlReport = null;
        if (validateFormData())  return null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        FoodDaysCalendarReport builder = new FoodDaysCalendarReport(templateFilename);
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
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                htmlReport = os.toString("UTF-8");
                os.close();
            } catch (Exception e) {
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = FoodDaysCalendarReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private boolean validateFormData() {
        if(CollectionUtils.isEmpty(idOfOrgList)){
            printError("Выберите список организаций");
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
        return false;
    }

    public void exportToXLS(ActionEvent actionEvent){
        if (validateFormData()) return;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) return ;
        Date generateTime = new Date();
        FoodDaysCalendarReport builder = new FoodDaysCalendarReport(templateFilename);
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
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String idOfOrgString = "";
        if(idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        if (idOfClientGroup != null){
            properties.setProperty("idOfClientGroup", idOfClientGroup.toString());
            properties.setProperty("clientGroupName", clientGroupName);
        }

        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "FoodDaysCalendarReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/food_days_calendar_report";
    }

    public OrgRequestFilterConverter getOrgRequest() {
        return orgRequest;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public Boolean getApplyUserSettings() {
        return applyUserSettings;
    }

    public void setApplyUserSettings(Boolean applyUserSettings) {
        this.applyUserSettings = applyUserSettings;
    }

    public String getClientGroupName() {
        return clientGroupName;
    }

    public void setClientGroupName(String clientGroupName) {
        this.clientGroupName = clientGroupName;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Boolean getAllOrg() {
        return allOrg;
    }

    public void setAllOrg(Boolean allOrg) {
        this.allOrg = allOrg;
    }

    public Long getIdOfOrgs() {
        if (idOfOrgList.size() == 1)
            return idOfOrgList.get(0);
        else {
            clientGroupName = null;
            idOfClientGroup = null;
            return null;
        }
    }

    public void setIdOfOrgs(Long idOfOrgs) {
        this.idOfOrgs = idOfOrgs;
    }

}
