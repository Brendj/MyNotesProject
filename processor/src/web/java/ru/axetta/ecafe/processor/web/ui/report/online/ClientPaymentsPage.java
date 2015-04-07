/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ClientPaymentsBuilder;
import ru.axetta.ecafe.processor.core.report.ClientPaymentsReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 10.04.13
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsPage extends OnlineReportPage {

    private String htmlReport;

    public String getHtmlReport() {
        return htmlReport;
    }

    // тип организации
    private OrganizationTypeModify organizationTypeModify;

    private final OrganizationTypeModifyMenu organizationTypeModifyMenu = new OrganizationTypeModifyMenu();

    public OrganizationTypeModify getOrganizationTypeModify() {
        return organizationTypeModify;
    }

    public void setOrganizationTypeModify(OrganizationTypeModify organizationTypeModify) {
        this.organizationTypeModify = organizationTypeModify;
    }

    public OrganizationTypeModifyMenu getOrganizationTypeModifyMenu() {
        return organizationTypeModifyMenu;
    }

    public boolean validateFormData() {
        if (startDate == null) {
            printError("Не указана Начальная дата");
            return true;
        }
        if (endDate == null) {
            printError("Не указана Конечная дата");
            return true;
        }

        if (startDate != null) {
            if (startDate.after(endDate)) {
                printError("Начальная дата меньше Конечной даты");
                return true;
            }
        }

        if (organizationTypeModify != null && organizationTypeModify.toString().equals("")) {
            if (idOfOrgList.isEmpty()) {
                printError("Выберите Организации или Тип организации");
                return true;
            }
        } else {
            Session session = RuntimeContext.getInstance().createReportPersistenceSession();
            idOfOrgList = getOrgsByOrgType(session, findOrganizationType(organizationTypeModify.name()));
            if (idOfOrgList.isEmpty()) {
                printError("Не найдено ни одной организации с Типом организации: " + organizationTypeModify.toString());
                return true;
            }
        }

        return false;
    }

    public ClientPaymentsReport buildReport() {
        if (validateFormData()) {
            return null;
        }

        BasicReportJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "ClientPaymentsReport.jasper";

        ClientPaymentsBuilder builder = new ClientPaymentsBuilder(templateFilename);
        String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        builder.getReportProperties().setProperty("organizationTypeModify", organizationTypeModify.name());
        builder.getReportProperties().setProperty("organizationNames", getFilter());
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Failed build ClientPaymentReport", e);
            printError("Ошибка при построение отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return (ClientPaymentsReport) report;
    }

    public Object buildReportHTML() {
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
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
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при построении отчета:", e);
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition",
                        "inline;filename=detailedDeviationsWithoutCorpsNewReport.xls");
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.close();
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выгрузке отчета:", e);
        }
    }

    @Override
    public String getPageFilename() {
        return "report/online/client_payments_report";
    }

    public void fill() throws Exception {
    }

    @Override
    public String getFilter() {
        return super.getFilter();
    }

    private List<Long> getOrgsByOrgType(Session session, OrganizationType organizationType) {
        Org org;
        List<Long> orgIds = new ArrayList<Long>();
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("type", organizationType));
        List orgList = criteria.list();
        if (!orgList.isEmpty()) {
            for (Object orgObject : orgList) {
                org = (Org) orgObject;
                orgIds.add(org.getIdOfOrg());
            }
        }
        return orgIds;
    }

    //нахождение - типа организации
    private OrganizationType findOrganizationType(String organizationTypeModify) {
        OrganizationType[] organizationTypes = OrganizationType.values();

        for (OrganizationType orgType : organizationTypes) {
            if (orgType.name().equals(organizationTypeModify)) {
                return orgType;
            }
        }
        return null;
    }
}
