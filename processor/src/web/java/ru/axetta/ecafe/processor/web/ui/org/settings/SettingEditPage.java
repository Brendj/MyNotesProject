/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SettingEditPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private ECafeSettings setting;
    private OrgItem orgItem;
    private Integer settingsIds;
    private final SettingsIdEnumTypeMenu settingsIdEnumTypeMenu = new SettingsIdEnumTypeMenu();
    private ECafeSettings.AbstractParserBySettingValue parserBySettingValue;
    private SelectItemBuilder selectItemBuilder;

    @Autowired
    private SelectedSettingsGroupPage selectedSettingsGroupPage;

    @Override
    public void onShow() throws Exception {
        setting = selectedSettingsGroupPage.getSelectSettings();
        orgItem = selectedSettingsGroupPage.getCurrentOrg();
        settingsIds = setting.getSettingsId().getId();
        parserBySettingValue = setting.getSplitSettingValue();
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.orgItem = new OrgItem(org);
        }
    }

    @Override
    public String getPageFilename() {
        return "org/settings/edit";
    }

    public SettingsIdEnumTypeMenu getSettingsIdEnumTypeMenu() {
        return settingsIdEnumTypeMenu;
    }

    public Integer getSettingsIds() {
        return settingsIds;
    }

    public void setSettingsIds(Integer settingsIds) {
        this.settingsIds = settingsIds;
    }

    public ECafeSettings getSetting() {
        return setting;
    }

    public void setSetting(ECafeSettings setting) {
        this.setting = setting;
    }

    public OrgItem getOrgItem() {
        return orgItem;
    }

    public void setOrgItem(OrgItem orgItem) {
        this.orgItem = orgItem;
    }

    public ECafeSettings.AbstractParserBySettingValue getParserBySettingValue() {
        return parserBySettingValue;
    }

}
