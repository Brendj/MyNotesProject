/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.OrgSmsStatsReport;

import org.hibernate.Session;
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
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.02.14
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class OrgSmsStatsReportPage extends OnlineReportPage {
    private final static Logger logger = LoggerFactory.getLogger(OrgSmsStatsReportPage.class);
    protected OrgSmsStatsReport report;

    private Date start;
    private Date end;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    @Override
    public String getPageFilename() {
        return "report/online/org_sms_stats_report";
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void doGenerateXLS(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(OrgSmsStatsReportPage.class).generateXLS();
    }

    public void doGenerate() {
        RuntimeContext.getAppContext().getBean(OrgSmsStatsReportPage.class).generate();
    }

    @Transactional
    public void generate() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            generateReport(session, null);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void generateXLS() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            generateXLS(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void generateReport(Session session, String templateFile) throws Exception {
        OrgSmsStatsReport.Builder reportBuilder = null;
        if(templateFile != null) {
            reportBuilder = new OrgSmsStatsReport.Builder(templateFile);
        } else {
            reportBuilder = new OrgSmsStatsReport.Builder();
        }
        if (idOfOrg != null) {
            Org org = null;
            if (idOfOrg != null && idOfOrg > -1) {
                org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
            }
            reportBuilder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        }
        this.report = reportBuilder.build (session, startDate, endDate, new GregorianCalendar());
    }

    public void generateXLS(Session session) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + OrgSmsStatsReport.class.getSimpleName() + ".jasper";
            generateReport(session, templateFilename);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=sent_sms_stats.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, this.report.getPrint());
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
            getLogger().error("Failed to build org sms stats report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
        } finally {
        }
    }

    public OrgSmsStatsReport getReport() {
        return report;
    }
}
