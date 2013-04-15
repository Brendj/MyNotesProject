/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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
public class SettingViewPage extends BasicWorkspacePage {

    private ECafeSettings setting;
    private OrgItem orgItem;
    @Autowired
    private SelectedSettingsGroupPage selectedSettingsGroupPage;

    @Override
    public void onShow() throws Exception {
         setting = selectedSettingsGroupPage.getSelectSettings();
         orgItem = selectedSettingsGroupPage.getCurrentOrg();
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
