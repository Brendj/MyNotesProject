/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.SyncMonitorReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.09.12
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class SyncMonitorPage extends OnlineReportPage {

    private List<DashboardResponse.OrgSyncStatItem> items;
    private Date lastUpdate;

    private Boolean showVersion = false;
    private Integer[] versionTitles;
    private HashMap<Integer, SelectItem> availableVersions;

    private final static Logger logger = LoggerFactory.getLogger(SyncMonitorPage.class);


    public SyncMonitorPage() {
    }

    public List<DashboardResponse.OrgSyncStatItem> getItemList() {
        return items;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public Boolean getShowVersion() {
        return showVersion;
    }

    public void setShowVersion(Boolean showVersion) {
        this.showVersion = showVersion;
    }

    public Integer[] getVersionTitles() {
        return versionTitles;
    }

    public void setVersionTitles(Integer[] versionTitles) {
        this.versionTitles = versionTitles;
    }

    public void buildReportXLS(ActionEvent actionEvent) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortName = "SyncMonitorReport";
        String templateShortFileName = templateShortName + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return;
        }
        SyncMonitorReport.Builder builder = new SyncMonitorReport.Builder();
        builder.setTemplateFilename(templateFilename);
        if (showVersion)
            builder.setVersionsList(getVersionsList());

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            SyncMonitorReport totalSalesReport = (SyncMonitorReport) builder.build(session, null, null, null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            Date generateTime = new Date();
            String filename = buildFileName(generateTime, totalSalesReport);
            response.setHeader("Content-disposition", String.format("inline;filename=%s", filename));

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, totalSalesReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            printError("Ошибка при построении отчета: "+e.getMessage());
            logger.error("Failed build report: " + e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public Object buildReportHtml() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            items = SyncMonitorReport.Builder.getSyncReportData(session, (showVersion) ? getVersionsList() : null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            printError("Ошибка при построении отчета: "+e.getMessage());
            logger.error("Failed build report: " + e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        if (lastUpdate == null) {
            lastUpdate = new Date();
        }
        lastUpdate.setTime(System.currentTimeMillis());
        return null;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s.xls", "SyncMonitorReport", reportDistinctText, format);
    }

    public HashMap<Integer, SelectItem> getAvailableVersions() {
        if (null == availableVersions) {
            availableVersions = new HashMap<Integer, SelectItem>();
        }
        if (availableVersions.isEmpty()) {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session session = null;
            Transaction persistenceTransaction = null;
            List<String> versions = null;
            try {
                session = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = session.beginTransaction();
                versions = SyncMonitorReport.getAvailableVersions(session);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report: " + e.getMessage(), e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(session, logger);
            }

            int i = 0;
            for (String v : versions) {
                SelectItem selectItem = new SelectItem(i, v);
                availableVersions.put(i, selectItem);
                i++;
            }
        }
        return availableVersions;
    }

    public void setAvailableVersions(HashMap<Integer, SelectItem> availableVersions) {
        this.availableVersions = availableVersions;
    }

    private List<String> getVersionsList() {
        if (null == versionTitles)
            return null;

        List<String> versionsList = new ArrayList<String>();
        for (Integer v : versionTitles) {
            if (availableVersions.containsKey(v))
                versionsList.add(availableVersions.get(v).getLabel());
        }
        return versionsList;
    }

    public String getPageFilename() {
        return "monitoring/sync_monitor";
    }
}