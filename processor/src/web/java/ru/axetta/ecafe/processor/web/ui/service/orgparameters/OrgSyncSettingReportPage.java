/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ConcreteTime;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.orgparameters.OrgSyncSettingReport;
import ru.axetta.ecafe.processor.core.report.orgparameters.OrgSyncSettingReportItem;
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

import javax.faces.model.SelectItem;
import java.util.*;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class OrgSyncSettingReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {
    private static final Logger logger = LoggerFactory.getLogger(OrgSyncSettingReportPage.class);

    private Boolean allFriendlyOrgs = false;
    private List<SelectItem> listOfOrgDistricts;
    private List<SelectItem> listOfContentType;
    private List<SelectItem> modalListOfContentType;
    private String selectedDistricts = "";
    private Integer selectedContentType = OrgSyncSettingReport.ALL_TYPES;
    private Integer modalSelectedContentType = ContentType.FULL_SYNC.getTypeCode();
    private List<OrgSyncSettingReportItem> items = new LinkedList<>();
    private OrgSyncSettingReportItem selectedItem = null;

    private List<SelectItem> buildListOfOrgDistricts(Session session) {
        List<String> allDistricts;
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem("", "Все"));
        try{
            allDistricts = DAOUtils.getAllDistinctDepartmentsFromOrgs(session);
            for(String district : allDistricts){
                selectItemList.add(new SelectItem(district, district));
            }
        } catch (Exception e){
            logger.error("Can't build Districts items", e);
        }
        return selectItemList;
    }

    @Override
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            listOfOrgDistricts = buildListOfOrgDistricts(session);
            listOfContentType = buildListOfContentType();
            modalListOfContentType = buildModalListOfContentType();
            items.clear();
        } catch (Exception e){
            logger.error("Exception when prepared the OrgSyncSettingsPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private List<SelectItem> buildModalListOfContentType() {
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem(ContentType.FULL_SYNC.getTypeCode(), ContentType.FULL_SYNC.toString()));
        selectItemList.add(new SelectItem(ContentType.BALANCES_AND_ENTEREVENTS.getTypeCode(), ContentType.BALANCES_AND_ENTEREVENTS.toString()));
        selectItemList.add(new SelectItem(ContentType.ORGSETTINGS.getTypeCode(), ContentType.ORGSETTINGS.toString()));
        selectItemList.add(new SelectItem(ContentType.CLIENTS_DATA.getTypeCode(), ContentType.CLIENTS_DATA.toString()));
        selectItemList.add(new SelectItem(ContentType.MENU.getTypeCode(), ContentType.MENU.toString()));
        selectItemList.add(new SelectItem(ContentType.PHOTOS.getTypeCode(), ContentType.PHOTOS.toString()));
        selectItemList.add(new SelectItem(ContentType.SUPPORT_SERVICE.getTypeCode(), ContentType.SUPPORT_SERVICE.toString()));
        selectItemList.add(new SelectItem(ContentType.LIBRARY.getTypeCode(), ContentType.LIBRARY.toString()));
        return selectItemList;
    }

    private List<SelectItem> buildListOfContentType() {
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem(OrgSyncSettingReport.ALL_TYPES, "Все"));
        selectItemList.add(new SelectItem(ContentType.FULL_SYNC.getTypeCode(), ContentType.FULL_SYNC.toString()));
        selectItemList.add(new SelectItem(ContentType.BALANCES_AND_ENTEREVENTS.getTypeCode(), ContentType.BALANCES_AND_ENTEREVENTS.toString()));
        selectItemList.add(new SelectItem(ContentType.ORGSETTINGS.getTypeCode(), ContentType.ORGSETTINGS.toString()));
        selectItemList.add(new SelectItem(ContentType.CLIENTS_DATA.getTypeCode(), ContentType.CLIENTS_DATA.toString()));
        selectItemList.add(new SelectItem(ContentType.MENU.getTypeCode(), ContentType.MENU.toString()));
        selectItemList.add(new SelectItem(ContentType.PHOTOS.getTypeCode(), ContentType.PHOTOS.toString()));
        selectItemList.add(new SelectItem(ContentType.SUPPORT_SERVICE.getTypeCode(), ContentType.SUPPORT_SERVICE.toString()));
        selectItemList.add(new SelectItem(ContentType.LIBRARY.getTypeCode(), ContentType.LIBRARY.toString()));
        return selectItemList;
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

            items = OrgSyncSettingReport.getInstance().getBuilder().buildOrgSettingCollection(
                    idOfOrgList, persistenceSession, selectedDistricts, allFriendlyOrgs, selectedContentType);
            Collections.sort(items);

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
        /*RuntimeContext runtimeContext = RuntimeContext.getInstance();
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
        }*/
    }

    @Override
    public String getPageFilename() {
        return "service/org_sync_settings";
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
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

    public List<OrgSyncSettingReportItem> getItems() {
        return items;
    }

    public void setItems(List<OrgSyncSettingReportItem> items) {
        this.items = items;
    }

    public Object applyChanges() {
        return null;
    }

    public List<SelectItem> getListOfContentType() {
        return listOfContentType;
    }

    public void setListOfContentType(List<SelectItem> listOfContentType) {
        this.listOfContentType = listOfContentType;
    }

    public Integer getSelectedContentType() {
        return selectedContentType;
    }

    public void setSelectedContentType(Integer selectedContentType) {
        this.selectedContentType = selectedContentType;
    }

    public boolean getShowColumnFull() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.FULL_SYNC.getTypeCode());
    }

    public boolean getShowColumnBalance() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.BALANCES_AND_ENTEREVENTS.getTypeCode());
    }

    public boolean getShowColumnOrgSettings() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.ORGSETTINGS.getTypeCode());
    }

    public boolean getShowColumnClientData() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.CLIENTS_DATA.getTypeCode());
    }

    public boolean getShowColumnMenu() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.MENU.getTypeCode());
    }

    public boolean getShowColumnPhoto() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.PHOTOS.getTypeCode());
    }

    public boolean getShowColumnSupport() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.SUPPORT_SERVICE.getTypeCode());
    }

    public boolean getShowColumnLib() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ContentType.LIBRARY.getTypeCode());
    }

    public OrgSyncSettingReportItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(OrgSyncSettingReportItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public Object resetChanges() {
        return null;
    }

    public List<SelectItem> getModalListOfContentType() {
        return modalListOfContentType;
    }

    public void setModalListOfContentType(List<SelectItem> modalListOfContentType) {
        this.modalListOfContentType = modalListOfContentType;
    }

    public Integer getModalSelectedContentType() {
        return modalSelectedContentType;
    }

    public void setModalSelectedContentType(Integer modalSelectedContentType) {
        this.modalSelectedContentType = modalSelectedContentType;
    }

    class EditedSetting{
        private Integer everySecond;
        private Integer limitStartHour;
        private Integer limitEndHour;
        private Boolean monday = false;
        private Boolean tuesday = false;
        private Boolean wednesday = false;
        private Boolean thursday = false;
        private Boolean friday = false;
        private Boolean saturday = false;
        private Boolean sunday = false;
        private Long version;
        private Boolean deleteState = false;
        private Set<ConcreteTime> concreteTime = new HashSet<>();
        private Date createdDate;
        private Date lastUpdate;
    }
}
