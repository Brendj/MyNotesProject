/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.05.12
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderListPage extends BasicWorkspacePage {

    @Autowired
    private ConfigurationProviderService service;

    private List<ConfigurationProvider> configurationProviderList;

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", configurationProviderList.size());
    }

    @Override
    public void onShow() throws Exception {
        configurationProviderList = service.findConfigurationProvider();
    }

    public List<ConfigurationProvider> getConfigurationProviderList() {
        return configurationProviderList;
    }

    public void setConfigurationProviderList(List<ConfigurationProvider> configurationProviderList) {
        this.configurationProviderList = configurationProviderList;
    }

    public boolean getEligibleToWorkConfigurationProviderList() {
        Boolean result = false;
        try {
            User user = MainPage.getSessionInstance().getCurrentUser();
            if (user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification())) {
                result = true;
            } else {
                result = !user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification());
            }
        } catch (Exception e) {
             getLogger().error("getEligibleToWorkConfigurationProviderList exception", e);
        }
        return result;
    }
}
