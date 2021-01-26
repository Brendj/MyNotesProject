/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.TypesOfCardReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;

import org.hibernate.HibernateException;
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
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.11.14
 * Time: 14:04
 */
public class TypesOfCardReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReportPage.class);

    private Boolean includeSummaryByDistrict = false;
    private Boolean includeAllBuildings = false;

    private String htmlReport = null;

    private final ClientFilter clientFilter = new ClientFilter();

    public String getPageFilename() {
        return "report/online/types_of_card_report";
    }

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
            return null;
        }
        String subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        TypesOfCardReport.Builder builder = new TypesOfCardReport.Builder(templateFilename, subReportDir);
        builder.setReportProperties(buildProperties());
        builder.setOrgsList(idOfOrgList);

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setUserId(DAOUtils.findUser(persistenceSession,
                        FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()).getIdOfUser());
                report = builder.build(persistenceSession, startDate, localCalendar);
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
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
        } else {
            String subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
            TypesOfCardReport.Builder builder = new TypesOfCardReport.Builder(templateFilename, subReportDir);
            builder.setReportProperties(buildProperties());
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setUserId(DAOUtils.findUser(persistenceSession,
                        FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()).getIdOfUser());
                builder.setOrgsList(idOfOrgList);
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
                    response.setHeader("Content-disposition", "inline;filename=typesOfCardReport.xls");
                    JRXlsExporter xlsExporter = new JRXlsExporter();
                    xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                    xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
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

    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        super.completeOrgListSelection(orgMap);
        if (emptyOrgs()) {
            includeAllBuildings = false;
        }
    }

    public boolean emptyOrgs() {
        return ((idOfOrgList == null) || (idOfOrgList.isEmpty())) ? true : false;
    }

    public Boolean getIncludeSummaryByDistrict() {
        return includeSummaryByDistrict;
    }

    public void setIncludeSummaryByDistrict(Boolean includeSummaryByDistrict) {
        htmlReport = null;
        this.includeSummaryByDistrict = includeSummaryByDistrict;
    }

    public Boolean getIncludeAllBuildings() {
        return includeAllBuildings;
    }

    public void setIncludeAllBuildings(Boolean includeAllBuildings) {
        this.includeAllBuildings = includeAllBuildings;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public void onShow() throws Exception {
        startDate = new Date();
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = TypesOfCardReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        if (includeSummaryByDistrict) {
            properties.setProperty(TypesOfCardReport.PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS,
                    includeSummaryByDistrict.toString());
        }
        if (includeAllBuildings) {
            properties.setProperty(TypesOfCardReport.PARAM_INCLUDE_ALL_BUILDINGS,
                    includeAllBuildings.toString());
        }

        if (clientFilter.getClientGroupId() != null) {
            properties.setProperty(TypesOfCardReport.PARAM_CLIENT_GROUP, clientFilter.getClientGroupId().toString());
        }

        String groupName = "";
        for (Map.Entry<String, Long> entry : clientFilter.getClientGroupItems().entrySet()) {
            if (entry.getValue().equals(clientFilter.getClientGroupId())) {
                groupName = entry.getKey();
            }
        }

        properties.setProperty(TypesOfCardReport.PARAM_GROUP_NAME, groupName);

        return properties;
    }
}
