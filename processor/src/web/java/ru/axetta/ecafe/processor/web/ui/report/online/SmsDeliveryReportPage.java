/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.IPrintWarn;
import ru.axetta.ecafe.processor.core.report.SMSDeliveryReport;
import ru.axetta.ecafe.processor.core.service.SmsDeliveryCalculationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.12.14
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class SmsDeliveryReportPage extends OnlineReportPage {
    @PersistenceContext(unitName = "reportsPU")
    public EntityManager entityManager;
    private final static Logger logger = LoggerFactory.getLogger(SmsDeliveryReportPage.class);
    private SMSDeliveryReport report;
    private Boolean isActiveState = true;
    private Boolean moreThanTwoMinutes = true;

    public SmsDeliveryReportPage() throws RuntimeContext.NotInitializedException {
        super();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        CalendarUtils.truncateToDayOfMonth(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        this.endDate = calendar.getTime();
    }

    public String getPageFilename() {
        return "report/online/sms_delivery_report";
    }

    public SMSDeliveryReport getReport() {
        return report;
    }

    public void buildReport () throws Exception {
        RuntimeContext.getAppContext().getBean(SmsDeliveryReportPage.class).execute();
    }

    @Transactional
    public void execute() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            buildReport(session);
        } catch (Exception e) {
                logger.error("Failed to load sent sms data", e);
        }
    }

    public void buildReport(Session session) throws Exception {
        IPrintWarn printer = new IPrintWarn() {
            @Override
            public void PrintWarn(String message) {
                printWarn(message);
            }
        };
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(startDate.after(endDate)) {
            complainWrongPeriod(facesContext);
            return;
        }
        this.report = new SMSDeliveryReport ();
        SMSDeliveryReport.Builder reportBuilder = new SMSDeliveryReport.Builder(printer);
        addStateFilter(reportBuilder);
        addMoreThanTwoMinutesFilter(reportBuilder);
        addOrgFilter(reportBuilder);

        try {
            this.report = reportBuilder.build (session, startDate, endDate, new GregorianCalendar());
        }
        catch (IllegalArgumentException iex) {
            printWarn(iex.getMessage());
        }
        catch (Exception ex) {
            printError(ex.getMessage());
        }
    }

    protected void addOrgFilter(SMSDeliveryReport.Builder builder) {
        if (idOfOrgList != null && idOfOrgList.size() > 0) {
            Properties properties = builder.getReportProperties();
            String idOfOrgString = "";
            if(idOfOrgList != null) {
                idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
            }
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        }
    }

    protected void addStateFilter(SMSDeliveryReport.Builder builder) {
        Properties properties = builder.getReportProperties();
        properties.setProperty(SMSDeliveryReport.IS_ACTIVE_STATE, Boolean.toString(isActiveState));
    }

    protected void addMoreThanTwoMinutesFilter(SMSDeliveryReport.Builder builder) {
        Properties properties = builder.getReportProperties();
        properties.setProperty(SMSDeliveryReport.MORE_THAN_TWO_MINUTES, Boolean.toString(moreThanTwoMinutes));
    }



    public void showXLS(ActionEvent actionEvent){
        RuntimeContext.getAppContext().getBean(SmsDeliveryReportPage.class).doBuidXLS();
    }

    public void recalculateSyncData(ActionEvent actionEvent){
        RuntimeContext.getAppContext().getBean(SmsDeliveryCalculationService.class).doRun();
        printMessage("Пересчет закончен.");
    }

    @Transactional
    public void doBuidXLS() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            doBuidXLS(session);
        } catch (Exception e) {
            logger.error("Failed to load sent sms data", e);
        }
    }

    public void doBuidXLS(Session session) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        if(startDate.after(endDate)) {
            complainWrongPeriod(facesContext);
            return;
        }
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename;
            if (moreThanTwoMinutes) {
                templateFilename =
                        autoReportGenerator.getReportsTemplateFilePath() + SMSDeliveryReport.class.getSimpleName() + ".jasper";
            } else {
                templateFilename =
                        autoReportGenerator.getReportsTemplateFilePath() + "SMSDeliveryReportStatus" + ".jasper";
            }

            SMSDeliveryReport.Builder builder = new SMSDeliveryReport.Builder(templateFilename);

            addOrgFilter(builder);
            addStateFilter(builder);
            addMoreThanTwoMinutesFilter(builder);
            /*if(idOfOrg != null) {
                builder.setOrg(new BasicReportJob.OrgShortItem(idOfOrg, filter, filter));
            }*/
            SMSDeliveryReport smsDeliveryReport = builder.build(session, startDate, endDate, new GregorianCalendar());

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=smsDelivery.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, smsDeliveryReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            //xlsExport.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            String message = (fnfe.getCause()==null?fnfe.getMessage():fnfe.getCause().getMessage());
            logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message),fnfe);
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    private void complainWrongPeriod(FacesContext facesContext) {
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Начальная дата больше конечной.", null));
    }

    public Boolean getIsActiveState() {
        return isActiveState;
    }

    public void setIsActiveState(Boolean activeState) {
        isActiveState = activeState;
    }


    public Boolean getMoreThanTwoMinutes() {
        return moreThanTwoMinutes;
    }

    public void setMoreThanTwoMinutes(Boolean moreThanTwoMinutes) {
        this.moreThanTwoMinutes = moreThanTwoMinutes;
    }
}
