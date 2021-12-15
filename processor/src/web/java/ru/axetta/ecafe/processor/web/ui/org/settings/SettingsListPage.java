/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SettingsListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler{

    private DataModel settingsList;
    private OrgItem orgItem;
    private Integer settingsIds=-1;
    private Boolean deleted=false;
    private final SettingsIdEnumTypeMenu settingsIdEnumTypeMenu = new SettingsIdEnumTypeMenu();

    @Autowired
    private SelectedSettingsGroupPage selectedSettingsGroupPage;
    @Autowired
    private SettingEditPage settingEditPage;
    @Autowired
    private SettingViewPage settingViewPage;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Override
    public String getPageFilename() {
        return "org/settings/list";
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.orgItem = new OrgItem(org);
        }
    }

    private void reload() {
        final Long idOfOrg = (orgItem == null ? null : orgItem.getIdOfOrg());
        final SettingsIds settingsid = (settingsIds == null || settingsIds < 0 ? null : SettingsIds.fromInteger(settingsIds));
        List<ECafeSettings> list = daoService.geteCafeSettingsesWithoutFiveElm(idOfOrg,settingsid,deleted);
        for (ECafeSettings eCafeSettings : list)
            eCafeSettings.setSettingsIdDescription(eCafeSettings.getSettingsId().toString());
        this.settingsList = new ListDataModel(list);
    }

    public Object updateSettingListPage() {
        reload();
        return null;
    }

    public Object clearSettingListPageFilter() {
        orgItem=null;
        settingsIds=-1;
        deleted = false;
        reload();
        return null;
    }

    public Object view() throws Exception {
        extractParams();
        MainPage.getSessionInstance().setCurrentWorkspacePage(settingViewPage);
        settingViewPage.show();
        return null;
    }

    private void extractParams() throws Exception {
        ECafeSettings settings = getEntityFromRequestParam();
        selectedSettingsGroupPage.setSelectSettings(settings);
        Org currentOrg = DAOReadonlyService.getInstance().findOrg(settings.getOrgOwner());
        selectedSettingsGroupPage.setCurrentOrg(new OrgItem(currentOrg));
        selectedSettingsGroupPage.onShow();
    }

    public Object edit() throws Exception {
        extractParams();
        MainPage.getSessionInstance().setCurrentWorkspacePage(settingEditPage);
        settingEditPage.show();
        return null;
    }

    private ECafeSettings getEntityFromRequestParam() {
        if (settingsList == null) return null;
        return (ECafeSettings) settingsList.getRowData();
    }

    public DataModel getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(DataModel settingsList) {
        this.settingsList = settingsList;
    }

    public boolean isEmpty() {
        return (orgItem==null) || (settingsIds==null);
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public OrgItem getOrgItem() {
        return orgItem;
    }

    public void setOrgItem(OrgItem orgItem) {
        this.orgItem = orgItem;
    }

    public Integer getSettingsIds() {
        return settingsIds;
    }

    public String getSettingsHeadText() {
        if(settingsIds!=null && settingsIds==4){
            return "Параметры";
        }
        return "Параметры принтера (формат чека)";
    }

    public void setSettingsIds(Integer settingsIds) {
        this.settingsIds = settingsIds;
    }

    public SettingsIdEnumTypeMenu getSettingsIdEnumTypeMenu() {
        return settingsIdEnumTypeMenu;
    }

}
