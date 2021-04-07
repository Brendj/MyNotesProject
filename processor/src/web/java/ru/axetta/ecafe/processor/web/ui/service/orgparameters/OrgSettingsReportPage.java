/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingManager;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.ARMsSettingsType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.orgparameters.OrgSettingsReport;
import ru.axetta.ecafe.processor.core.report.orgparameters.OrgSettingsReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.collections.CollectionUtils;
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
public class OrgSettingsReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList{

    private final Logger logger = LoggerFactory.getLogger(OrgSettingsReportPage.class);

    private Integer status = 0;
    private Long numOfChangedRecords = 0L;
    private List<SelectItem> statuses;
    private List<OrgSettingsReportItem> items = Collections.emptyList();
    private List<SelectItem> listOfOrgDistricts;
    private String selectedDistricts = "";

    private Boolean showRequisite = false;
    private Boolean showFeedingSettings = true;
    private Boolean showCardSettings = true;
    private Boolean showOtherSetting = true;
    private Boolean allFriendlyOrgs = true;

    private Boolean allUseWebArm = true;
    private Boolean allUsePaydableSubscriptionFeeding = true;
    private Boolean allVariableFeeding = true;
    private Boolean allPreordersEnabled = true;
    private Boolean allReverseMonthOfSale = true;
    private Boolean allDenyPayPlanForTimeDifference = true;
    private Boolean allOneActiveCard = true;
    private Boolean allEnableDuplicateCard = true;
    private Boolean allMultiCardModeEnabled = true;
    private Boolean allNeedVerifyCardSign = true;
    private Boolean allRequestForVisitsToOtherOrg = true;
    private Boolean allIsWorkInSummerTime = true;

    private void resetSelectedColumns() {
        allUseWebArm = true;
        allUsePaydableSubscriptionFeeding = true;
        allVariableFeeding = true;
        allPreordersEnabled = true;
        allReverseMonthOfSale = true;
        allDenyPayPlanForTimeDifference = true;
        allOneActiveCard = true;
        allEnableDuplicateCard = true;
        allMultiCardModeEnabled = true;
        allNeedVerifyCardSign = true;
        allRequestForVisitsToOtherOrg = true;
        allIsWorkInSummerTime = true;
    }

    private void processSelectedColumns(List<OrgSettingsReportItem> items) {
        resetSelectedColumns();
        if(CollectionUtils.isEmpty(items)){
            return;
        }
        for(OrgSettingsReportItem item : items){
            allUseWebArm &= item.getUseWebArm();
            allUsePaydableSubscriptionFeeding &= item.getUsePaydableSubscriptionFeeding();
            allVariableFeeding &= item.getVariableFeeding();
            allPreordersEnabled &= item.getPreordersEnabled();
            allReverseMonthOfSale &= item.getReverseMonthOfSale();
            allDenyPayPlanForTimeDifference &= item.getDenyPayPlanForTimeDifference();
            allOneActiveCard &= item.getOneActiveCard();
            allEnableDuplicateCard &= item.getEnableDuplicateCard();
            allMultiCardModeEnabled &= item.getMultiCardModeEnabled();
            allNeedVerifyCardSign &= item.getNeedVerifyCardSign();
            allRequestForVisitsToOtherOrg &= item.getRequestForVisitsToOtherOrg();
            allIsWorkInSummerTime &= item.getIsWorkInSummerTime();
        }
    }

    private List<SelectItem> buildStatuses() {
        List<SelectItem> items = new ArrayList<SelectItem>(3);
        items.add(new SelectItem(0, "Все"));
        items.add(new SelectItem(1, "Обслуживается"));
        items.add(new SelectItem(2, "Не обслуживается"));
        return items;
    }

    private List<SelectItem> buildListOfOrgDistricts(Session session) {
        List<String> allDistricts = null;
        List<SelectItem> selectItemList = new LinkedList<>();
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
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            statuses = buildStatuses();
            listOfOrgDistricts = buildListOfOrgDistricts(session);
            items.clear();
        } catch (Exception e){
            logger.error("Exception when prepared the OrgSettingsPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void countChangedRows(){
        numOfChangedRecords = 0L;
        if(CollectionUtils.isEmpty(items)){
            return;
        }
        for(OrgSettingsReportItem item : items){
            if(item.getChanged()){
                numOfChangedRecords++;
            }
        }
    }

    public void doMarkAll(Long index){
        if(CollectionUtils.isEmpty(items)){
            return;
        }
        int i = index.intValue();
        for(OrgSettingsReportItem item : items){
            switch (i){
                case 0:
                    item.setUseWebArm(allUseWebArm);
                    break;
                case 1:
                    item.setUsePaydableSubscriptionFeeding(allUsePaydableSubscriptionFeeding);
                    break;
                case 2:
                    item.setVariableFeeding(allVariableFeeding);
                    break;
                case 3:
                    item.setPreordersEnabled(allPreordersEnabled);
                    break;
                case 4:
                    item.setReverseMonthOfSale(allReverseMonthOfSale);
                    break;
                case 5:
                    item.setDenyPayPlanForTimeDifference(allDenyPayPlanForTimeDifference);
                    break;
                case 6:
                    item.setOneActiveCard(allOneActiveCard);
                    break;
                case 7:
                    item.setEnableDuplicateCard(allEnableDuplicateCard);
                    break;
                case 8:
                    item.setMultiCardModeEnabled(allMultiCardModeEnabled);
                    break;
                case 9:
                    item.setNeedVerifyCardSign(allNeedVerifyCardSign);
                    break;
                case 10:
                    item.setRequestForVisitsToOtherOrg(allRequestForVisitsToOtherOrg);
                    break;
                case 11:
                    item.setIsWorkInSummerTime(allIsWorkInSummerTime);
                    break;
            }
            item.change();
        }
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
            List<Long> idOfOrgList = new ArrayList<>(idOfOrgListString.size());
            for (String item : idOfOrgListString) {
                idOfOrgList.add(Long.parseLong(item));
            }

            items = OrgSettingsReport.Builder.buildOrgSettingCollection(idOfOrgList, status, persistenceSession, selectedDistricts, allFriendlyOrgs);
            Collections.sort(items);
            processSelectedColumns(items);

            transaction.commit();
            transaction = null;
        } catch (Exception e){
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

            OrgSettingsReport.Builder builder = new OrgSettingsReport.Builder();

            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.LIST_OF_ORG_IDS_PARAM, getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SELECTED_STATUS_PARAM, status.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SELECTED_DISTRICT_PARAM, selectedDistricts);
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_REQUISITE, showRequisite.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_FEEDING_SETTINGS, showFeedingSettings.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_CARD_SETTINGS, showCardSettings.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.SHOW_OTHER_SETTINGS, showOtherSetting.toString());
            builder.getReportProperties().setProperty(OrgSettingsReport.Builder.ALL_FRIENDLY_ORGS, allFriendlyOrgs.toString());

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
            printError("Для применения настроек необходимо выгрузить в таблицу хотя бы 1 ОО");
            return;
        }

        Session session = null;
        Transaction transaction = null;
        List<Long> problemOrgsId = new LinkedList<>();
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

                    Long lastVersionOfOrgSetting = OrgSettingDAOUtils.getLastVersionOfOrgSettings(session);
                    Long lastVersionOfOrgSettingItem = OrgSettingDAOUtils.getLastVersionOfOrgSettingsItem(session);

                    org.setUsePaydableSubscriptionFeeding(item.getUsePaydableSubscriptionFeeding());
                    org.setVariableFeeding(item.getVariableFeeding());
                    org.setPreordersEnabled(item.getPreordersEnabled());
                    manager.createOrUpdateOrgSettingValue(org, ARMsSettingsType.REVERSE_MONTH_OF_SALE, item.getReverseMonthOfSale(),
                            session, lastVersionOfOrgSetting, lastVersionOfOrgSettingItem);
                    org.setDenyPayPlanForTimeDifference(item.getDenyPayPlanForTimeDifference());

                    if(!item.getUseWebArm().equals(org.getUseWebArm())){
                        org.setUseWebArm(item.getUseWebArm());
                        for(Org friendlyOrg : org.getFriendlyOrg()){
                            if(friendlyOrg.equals(org)){
                                continue;
                            }
                            friendlyOrg.setUseWebArm(item.getUseWebArm());
                        }
                    }

                    org.setOneActiveCard(item.getOneActiveCard());
                    manager.createOrUpdateOrgSettingValue(org, ARMsSettingsType.CARD_DUPLICATE_ENABLED, item.getEnableDuplicateCard(), session,
                            lastVersionOfOrgSetting, lastVersionOfOrgSettingItem);
                    org.setNeedVerifyCardSign(item.getNeedVerifyCardSign());
                    if (!item.getMultiCardModeEnabled() && org.multiCardModeIsEnabled()) {
                        ClientManager.resetMultiCardModeToAllClientsAndBlockCardsAndUpRegVersion(org, session);
                    }
                    org.setMultiCardModeEnabled(item.getMultiCardModeEnabled());

                    org.setRequestForVisitsToOtherOrg(item.getRequestForVisitsToOtherOrg());
                    org.setIsWorkInSummerTime(item.getIsWorkInSummerTime());

                    org.setOrgStructureVersion(nextOrgVersion);
                    org.setOrgSettingsSyncParam(Boolean.TRUE);
                    org.setGovernmentContract(item.getGovernmentContract());
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
            } else {
                printMessage("Новые настройки применены успешно");
            }

            transaction.commit();
            transaction = null;
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

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    public Boolean getAllUseWebArm() {
        return allUseWebArm;
    }

    public void setAllUseWebArm(Boolean allUseWebArm) {
        this.allUseWebArm = allUseWebArm;
    }

    public Boolean getAllUsePaydableSubscriptionFeeding() {
        return allUsePaydableSubscriptionFeeding;
    }

    public void setAllUsePaydableSubscriptionFeeding(Boolean allUsePaydableSubscriptionFeeding) {
        this.allUsePaydableSubscriptionFeeding = allUsePaydableSubscriptionFeeding;
    }

    public Boolean getAllVariableFeeding() {
        return allVariableFeeding;
    }

    public void setAllVariableFeeding(Boolean allVariableFeeding) {
        this.allVariableFeeding = allVariableFeeding;
    }

    public Boolean getAllPreordersEnabled() {
        return allPreordersEnabled;
    }

    public void setAllPreordersEnabled(Boolean allPreordersEnabled) {
        this.allPreordersEnabled = allPreordersEnabled;
    }

    public Boolean getAllReverseMonthOfSale() {
        return allReverseMonthOfSale;
    }

    public void setAllReverseMonthOfSale(Boolean allReverseMonthOfSale) {
        this.allReverseMonthOfSale = allReverseMonthOfSale;
    }

    public Boolean getAllDenyPayPlanForTimeDifference() {
        return allDenyPayPlanForTimeDifference;
    }

    public void setAllDenyPayPlanForTimeDifference(Boolean allDenyPayPlanForTimeDifference) {
        this.allDenyPayPlanForTimeDifference = allDenyPayPlanForTimeDifference;
    }

    public Boolean getAllOneActiveCard() {
        return allOneActiveCard;
    }

    public void setAllOneActiveCard(Boolean allOneActiveCard) {
        this.allOneActiveCard = allOneActiveCard;
    }

    public Boolean getAllEnableDuplicateCard() {
        return allEnableDuplicateCard;
    }

    public void setAllEnableDuplicateCard(Boolean allEnableDuplicateCard) {
        this.allEnableDuplicateCard = allEnableDuplicateCard;
    }

    public Boolean getAllMultiCardModeEnabled() {
        return allMultiCardModeEnabled;
    }

    public void setAllMultiCardModeEnabled(Boolean allMultiCardModeEnabled) {
        this.allMultiCardModeEnabled = allMultiCardModeEnabled;
    }

    public Boolean getAllNeedVerifyCardSign() {
        return allNeedVerifyCardSign;
    }

    public void setAllNeedVerifyCardSign(Boolean allNeedVerifyCardSign) {
        this.allNeedVerifyCardSign = allNeedVerifyCardSign;
    }

    public Boolean getAllRequestForVisitsToOtherOrg() {
        return allRequestForVisitsToOtherOrg;
    }

    public void setAllRequestForVisitsToOtherOrg(Boolean allRequestForVisitsToOtherOrg) {
        this.allRequestForVisitsToOtherOrg = allRequestForVisitsToOtherOrg;
    }

    public Boolean getAllIsWorkInSummerTime() {
        return allIsWorkInSummerTime;
    }

    public void setAllIsWorkInSummerTime(Boolean allIsWorkInSummerTime) {
        this.allIsWorkInSummerTime = allIsWorkInSummerTime;
    }

    public Long getNumOfChangedRecords() {
        return numOfChangedRecords;
    }

    public void setNumOfChangedRecords(Long numOfChangedRecords) {
        this.numOfChangedRecords = numOfChangedRecords;
    }
}
