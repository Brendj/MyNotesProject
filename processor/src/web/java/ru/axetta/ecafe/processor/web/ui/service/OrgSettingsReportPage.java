/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingManager;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.ARMsSettingsType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.OrgSettingsReportItem;
import ru.axetta.ecafe.processor.core.report.OrgSettingsReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@Scope("session")
public class OrgSettingsReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList{

    private Integer status = 0;
    private List<SelectItem> statuses;
    private List<OrgSettingsReportItem> items;
    private List<SelectItem> listOfOrgDistricts;
    private String selectedDistricts = "";

    private Boolean showRequisite = true;
    private Boolean showFeedingSettings = true;
    private Boolean showCardSettings = true;
    private Boolean showOtherSetting = true;

    private final Logger logger = LoggerFactory.getLogger(OrgSettingsReportPage.class);

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
        try{
            allDistricts = DAOUtils.getAllDistinctDepartmentsFromOrgs(session);

            for(String district : allDistricts){
                selectItemList.add(new SelectItem(district, district));
            }
        } catch (Exception e){
            logger.error("Cant build Districts items", e);
        }
        return selectItemList;
    }

    @Override
    public void fill(Session session) throws Exception {
        statuses = buildStatuses();
        listOfOrgDistricts = buildListOfOrgDistricts(session);
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

            items = OrgSettingsReport.Builder.buildOrgSettingCollection(idOfOrgList, status, persistenceSession, selectedDistricts);

            transaction = null;
        } catch (Exception e){
            logger.error("Can't build HTML report: ", e);
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

            OrgSettingsReport.Builder builder = new OrgSettingsReport.Builder();

            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.LIST_OF_ORG_IDS_PARAM, getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SELECTED_STATUS_PARAM, status.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SELECTED_DISTRICT_PARAM, selectedDistricts);
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_REQUISITE, showRequisite.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_FEEDING_SETTINGS, showFeedingSettings.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_CARD_SETTINGS, showCardSettings.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_OTHER_SETTINGS, showOtherSetting.toString());

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

    public void applyChanges() {
        if(CollectionUtils.isEmpty(items)){
            return;
        }

        Session session = null;
        Transaction transaction = null;
        List<Long> problemOrgsId = new LinkedList<Long>();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            OrgSettingManager manager = RuntimeContext.getAppContext().getBean(OrgSettingManager.class);

            Long nextOrgVersion = DAOUtils.nextVersionByOrgStucture(session);
            for (OrgSettingsReportItem item : items){
                if(!item.getChanged()) {
                    continue;
                }
                try {
                    logger.info("Try apply settings for Org ID: " + item.getIdOfOrg());
                    Org org = (Org) session.load(Org.class, item.getIdOfOrg());

                    org.setUsePaydableSubscriptionFeeding(item.getUsePaydableSubscriptionFeeding());
                    org.setVariableFeeding(item.getVariableFeeding());
                    org.setPreordersEnabled(item.getPreordersEnabled());
                    manager.createOrUpdateOrgSettingValue(org, ARMsSettingsType.REVERSE_MONTH_OF_SALE, item.getReverseMonthOfSale().toString(), session);
                    org.setDenyPayPlanForTimeDifference(item.getDenyPayPlanForTimeDifference());

                    org.setOneActiveCard(item.getOneActiveCard());
                    manager.createOrUpdateOrgSettingValue(org, ARMsSettingsType.CARD_DUPLICATE_ENABLED, item.getEnableDuplicateCard().toString(), session);
                    org.setNeedVerifyCardSign(item.getNeedVerifyCardSign());
                    if (!item.getMultiCardModeEnabled() && org.multiCardModeIsEnabled()) {
                        ClientManager.resetMultiCardModeToAllClientsAndBlockCardsAndUpRegVersion(org, session);
                    }
                    org.setMultiCardModeEnabled(item.getMultiCardModeEnabled());

                    org.setRequestForVisitsToOtherOrg(item.getRequestForVisitsToOtherOrg());
                    org.setIsWorkInSummerTime(item.getIsWorkInSummerTime());

                    org.setOrgStructureVersion(nextOrgVersion);
                    session.update(org);

                    logger.info("Success");
                    item.setChanged(false);
                } catch (Exception e) {
                    logger.error("Exception when try change Org settings ID: " + item.getIdOfOrg(), e);
                    problemOrgsId.add(item.getIdOfOrg());
                    printMessage("Не удалось установить новые значения для ID OO " + item.getIdOfOrg());
                }
            }

            if(!problemOrgsId.isEmpty()){
                String problemOrgsIdString = StringUtils.join(problemOrgsId, ", ");
                logger.warn("Can't apply settings for next Org (IDs) : " + problemOrgsIdString);
                printWarn("Не удалось установить новые значения для следующих ID OO: " + problemOrgsIdString);
            }
            transaction.commit();
        }catch (Exception e){
            logger.error("Can't apply settings ", e);
            printError("Не удалось запустить процедуру установки новых значений настроек: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<OrgSettingsReportItem> getItems() {
        if(items == null){
            return Collections.emptyList();
        }
        return items;
    }

    public void setItems(List<OrgSettingsReportItem> items) {
        this.items = items;
    }

    @Override
    public String getPageFilename() {
        return "service/org_settings_report";
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    public Boolean getShowRequisite() {
        return showRequisite;
    }

    public void setShowRequisite(Boolean showRequisite) {
        this.showRequisite = showRequisite;
    }

    public Boolean getShowFeedingSettings() {
        return showFeedingSettings;
    }

    public void setShowFeedingSettings(Boolean showFeedingSettings) {
        this.showFeedingSettings = showFeedingSettings;
    }

    public Boolean getShowCardSettings() {
        return showCardSettings;
    }

    public void setShowCardSettings(Boolean showCardSettings) {
        this.showCardSettings = showCardSettings;
    }

    public Boolean getShowOtherSetting() {
        return showOtherSetting;
    }

    public void setShowOtherSetting(Boolean showOtherSetting) {
        this.showOtherSetting = showOtherSetting;
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
}
