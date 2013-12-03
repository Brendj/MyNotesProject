/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.ContragentPaymentReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
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
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 11.11.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class ContragentPaymentReportPage extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler {
    private ContragentPaymentReport contragentPaymentReport;
    private String htmlReport;
    private Org org;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager em;
    private final CCAccountFilter contragentReceiverFilter = new CCAccountFilter();
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private boolean receiverSelection;

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

    public void showCSVList(ActionEvent actionEvent){
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

    @Transactional
    public void buildReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        try {
            persistenceSession = (Session) em.getDelegate();
            buildReport(persistenceSession, getContragent());
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }/* finally {
            HibernateUtils.close(persistenceSession, getLogger());
        }*/
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
        return props;
    }
}

