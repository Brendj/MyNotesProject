/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.ActiveClientsReport;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 28.10.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class ActiveClientsReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ActiveClientsReportPage.class);
    private ru.axetta.ecafe.processor.core.report.ActiveClientsReport report;

    public String getPageFilename ()
    {
        return "report/online/active_clients_report";
    }

    public ru.axetta.ecafe.processor.core.report.ActiveClientsReport getReport()
    {
        return report;
    }

    public void buildReport (Session session) throws Exception
    {
        this.report = new ActiveClientsReport ();
        ActiveClientsReport.Builder reportBuilder = new ActiveClientsReport.Builder();
        if (!idOfOrgList.isEmpty()) {
            Org org;
            List<BasicReportJob.OrgShortItem> orgShortItemList = new ArrayList<BasicReportJob.OrgShortItem>();

            for (Long idOfOrg : idOfOrgList) {
                org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
                orgShortItemList.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(),
                        org.getOfficialName()));
            }
            reportBuilder.setOrgShortItemList(orgShortItemList);
        }
        this.report = reportBuilder.build(session, startDate, endDate, new GregorianCalendar());
    }

    public void executeReport() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "ActiveClientsReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    String.format("Не найден файл шаблона '%s'", templateFilename), null));
        } else {

            if (!idOfOrgList.isEmpty()) {
                try {
                    runtimeContext = RuntimeContext.getInstance();
                    persistenceSession = runtimeContext.createPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    buildReport(persistenceSession);
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
                } catch (Exception e) {
                    //logger.error("Failed to build sales report", e);
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
                } finally {
                    try {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
                    } catch (Exception e) {
                        logger.error("Failed to build active clients report", e);
                    }
                }
            } else {
                facesContext
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Выберите организацию или список организаций", null));
            }
        }
    }

    public void exportToXLS(ActionEvent actionEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ActiveClientsReport.Builder reportBuilder = new ActiveClientsReport.Builder();
        Date generateTime = new Date();

        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "ActiveClientsReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    String.format("Не найден файл шаблона '%s'", templateFilename), null));
        } else {

            if (!idOfOrgList.isEmpty()) {
                try {
                    runtimeContext = RuntimeContext.getInstance();
                    persistenceSession = runtimeContext.createPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    if (!idOfOrgList.isEmpty()) {
                        Org org;
                        List<BasicReportJob.OrgShortItem> orgShortItemList = new ArrayList<BasicReportJob.OrgShortItem>();

                        for (Long idOfOrg : idOfOrgList) {
                            org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
                            orgShortItemList.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(),
                                    org.getOfficialName()));
                        }
                        reportBuilder.setOrgShortItemList(orgShortItemList);
                    }
                    this.report = reportBuilder.build(persistenceSession, startDate, endDate, new GregorianCalendar());

                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
                } catch (FileNotFoundException ex) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
                } catch (Exception e) {
                    //logger.error("Failed to build sales report", e);
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
                } finally {
                    try {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
                    } catch (Exception e) {
                        logger.error("Failed to build active clients report", e);
                    }
                }

                if (this.report != null) {
                    try {
                        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                                .getResponse();

                        ServletOutputStream servletOutputStream = response.getOutputStream();

                        facesContext.responseComplete();
                        response.setContentType("application/xls");
                        response.setHeader("Content-disposition", "inline;filename=active_client.xls");

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
                        printMessage("Отчет по активным клиентам построен");
                    } catch (Exception e) {
                        logger.error("Failed export report : ", e);
                        printError("Ошибка при подготовке отчета: " + e.getMessage());
                    }
                }
            } else {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Выберите организацию или список организаций",
                                null));
            }
        }
    }
}
