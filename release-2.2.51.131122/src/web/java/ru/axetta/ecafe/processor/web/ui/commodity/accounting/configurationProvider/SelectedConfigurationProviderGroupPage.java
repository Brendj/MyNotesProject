/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;

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
public class SelectedConfigurationProviderGroupPage extends BasicWorkspacePage {

    private String title;
    private ConfigurationProvider selectConfigurationProvider;

    @Override
    public void onShow() throws Exception {
        if (null == selectConfigurationProvider) {
            this.title = null;
        } else {
            this.title = String.format("%s",selectConfigurationProvider.getName());
        }
    }

    public String getTitle() {
        return title;
    }

    public ConfigurationProvider getSelectConfigurationProvider() {
        return selectConfigurationProvider;
    }

    public void setSelectConfigurationProvider(ConfigurationProvider selectConfigurationProvider) {
        this.selectConfigurationProvider = selectConfigurationProvider;
    }

}
