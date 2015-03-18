/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ContragentPaymentReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeMenu;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 11.11.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ContragentPaymentReportPage extends OnlineReportCustomPage implements ContragentSelectPage.CompleteHandler {
    private ContragentPaymentReport contragentPaymentReport;
    private String htmlReport;
    private Org org;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager em;
    private final CCAccountFilter contragentReceiverFilter = new CCAccountFilter();
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private boolean receiverSelection;
    private final PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu();
    // тип организации
    private OrganizationTypeModify organizationTypeModify;
    private final OrganizationTypeModifyMenu organizationTypeModifyMenu = new OrganizationTypeModifyMenu();
    private String terminal;
    private String paymentIdentifier;

    String emptyData = null;

    public String getEmptyData() {
        return emptyData;
    }

    public void setEmptyData(String emptyData) {
        this.emptyData = emptyData;
    }

    public String getPaymentIdentifier() {
        return paymentIdentifier;
    }

    public void setPaymentIdentifier(String paymentIdentifier) {
        this.paymentIdentifier = paymentIdentifier;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public OrganizationTypeModify getOrganizationTypeModify() {
        return organizationTypeModify;
    }

    public void setOrganizationTypeModify(OrganizationTypeModify organizationTypeModify) {
        this.organizationTypeModify = organizationTypeModify;
    }

    public OrganizationTypeModifyMenu getOrganizationTypeModifyMenu() {
        return organizationTypeModifyMenu;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged(ActionEvent event) {
        switch (periodTypeMenu.getPeriodType()){
            case ONE_DAY: {
                setEndDate(startDate);
                setHoursMinuteSecond(endDate);
            } break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
                setHoursMinuteSecond(endDate);
            } break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
                setHoursMinuteSecond(endDate);
            } break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
                setHoursMinuteSecond(endDate);
            } break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(CalendarUtils.addDays(end, -1), -1).equals(startDate)){
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
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
        }
    }


    public String getPageFilename() {
        return "report/online/contragent_payment_report";
    }

    public ContragentPaymentReport getContragentPaymentReport() {
        return contragentPaymentReport;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public CCAccountFilter getContragentReceiverFilter() {
        return contragentReceiverFilter;
    }

    public void showContragentSelectPage (boolean isReceiver) {
        idOfOrgList.clear();
        filter = "Не выбрано";
        receiverSelection = isReceiver;
        MainPage.getSessionInstance().showContragentSelectPage();
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (!receiverSelection) {
            contragentFilter.completeContragentSelection(session, idOfContragent);
        } else {
            contragentReceiverFilter.completeContragentSelection(session, idOfContragent);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportPage.class);

    public void exportToXLS(ActionEvent actionEvent){
        if (validateFormData()) return;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + ContragentPaymentReport.class.getSimpleName() + ".jasper";
        if (StringUtils.isEmpty(templateFilename)) return ;
        ContragentPaymentReport.Builder builder = new ContragentPaymentReport.Builder(templateFilename);
        //Date generateTime = new Date();
        //builder.setReportProperties(fillContragentReceiver());
        if (contragentFilter.getContragent().getIdOfContragent() == null
                || contragentReceiverFilter.getContragent().getIdOfContragent() == null) {
            printError("Не выбран 'Агент по приему платежей' или 'Контрагент-получатель'");
        } else {
            builder.getReportProperties().setProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_PAYER_ID,
                    Long.toString(contragentFilter.getContragent().getIdOfContragent()));
            builder.getReportProperties().setProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID,
                    Long.toString(contragentReceiverFilter.getContragent().getIdOfContragent()));
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty("organizationTypeModify", String.valueOf(getOrganizationTypeModify()));
            builder.getReportProperties().setProperty("terminal", terminal);
            builder.getReportProperties().setProperty("paymentIdentifier", paymentIdentifier);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try{
                try {
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    builder.setContragent(getContragent());
                    report = builder.build(persistenceSession, startDate, endDate, localCalendar);
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }  finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }

            if (report != null) {
                try {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();

                    ServletOutputStream servletOutputStream = response.getOutputStream();

                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    //response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));
                    response.setHeader("Content-disposition", "inline;filename=contragent_payment.xls");

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
                    //printMessage("Сводный отчет по заявкам построен");
                } catch (Exception e) {
                    logger.error("Failed export report : ", e);
                    printError("Ошибка при подготовке отчета: " + e.getMessage());
                }
            }
        }
    }

    public void showCSVList(ActionEvent actionEvent){
        if (validateFormData()) return;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + ContragentPaymentReport.class.getSimpleName() + ".jasper";
            ContragentPaymentReport.Builder builder = new ContragentPaymentReport.Builder(templateFilename);
            builder.setContragent(getContragent());
            builder.setReportProperties(fillContragentReceiver());
            Session session = RuntimeContext.getInstance().createPersistenceSession();
            contragentPaymentReport = (ContragentPaymentReport) builder.build(session,startDate, endDate, localCalendar);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=contragent_payment.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, contragentPaymentReport.getPrint());
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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    private boolean validateFormData() {
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
        if (contragentFilter.getContragent().getIdOfContragent() == null
                || contragentReceiverFilter.getContragent().getIdOfContragent() == null) {
            printError("Не выбран 'Агент по приему платежей' и 'Контрагент-получатель'");
        }
        return false;
    }

    public Object buildReport() {
        htmlReport="";
        if (validateFormData()) return null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ContragentPaymentReport.Builder builder = new ContragentPaymentReport.Builder();
        if (contragentFilter.getContragent().getIdOfContragent() == null) return null;
        builder.getReportProperties().setProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_PAYER_ID,
                Long.toString(contragentFilter.getContragent().getIdOfContragent()));
        if (contragentReceiverFilter.getContragent().getIdOfContragent() == null) return null;
        builder.getReportProperties().setProperty(BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID,
                Long.toString(contragentReceiverFilter.getContragent().getIdOfContragent()));
        builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
        builder.getReportProperties().setProperty("organizationTypeModify", String.valueOf(getOrganizationTypeModify()));
        builder.getReportProperties().setProperty("terminal", terminal);
        builder.getReportProperties().setProperty("paymentIdentifier", paymentIdentifier);
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            builder.setContragent(getContragent());
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            emptyData = builder.getError();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        //if (builder.isTransactionsWithoutOrgIsPresented()) {
        //    String warningMessage =
        //            "Внимание! Если в организации есть клиенты перемещенные с других организаций данные представленные в отчете могут быть некорректны. "
        //                    + "В наборе данных полученных на выбранный диапазон дат имеются транзакции для которых не указана организация.";
        //    printWarn(warningMessage);
        //}

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
        }
        return null;
    }

    public void buildReport(Session session, Contragent contragent) throws Exception {
        ContragentPaymentReport.Builder reportBuilder = new ContragentPaymentReport.Builder();
        reportBuilder.setContragent(contragent);
        /*reportProperties.setProperty(ContragentPaymentReport.PARAM_PERIOD_TYPE, "");*/
        reportBuilder.setReportProperties(fillContragentReceiver());
        contragentPaymentReport = (ContragentPaymentReport) reportBuilder.build(session, startDate, endDate, localCalendar);
        htmlReport = contragentPaymentReport.getHtmlReport();
    }

    private Contragent getContragent() throws Exception {
        Contragent contragent = null;
        if (contragentFilter != null && contragentFilter.getContragent() != null &&
            contragentFilter.getContragent().getIdOfContragent() != null) {
            try {
                contragent = DAOService.getInstance().getContragentById(
                        contragentFilter.getContragent().getIdOfContragent());
            } catch (Exception e) { }
        }
        if (contragent == null) {
            throw new Exception("Необходимо выбрать контрагента");
        }
        return contragent;
    }

    private Properties fillContragentReceiver() {
        return fillContragentReceiver(new Properties());
    }

    private Properties fillContragentReceiver(Properties props) {
        if (contragentReceiverFilter.getContragent() != null &&
                contragentReceiverFilter.getContragent().getIdOfContragent() != null) {
            props.setProperty(ContragentPaymentReport.PARAM_CONTRAGENT_RECEIVER_ID,
                    "" + contragentReceiverFilter.getContragent().getIdOfContragent());
        }
        if (contragentFilter.getContragent() != null &&
                contragentFilter.getContragent().getIdOfContragent() != null) {
            props.setProperty(ContragentPaymentReport.PARAM_CONTRAGENT_PAYER_ID,
                    "" + contragentFilter.getContragent().getIdOfContragent());
        }
        return props;
    }

    public Object showOrgListSelectPage () {
        if(contragentReceiverFilter.getContragent()!=null){
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(contragentReceiverFilter.getContragent().getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }
}

