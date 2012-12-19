/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
/*import ru.axetta.ecafe.processor.core.persistence.ProductGuide;
import ru.axetta.ecafe.processor.core.persistence.User;*/
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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

    @PersistenceContext
    private EntityManager entityManager;

    private List<ConfigurationProvider> configurationProviderList;

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", configurationProviderList.size());
    }

    @Override
    public void onShow() throws Exception {
        configurationProviderList = entityManager.createQuery("from ConfigurationProvider order by id",ConfigurationProvider.class).getResultList();
    }

    public List<ConfigurationProvider> getConfigurationProviderList() {
        return configurationProviderList;
    }

    public void setConfigurationProviderList(List<ConfigurationProvider> configurationProviderList) {
        this.configurationProviderList = configurationProviderList;
    }
}
