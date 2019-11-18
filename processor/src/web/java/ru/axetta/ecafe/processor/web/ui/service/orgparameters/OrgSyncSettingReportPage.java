/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettingManager;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.autodistribution.AutoDistributionSyncSettingsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
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

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType.*;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class OrgSyncSettingReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {
    private static final Logger logger = LoggerFactory.getLogger(OrgSyncSettingReportPage.class);
    private static final Pattern TIME_PATTERN = Pattern.compile("([0-1][0-9]|2[0-3]):[0-5][0-9]");

    private Boolean allFriendlyOrgs = false;
    private List<SelectItem> listOfOrgDistricts;
    private List<SelectItem> listOfContentType;
    private List<SelectItem> modalListOfContentType;
    private String selectedDistricts = "";
    private Integer selectedContentType = OrgSyncSettingReport.ALL_TYPES;
    private Integer modalSelectedContentType = FULL_SYNC.getTypeCode();
    private List<OrgSyncSettingReportItem> items = new LinkedList<>();
    private OrgSyncSettingReportItem selectedItem = null;
    private EditedSetting editedSetting = new EditedSetting();
    private Boolean runEverySecond = false;
    private Boolean showConcreteTime2 = false;
    private Boolean showConcreteTime3 = false;
    private Boolean newSyncSetting = false;
    private Boolean showSettingEnable = false;
    private Boolean settingEnable = true;

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
        selectItemList.add(new SelectItem(FULL_SYNC.getTypeCode(), FULL_SYNC.toString()));
        selectItemList.add(new SelectItem(BALANCES_AND_ENTEREVENTS.getTypeCode(), BALANCES_AND_ENTEREVENTS.toString()));
        selectItemList.add(new SelectItem(ORGSETTINGS.getTypeCode(), ORGSETTINGS.toString()));
        selectItemList.add(new SelectItem(CLIENTS_DATA.getTypeCode(), CLIENTS_DATA.toString()));
        selectItemList.add(new SelectItem(MENU.getTypeCode(), MENU.toString()));
        selectItemList.add(new SelectItem(PHOTOS.getTypeCode(), PHOTOS.toString()));
        selectItemList.add(new SelectItem(SUPPORT_SERVICE.getTypeCode(), SUPPORT_SERVICE.toString()));
        selectItemList.add(new SelectItem(LIBRARY.getTypeCode(), LIBRARY.toString()));
        return selectItemList;
    }

    private List<SelectItem> buildListOfContentType() {
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem(OrgSyncSettingReport.ALL_TYPES, "Все"));
        selectItemList.addAll( buildModalListOfContentType());
        return selectItemList;
    }

    public void buildHTML() {
        resetParams();

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
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            startDate = new Date();

            OrgSyncSettingReport.Builder builder = new OrgSyncSettingReport.Builder();
            builder.getReportProperties().setProperty(OrgSyncSettingReport.Builder.LIST_OF_ORG_IDS_PARAM, getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty(OrgSyncSettingReport.Builder.SELECTED_DISTRICT_PARAM, selectedDistricts);
            builder.getReportProperties().setProperty(OrgSyncSettingReport.Builder.ALL_FRIENDLY_ORGS, allFriendlyOrgs.toString());
            builder.getReportProperties().setProperty(OrgSyncSettingReport.Builder.SELECTED_CONTENT_TYPE, selectedContentType.toString());

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
                response.setHeader("Content-disposition", "inline;filename=sync_settings_report.xls");
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

    public void buildEditedItem() {
        settingEnable = true;
        showSettingEnable = false;
        SyncSetting currentSetting = findBySelectedModalType(selectedItem, modalSelectedContentType);
        if(currentSetting == null || currentSetting.getIdOfSyncSetting() == null){
            editedSetting = new EditedSetting();
            newSyncSetting = true;
        } else {
            editedSetting = new EditedSetting(currentSetting);
            newSyncSetting = false;
        }
        runEverySecond = modalSelectedContentType.equals(BALANCES_AND_ENTEREVENTS.getTypeCode())
                || modalSelectedContentType.equals(SUPPORT_SERVICE.getTypeCode());

        showConcreteTime2 = modalSelectedContentType.equals(ORGSETTINGS.getTypeCode())
                || modalSelectedContentType.equals(CLIENTS_DATA.getTypeCode())
                || modalSelectedContentType.equals(MENU.getTypeCode())
                || modalSelectedContentType.equals(PHOTOS.getTypeCode())
                || modalSelectedContentType.equals(LIBRARY.getTypeCode());

        showConcreteTime3 = modalSelectedContentType.equals(CLIENTS_DATA.getTypeCode());
        showSettingEnable = modalSelectedContentType.equals(PHOTOS.getTypeCode())
                || modalSelectedContentType.equals(LIBRARY.getTypeCode());
        settingEnable = (modalSelectedContentType.equals(PHOTOS.getTypeCode()) && StringUtils.isNotBlank(selectedItem.getPhotoSync().getFullInf())
                || modalSelectedContentType.equals(LIBRARY.getTypeCode()) && StringUtils.isNotBlank(selectedItem.getLibSync().getFullInf())) || !showSettingEnable;
    }

    private SyncSetting findBySelectedModalType(OrgSyncSettingReportItem item, Integer modalSelectedContentType) {
        ContentType type = getContentTypeByCode(modalSelectedContentType);
        switch (type){
            case FULL_SYNC:
                return item.getFullSync().getSetting();
            case BALANCES_AND_ENTEREVENTS:
                return item.getAccIncSync().getSetting();
            case ORGSETTINGS:
                return item.getOrgSettingSync().getSetting();
            case CLIENTS_DATA:
                return item.getClientDataSync().getSetting();
            case MENU:
                return item.getMenuSync().getSetting();
            case PHOTOS:
                return item.getPhotoSync().getSetting();
            case SUPPORT_SERVICE:
                return item.getHelpRequestsSync().getSetting();
            case LIBRARY:
                return item.getLibSync().getSetting();
            default:
                return null;
        }
    }

    public void applyChanges() {
        Session session = null;
        Transaction transaction = null;
        SyncSettingManager manager = null;
        try {
            manager = RuntimeContext.getAppContext().getBean(SyncSettingManager.class);
            if(manager == null){
                throw new Exception("Can't get instance of SyncSettingManager");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long nextVersion = SyncSettingManager.getNextVersion(session);
            for(OrgSyncSettingReportItem item : items){
                if(item.getIsChange()){
                    if(item.getFullSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getFullSync().getSetting(), nextVersion);
                    }
                    if(item.getOrgSettingSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getOrgSettingSync().getSetting(), nextVersion);
                    }
                    if(item.getAccIncSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getAccIncSync().getSetting(), nextVersion);
                    }
                    if(item.getClientDataSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getClientDataSync().getSetting(), nextVersion);
                    }
                    if(item.getMenuSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getMenuSync().getSetting(), nextVersion);
                    }
                    if(item.getPhotoSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getPhotoSync().getSetting(), nextVersion);
                    }
                    if(item.getHelpRequestsSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getHelpRequestsSync().getSetting(), nextVersion);
                    }
                    if(item.getLibSync().getState().equals(OrgSyncSettingReportItem.CHANGED)){
                        manager.saveOrUpdateSettingFromReportPage(session, item.getLibSync().getSetting(), nextVersion);
                    }
                }
            }
            transaction.commit();
            transaction = null;
            session.close();

            printMessage("Изменения сохранены");
        } catch (Exception e) {
            logger.error("Cant save SyncSetting to DB: ", e);
            printError("Не удалось сохранить изменения в БД: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        buildHTML();
    }

    public void beginDistributionSyncSettings(){
        try {
            AutoDistributionSyncSettingsService service = RuntimeContext.getAppContext().getBean(AutoDistributionSyncSettingsService.class);
            if(service == null) {
                throw new Exception("Не удалось получить экземпляр сервиса автораспределения");
            }
            for (OrgSyncSettingReportItem item : items) {
                try {
                    if(selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES)){
                        SyncSetting fullSync = service.distributionSyncSettings(item.getFullSync().getSetting());
                        SyncSetting orgSettingSync = service.distributionSyncSettings(item.getOrgSettingSync().getSetting());
                        SyncSetting clientDataSync = service.distributionSyncSettings(item.getClientDataSync().getSetting());
                        SyncSetting menuSync = service.distributionSyncSettings(item.getMenuSync().getSetting());
                        SyncSetting photoSync = service.distributionSyncSettings(item.getPhotoSync().getSetting());
                        SyncSetting libSync = service.distributionSyncSettings(item.getLibSync().getSetting());

                        item.rebuildSyncInfo(fullSync.getContentType().getTypeCode(), fullSync);
                        item.rebuildSyncInfo(orgSettingSync.getContentType().getTypeCode(), orgSettingSync);
                        item.rebuildSyncInfo(clientDataSync.getContentType().getTypeCode(), clientDataSync);
                        item.rebuildSyncInfo(menuSync.getContentType().getTypeCode(), menuSync);
                        item.rebuildSyncInfo(photoSync.getContentType().getTypeCode(), photoSync);
                        item.rebuildSyncInfo(libSync.getContentType().getTypeCode(), libSync);
                    } else {
                        ContentType type = ContentType.getContentTypeByCode(selectedContentType);
                        SyncSetting currentSetting;
                        switch (type) {
                            case FULL_SYNC:
                                currentSetting = service.distributionSyncSettings(item.getFullSync().getSetting());
                                break;
                            case ORGSETTINGS:
                                currentSetting = service.distributionSyncSettings(item.getOrgSettingSync().getSetting());
                                break;
                            case CLIENTS_DATA:
                                currentSetting = service.distributionSyncSettings(item.getClientDataSync().getSetting());
                                break;
                            case MENU:
                                currentSetting = service.distributionSyncSettings(item.getMenuSync().getSetting());
                                break;
                            case PHOTOS:
                                currentSetting = service.distributionSyncSettings(item.getPhotoSync().getSetting());
                                break;
                            case LIBRARY:
                                currentSetting = service.distributionSyncSettings(item.getLibSync().getSetting());
                                break;
                            default:
                                throw new IllegalArgumentException("Not supported type of SyncSetting");
                        }
                        assert currentSetting != null;
                        item.rebuildSyncInfo(currentSetting.getContentType().getTypeCode(), currentSetting);
                    }
                } catch (Exception e) {
                    logger.error(String.format("Can't calculate new sessionTime for ID OO %d", item.getIdOfOrg()), e);
                    printError("Не удалось расчитать новое время сеанса для настроек ID OO " + item.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            logger.error("Can't begin distribute SyncSetting: ", e);
            printError("Не удалось запусть процедуру распределения: " + e.getMessage());
        }
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
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(FULL_SYNC.getTypeCode());
    }

    public boolean getShowColumnBalance() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(
                BALANCES_AND_ENTEREVENTS.getTypeCode());
    }

    public boolean getShowColumnOrgSettings() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(ORGSETTINGS.getTypeCode());
    }

    public boolean getShowColumnClientData() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(CLIENTS_DATA.getTypeCode());
    }

    public boolean getShowColumnMenu() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(MENU.getTypeCode());
    }

    public boolean getShowColumnPhoto() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(PHOTOS.getTypeCode());
    }

    public boolean getShowColumnSupport() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(SUPPORT_SERVICE.getTypeCode());
    }

    public boolean getShowColumnLib() {
        return selectedContentType.equals(OrgSyncSettingReport.ALL_TYPES) || selectedContentType.equals(LIBRARY.getTypeCode());
    }

    public OrgSyncSettingReportItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(OrgSyncSettingReportItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void resetChanges() {
        resetParams();

        if(!items.isEmpty()) {
            items.clear();
            buildHTML();
        }
    }

    private void resetParams() {
        selectedItem = null;
        editedSetting = new EditedSetting();
        runEverySecond = false;
        showConcreteTime2 = false;
        showConcreteTime3 = false;
        newSyncSetting = false;
        showSettingEnable = false;
        settingEnable = true;
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

    public EditedSetting getEditedSetting() {
        return editedSetting;
    }

    public void setEditedSetting(EditedSetting editedSetting) {
        this.editedSetting = editedSetting;
    }

    public Boolean getRunEverySecond() {
        return runEverySecond;
    }

    public void setRunEverySecond(Boolean runEverySecond) {
        this.runEverySecond = runEverySecond;
    }

    public void saveLocalChanges() {
        try {
            validateData();
            SyncSetting currentSetting = findBySelectedModalType(selectedItem, modalSelectedContentType);
            if (currentSetting != null) {
                if(settingEnable) {
                    currentSetting.setMonday(editedSetting.getMonday());
                    currentSetting.setTuesday(editedSetting.getTuesday());
                    currentSetting.setWednesday(editedSetting.getWednesday());
                    currentSetting.setThursday(editedSetting.getThursday());
                    currentSetting.setFriday(editedSetting.getFriday());
                    currentSetting.setSaturday(editedSetting.getSaturday());
                    currentSetting.setSunday(editedSetting.getSunday());
                    currentSetting.setEverySecond(editedSetting.getEverySecond());
                    currentSetting.setConcreteTime(buildTime(editedSetting));
                    currentSetting.setLimitStartHour(editedSetting.getLimitStartHour());
                    currentSetting.setLimitEndHour(editedSetting.getLimitEndHour());
                } else {
                    currentSetting.setDeleteState(true);
                }
                selectedItem.setIsChange(true);
                selectedItem.rebuildSyncInfo(modalSelectedContentType, currentSetting);
            } else {
                if(!settingEnable){
                    selectedItem.setIsChange(false);
                } else {
                    selectedItem.buildSyncInfo(editedSetting.getMonday(), editedSetting.getTuesday(),
                            editedSetting.getWednesday(), editedSetting.getThursday(), editedSetting.getFriday(),
                            editedSetting.getSaturday(), editedSetting.getSunday(), editedSetting.getEverySecond(),
                            buildTime(editedSetting), modalSelectedContentType, editedSetting.getLimitStartHour(),
                            editedSetting.getLimitEndHour());
                    selectedItem.setIsChange(true);
                }
            }
        } catch (Exception e) {
            logger.error("Exception when save local changes: ", e);
            printError("Не удалось сохранить локальные изменения: " + e.getMessage());
        }
    }

    private void validateData() throws Exception {
        if(!settingEnable){
            return;
        }
        if(runEverySecond){
            if(editedSetting.getLimitStartHour() > editedSetting.getLimitEndHour()){
                throw new IllegalArgumentException("Значение начала таймаута не должно быть больше значению конца таймаута");
            }
        } else {
            Matcher m = TIME_PATTERN.matcher(editedSetting.getConcreteTime1());
            if(!m.find()){
                throw new IllegalArgumentException("Не верное значение времени 1-го сеанса");
            }
            if(showConcreteTime2 && StringUtils.isNotBlank(editedSetting.getConcreteTime2())){
                m = TIME_PATTERN.matcher(editedSetting.getConcreteTime2());
                if(!m.find()){
                    throw new IllegalArgumentException("Не верное значение времени 2-го сеанса");
                }
            }
            if(showConcreteTime3 && StringUtils.isNotBlank(editedSetting.getConcreteTime3())){
                m = TIME_PATTERN.matcher(editedSetting.getConcreteTime3());
                if(!m.find()){
                    throw new IllegalArgumentException("Не верное значение времени 3-го сеанса");
                }
            }
        }
    }

    private String buildTime(EditedSetting editedSetting) {
        List<String> as = new LinkedList<>();
        if(StringUtils.isNotBlank(editedSetting.getConcreteTime1())){
            as.add(editedSetting.getConcreteTime1());
        }
        if(StringUtils.isNotBlank(editedSetting.getConcreteTime2())){
            as.add(editedSetting.getConcreteTime2());
        }
        if(StringUtils.isNotBlank(editedSetting.getConcreteTime3())){
            as.add(editedSetting.getConcreteTime3());
        }
        return StringUtils.join(as, SyncSetting.SEPARATOR);
    }

    public Boolean getShowConcreteTime2() {
        return showConcreteTime2;
    }

    public void setShowConcreteTime2(Boolean showConcreteTime2) {
        this.showConcreteTime2 = showConcreteTime2;
    }

    public Boolean getShowConcreteTime3() {
        return showConcreteTime3;
    }

    public void setShowConcreteTime3(Boolean showConcreteTime3) {
        this.showConcreteTime3 = showConcreteTime3;
    }

    public Boolean getNewSyncSetting() {
        return newSyncSetting;
    }

    public void setNewSyncSetting(Boolean newSyncSetting) {
        this.newSyncSetting = newSyncSetting;
    }

    public Boolean getShowSettingEnable() {
        return showSettingEnable;
    }

    public void setShowSettingEnable(Boolean showSettingEnable) {
        this.showSettingEnable = showSettingEnable;
    }

    public Boolean getSettingEnable() {
        return settingEnable;
    }

    public void setSettingEnable(Boolean settingEnable) {
        this.settingEnable = settingEnable;
    }

    public boolean disableDistribution() {
        return selectedContentType.equals(BALANCES_AND_ENTEREVENTS.getTypeCode())
                ||  selectedContentType.equals(SUPPORT_SERVICE.getTypeCode());
    }

    public static class EditedSetting {
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
        private String concreteTime1;
        private String concreteTime2;
        private String concreteTime3;

        public EditedSetting(){
        }

        public EditedSetting(SyncSetting syncSetting){
            boolean setConcreteTime2 = syncSetting.getContentType().equals(ORGSETTINGS)
                || syncSetting.getContentType().equals(CLIENTS_DATA)
                || syncSetting.getContentType().equals(MENU)
                || syncSetting.getContentType().equals(PHOTOS)
                || syncSetting.getContentType().equals(LIBRARY);

            boolean setConcreteTime3 = syncSetting.getContentType().equals(CLIENTS_DATA);
            this.everySecond = syncSetting.getEverySecond();
            this.limitStartHour = syncSetting.getLimitStartHour();
            this.limitEndHour = syncSetting.getLimitEndHour();
            this.monday = syncSetting.getMonday();
            this.tuesday = syncSetting.getTuesday();
            this.wednesday = syncSetting.getWednesday();
            this.thursday = syncSetting.getThursday();
            this.friday = syncSetting.getFriday();
            this.saturday = syncSetting.getSaturday();
            this.sunday = syncSetting.getSunday();
            String[] times = StringUtils.split(syncSetting.getConcreteTime(), SyncSetting.SEPARATOR);
            if(times != null) {
                if (times.length > 0) {
                    if (times.length >= 1) {
                        concreteTime1 = times[0];
                    } if (times.length >= 2 && setConcreteTime2) {
                        concreteTime2 = times[1];
                    } if (times.length >= 3 && setConcreteTime3) {
                        concreteTime3 = times[2];
                    }
                }
            }
        }

        public Integer getEverySecond() {
            return everySecond;
        }

        public void setEverySecond(Integer everySecond) {
            this.everySecond = everySecond;
        }

        public Integer getLimitStartHour() {
            return limitStartHour;
        }

        public void setLimitStartHour(Integer limitStartHour) {
            this.limitStartHour = limitStartHour;
        }

        public Integer getLimitEndHour() {
            return limitEndHour;
        }

        public void setLimitEndHour(Integer limitEndHour) {
            this.limitEndHour = limitEndHour;
        }

        public Boolean getMonday() {
            return monday;
        }

        public void setMonday(Boolean monday) {
            this.monday = monday;
        }

        public Boolean getTuesday() {
            return tuesday;
        }

        public void setTuesday(Boolean tuesday) {
            this.tuesday = tuesday;
        }

        public Boolean getWednesday() {
            return wednesday;
        }

        public void setWednesday(Boolean wednesday) {
            this.wednesday = wednesday;
        }

        public Boolean getThursday() {
            return thursday;
        }

        public void setThursday(Boolean thursday) {
            this.thursday = thursday;
        }

        public Boolean getFriday() {
            return friday;
        }

        public void setFriday(Boolean friday) {
            this.friday = friday;
        }

        public Boolean getSaturday() {
            return saturday;
        }

        public void setSaturday(Boolean saturday) {
            this.saturday = saturday;
        }

        public Boolean getSunday() {
            return sunday;
        }

        public void setSunday(Boolean sunday) {
            this.sunday = sunday;
        }

        public String getConcreteTime1() {
            return concreteTime1;
        }

        public void setConcreteTime1(String concreteTime1) {
            this.concreteTime1 = concreteTime1;
        }

        public String getConcreteTime2() {
            return concreteTime2;
        }

        public void setConcreteTime2(String concreteTime2) {
            this.concreteTime2 = concreteTime2;
        }

        public String getConcreteTime3() {
            return concreteTime3;
        }

        public void setConcreteTime3(String concreteTime3) {
            this.concreteTime3 = concreteTime3;
        }
    }
}
