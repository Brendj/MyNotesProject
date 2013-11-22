/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SelectedSettingsGroupPage extends BasicWorkspacePage {

    private String title;
    private ECafeSettings selectSettings;
    private OrgItem currentOrg;

    @Override
    public void onShow() throws Exception {
        if (null == selectSettings) {
            this.title = null;
        } else {
            this.title = String.format("(%s)%s",selectSettings.getGlobalId(),selectSettings.getSettingsId().toString());
        }
    }

    public String getTitle() {
        return title;
    }

    public ECafeSettings getSelectSettings() {
        return selectSettings;
    }

    public void setSelectSettings(ECafeSettings selectSettings) {
        this.selectSettings = selectSettings;
    }

    public OrgItem getCurrentOrg() {
        return currentOrg;
    }

    public void setCurrentOrg(OrgItem currentOrg) {
        this.currentOrg = currentOrg;
    }
}
