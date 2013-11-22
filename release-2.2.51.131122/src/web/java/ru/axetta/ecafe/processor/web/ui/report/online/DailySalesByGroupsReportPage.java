/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DailySalesByGroupsReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

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
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class DailySalesByGroupsReportPage extends OnlineReportPage {
    private DailySalesByGroupsReport dailySalesReport;
    private String htmlReport;
    private Org org;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager em;
    private String includeComplex;
    private String groupByMenuGroup;
    private String menuGroups;

    public String getPageFilename() {
        return "report/online/daily_sales_by_groups_report";
    }

    public DailySalesByGroupsReport getDailySalesByGroupsReport() {
        return dailySalesReport;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public String getIncludeComplex() {
        return includeComplex;
    }

    public void setIncludeComplex(String includeComplex) {
        this.includeComplex = includeComplex;
    }

    public String getGroupByMenuGroup() {
        return groupByMenuGroup;
    }

    public void setGroupByMenuGroup(String groupByMenuGroup) {
        this.groupByMenuGroup = groupByMenuGroup;
    }

    public String getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(String menuGroups) {
        this.menuGroups = menuGroups;
    }

    public void showCSVList(ActionEvent actionEvent){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";
            DailySalesByGroupsReport.Builder builder = new DailySalesByGroupsReport.Builder(templateFilename);
            builder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
            Session session = RuntimeContext.getInstance().createPersistenceSession();
            dailySalesReport = (DailySalesByGroupsReport) builder.build(session,startDate, endDate, localCalendar);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=daily_sales.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, dailySalesReport.getPrint());
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
        if (idOfOrg != null && idOfOrg > -1) {
            org = DAOService.getInstance().findOrById(idOfOrg);
        }
        if (org == null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Необходимо выбрать организацию", null));
            return;
        }

        Session persistenceSession = null;
        try {
            persistenceSession = (Session) em.getDelegate();
            buildReport(persistenceSession);
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
        }/* finally {
            HibernateUtils.close(persistenceSession, getLogger());
        }*/
    }

    public void buildReport(Session session) throws Exception {
        DailySalesByGroupsReport.Builder reportBuilder = new DailySalesByGroupsReport.Builder();
        reportBuilder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        Properties reportProperties = new Properties();
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_INCLUDE_COMPLEX, includeComplex);
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_GROUP_BY_MENU_GROUP, groupByMenuGroup);
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_MENU_GROUPS, menuGroups);
        reportBuilder.setReportProperties(reportProperties);
        dailySalesReport = (DailySalesByGroupsReport) reportBuilder.build(session, startDate, endDate, localCalendar);
        htmlReport = dailySalesReport.getHtmlReport();
    }
}
