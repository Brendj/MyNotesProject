/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ClientBalanceByOrgReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 20.10.11
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientBalanceByOrgReportPage extends OnlineReportPageOnePerUser implements ContragentSelectPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(ClientBalanceByOrgReportPage.class);
    private List<ClientBalanceByOrgReport.Builder.OrgBalanceInfo> clientsBalance;
    private Contragent contragent;
    private Long totalBalance;
    private ClientBalanceByOrgReport clientBalanceByOrg;
    private String htmlReport;

    private final ClientFilter clientFilter = new ClientFilter();

    public ClientBalanceByOrgReportPage() {
        super();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.DATE).getTime();
        //startDate = new Date(System.currentTimeMillis());
    }

    public String getPageFilename() {
        return "report/online/client_balance_by_org_report";
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public List<ClientBalanceByOrgReport.Builder.OrgBalanceInfo> getClientsBalance() {
        return clientsBalance;
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    private boolean validateFormData() {
        if (startDate == null) {
            printError("Не указана дата");
            return true;
        }
        if ((contragent == null || contragent.getIdOfContragent() == null) && CollectionUtils.isEmpty(idOfOrgList)) {
            printError("Выберите список организаций или поставщика");
            return true;
        }
        return false;
    }

    public Object showOrgListSelectPage() {
        if (contragent != null) {
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(contragent.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object buildReport() {
        if (validateFormData()) {
            return null;
        }
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientBalanceByOrgReport.Builder reportBuilder = new ClientBalanceByOrgReport.Builder("");
            final Long idOfContragent = contragent == null ? null : contragent.getIdOfContragent();
            Date date = CalendarUtils.addOneDay(startDate);
            localCalendar.setTime(date);
            localCalendar.add(Calendar.SECOND, -1);
            startDate = localCalendar.getTime();
            clientsBalance = reportBuilder.buildReportItems(persistenceSession, idOfContragent, idOfOrgList, startDate,
                    clientFilter.getClientGroupId(), clientFilter.getClientBalanceCondition());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            totalBalance = 0L;
            for (ClientBalanceByOrgReport.Builder.OrgBalanceInfo item : clientsBalance) {
                totalBalance += item.getTotalBalance();
            }
            SimpleDateFormat dateShortFormat = new SimpleDateFormat("dd.MM.yyyy");
            printMessage("Состояние баланса лицевых счетов на дату " + dateShortFormat.format(startDate));
        } catch (Exception e) {
            logger.error("Failed to build client balance report", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    private BasicReportJob makeReport() {
        Properties properties = new Properties();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = ClientBalanceByOrgReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        ClientBalanceByOrgReport.Builder builder = new ClientBalanceByOrgReport.Builder(templateFilename);
        if (!CollectionUtils.isEmpty(idOfOrgList)) {
            String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (contragent != null) {
                builder.setContragent(contragent);
            }
            Date date = CalendarUtils.addOneDay(startDate);
            localCalendar.setTime(date);
            localCalendar.add(Calendar.SECOND, -1);

            startDate = localCalendar.getTime();
            endDate = localCalendar.getTime();
            properties.setProperty("clientGroupId", String.valueOf(clientFilter.getClientGroupId()));
            properties.setProperty("clientBalanceCondition", String.valueOf(clientFilter.getClientBalanceCondition()));
            builder.setReportProperties(properties);
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
        return report;
    }

    public Object exportToHtml() {
        if (validateFormData()) {
            return null;
        }
        this.clientBalanceByOrg = (ClientBalanceByOrgReport)makeReport();
        htmlReport = clientBalanceByOrg.getHtmlReport();
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        if (validateFormData()) {
            return;
        }
        BasicReportJob report = makeReport();
        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(new Date(), report);
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
                printMessage("Отчет построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "ClientBalanceByOrgReport", reportDistinctText, format);
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (null != idOfContragent) {
            this.contragent = (Contragent) session.get(Contragent.class, idOfContragent);
        } else {
            this.contragent = null;
        }
    }

    public ClientBalanceByOrgReport getClientBalanceByOrg() {
        return clientBalanceByOrg;
    }

    public void setClientBalanceByOrg(ClientBalanceByOrgReport clientBalanceByOrg) {
        this.clientBalanceByOrg = clientBalanceByOrg;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }
}
