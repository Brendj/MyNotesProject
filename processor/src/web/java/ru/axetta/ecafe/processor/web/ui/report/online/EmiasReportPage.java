/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

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
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * Created by voinov on 13.05.21.
 */
public class EmiasReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(EmiasReportPage.class);

    private String htmlReport = null;

    public EmiasReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public String getPageFilename() {
        return "report/online/emias_report";
    }

    private Boolean allFriendlyOrgs;
    private List<EmiasItem> items;

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();

        if (templateFilename == null) {
            return null;
        }
        EmiasReport.Builder builder = new EmiasReport.Builder(templateFilename);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setReportProperties(buildProperties());
                items = builder.createDataSource(persistenceSession);
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
        return items;
    }

    public void generateXLS(ActionEvent event) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename != null) {
            EmiasReport.Builder builder = new EmiasReport.Builder(templateFilename);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setReportProperties(buildProperties());

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

            FacesContext facesContext = FacesContext.getCurrentInstance();
            try {
                if (report != null) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();
                    ServletOutputStream servletOutputStream = response.getOutputStream();
                    facesContext.getResponseComplete();
                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    response.setHeader("Content-disposition", "inline;filename=EmiasReport.xls");
                    JRXlsExporter xlsExporter = new JRXlsExporter();
                    xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                    xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.FALSE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                    xlsExporter.exportReport();
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выгрузке отчета:", e);
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = "EmiasReport.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        if (!filter.equals("Не выбрано")) {
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, (null != idOfOrg) ? idOfOrg.toString() : "0");
        }

        List<Long> idClients = new ArrayList<>();
        for (ClientSelectListPage.Item item: getClientList())
        {
            idClients.add(item.getIdOfClient());
        }
        properties.setProperty(BlockUnblockCardReport.P_ID_OF_CLIENTS, StringUtils.join(idClients, ','));
        properties.setProperty(BlockUnblockCardReport.P_ALL_FRIENDLY_ORGS, allFriendlyOrgs.toString());
       // properties.setProperty(BlockUnblockCardReport.P_CARD_STATUS, cardStatusFilter);
        return properties;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public List<EmiasItem> getItems() {
        return items;
    }

    public void setItems(List<EmiasItem> items) {
        this.items = items;
    }
}
