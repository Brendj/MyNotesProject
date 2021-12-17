/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SettingViewPage extends BasicWorkspacePage {

    private ECafeSettings setting;
    private OrgItem orgItem;
    @Autowired
    private SelectedSettingsGroupPage selectedSettingsGroupPage;
    @Autowired
    private SettingEditPage settingEditPage;
    @Autowired
    private DAOService daoService;
    @Override
    public void onShow() throws Exception {
         setting = selectedSettingsGroupPage.getSelectSettings();
         orgItem = selectedSettingsGroupPage.getCurrentOrg();
    }

    public Object edit() throws Exception {
        selectedSettingsGroupPage.setSelectSettings(setting);
        Org currentOrg = DAOReadonlyService.getInstance().findOrg(setting.getOrgOwner());
        selectedSettingsGroupPage.setCurrentOrg(new OrgItem(currentOrg));
        selectedSettingsGroupPage.onShow();
        MainPage.getSessionInstance().setCurrentWorkspacePage(settingEditPage);
        settingEditPage.show();
        return null;
    }

    @Override
    public String getPageFilename() {
        return "org/settings/view";
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
}
