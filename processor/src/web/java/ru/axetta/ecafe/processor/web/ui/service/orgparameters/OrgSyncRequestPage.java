/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class OrgSyncRequestPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {
    private final Logger logger = LoggerFactory.getLogger(OrgSyncRequestPage.class);

    private List<SelectItem> listOfOrgDistricts;
    private List<SelectItem> listOfSyncType;
    private String selectedDistricts = "";
    private Integer selectedSyncType = SyncType.FULL_SYNC.ordinal();

    @Override
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            listOfOrgDistricts = buildListOfOrgDistricts(session);
            listOfSyncType = buildListOfSyncType();
        } catch (Exception e){
            logger.error("Exception when prepared the OrgSyncRequestPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
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

    private List<SelectItem> buildListOfSyncType() {
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        try{
            for(SyncType type: SyncType.values()){
                selectItemList.add(new SelectItem(type.ordinal(), type.description));
            }
        } catch (Exception e){
            logger.error("Cant build SyncType items", e);
        }
        return selectItemList;
    }

    public void applySyncOperation(){
        //todo
    }

    @Override
    public String getPageFilename() {
        return "service/org_sync_request";
    }

    @Override
    public Logger getLogger(){
        return this.logger;
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

    public List<SelectItem> getListOfSyncType() {
        return listOfSyncType;
    }

    public void setListOfSyncType(List<SelectItem> listOfSyncType) {
        this.listOfSyncType = listOfSyncType;
    }

    public Integer getSelectedSyncType() {
        return selectedSyncType;
    }

    public void setSelectedSyncType(Integer selectedSyncType) {
        this.selectedSyncType = selectedSyncType;
    }

    public enum SyncType {
        FULL_SYNC("Полная"),
        CLIENT_SYNC("Данные по клиентам"),
        MENU_SYNC("Меню"),
        ORG_SETTING_SYNC("Настройки ОО");

        SyncType(String description){
            this.description = description;
        }

        private String description;
    }
}
