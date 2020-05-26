/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.orghardware.HardwareSettingsReport;
import ru.axetta.ecafe.processor.core.report.orghardware.HardwareSettingsReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class HardwareSettingsReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {

    private Integer status = 0;
    private List<SelectItem> statuses;
    private List<HardwareSettingsReportItem> items = Collections.emptyList();
    private List<SelectItem> listOfOrgDistricts;
    private String selectedDistricts = "";

    private Boolean allFriendlyOrgs = true;

    private Boolean showAdministrator = true;
    private Boolean showCashier = true;
    private Boolean showGuard = true;
    private Boolean showInfo = true;
    private Boolean showTurnstile = true;

    private final Logger logger = LoggerFactory.getLogger(HardwareSettingsReportPage.class);

    private List<SelectItem> buildStatuses() {
        List<SelectItem> items = new ArrayList<SelectItem>(3);
        items.add(new SelectItem(0, "Все"));
        items.add(new SelectItem(1, "Обслуживается"));
        items.add(new SelectItem(2, "Не обслуживается"));
        return items;
    }

    private List<SelectItem> buildListOfOrgDistricts(Session session) {
        List<String> allDistricts = null;
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem("", "Все"));
        try {
            allDistricts = DAOUtils.getAllDistinctDepartmentsFromOrgs(session);

            for (String district : allDistricts) {
                selectItemList.add(new SelectItem(district, district));
            }
        } catch (Exception e) {
            logger.error("Cant build Districts items", e);
        }
        return selectItemList;
    }

    @Override
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            statuses = buildStatuses();
            listOfOrgDistricts = buildListOfOrgDistricts(session);
            items.clear();
        } catch (Exception e) {
            logger.error("Exception when prepared the OrgSettingsPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void buildHTML() {
        Session persistenceSession = null;
        Transaction transaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = persistenceSession.beginTransaction();

            List<String> idOfOrgListString = Arrays.asList(StringUtils.split(getGetStringIdOfOrgList(), ','));
            List<Long> idOfOrgList = new ArrayList<Long>(idOfOrgListString.size());
            for (String item : idOfOrgListString) {
                idOfOrgList.add(Long.parseLong(item));
            }

            items = HardwareSettingsReport.Builder
                    .buildOrgHardwareCollection(idOfOrgList, status, persistenceSession, selectedDistricts,
                            allFriendlyOrgs);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't build HTML report: ", e);
            printError("Не удалось построить отчет: " + e.getMessage());
        } finally {
            HibernateUtils.close(persistenceSession, logger);
            HibernateUtils.rollback(transaction, logger);
        }
    }

    public void buildXLS(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            startDate = new Date();

            HardwareSettingsReport.Builder builder = new HardwareSettingsReport.Builder();

            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.LIST_OF_ORG_IDS_PARAM, getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_STATUS_PARAM, status.toString());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_DISTRICT_PARAM, selectedDistricts);
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_ADMINISTRATOR_PARAM, showAdministrator.toString());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_CASHIER_PARAM,showCashier.toString());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_GUARD_PARAM,showGuard.toString());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.SELECTED_TURNSTILES_PARAM,showTurnstile.toString());
            builder.getReportProperties().setProperty(HardwareSettingsReport.Builder.ALL_FRIENDLY_ORGS, allFriendlyOrgs.toString());

            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
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
        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                        .getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=org_settings_report.xls");
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        }
    }

    @Override
    public String getPageFilename() {
        return "service/hardware_settings_report";
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SelectItem> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SelectItem> statuses) {
        this.statuses = statuses;
    }

    public List<HardwareSettingsReportItem> getItems() {
        return items;
    }

    public void setItems(List<HardwareSettingsReportItem> items) {
        this.items = items;
    }

    public List<SelectItem> getListOfOrgDistricts() {
        return listOfOrgDistricts;
    }

    public void setListOfOrgDistricts(List<SelectItem> listOfOrgDistricts) {
        this.listOfOrgDistricts = listOfOrgDistricts;
    }

    public String getSelectedDistricts() {
        return selectedDistricts;
    }

    public void setSelectedDistricts(String selectedDistricts) {
        this.selectedDistricts = selectedDistricts;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public Boolean getShowAdministrator() {
        return showAdministrator;
    }

    public void setShowAdministrator(Boolean showAdministrator) {
        this.showAdministrator = showAdministrator;
    }

    public Boolean getShowCashier() {
        return showCashier;
    }

    public void setShowCashier(Boolean showCashier) {
        this.showCashier = showCashier;
    }

    public Boolean getShowGuard() {
        return showGuard;
    }

    public void setShowGuard(Boolean showGuard) {
        this.showGuard = showGuard;
    }

    public Boolean getShowTurnstile() {
        return showTurnstile;
    }

    public void setShowTurnstile(Boolean showTurnstile) {
        this.showTurnstile = showTurnstile;
    }

    public Boolean getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(Boolean showInfo) {
        this.showInfo = showInfo;
    }
}
