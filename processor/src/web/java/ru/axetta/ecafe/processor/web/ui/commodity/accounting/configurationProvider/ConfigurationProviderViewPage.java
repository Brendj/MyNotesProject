/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductListPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.05.12
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderViewPage extends BasicWorkspacePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    private List<Org> orgList = new ArrayList<Org>();
    @Autowired
    private SelectedConfigurationProviderGroupPage selectedConfigurationProviderGroupPage;
    @Autowired
    private TechnologicalMapListPage technologicalMapListPage;
    @Autowired
    private ProductListPage productListPage;
    @Autowired
    private ConfigurationProviderService service;

    @Override
    public void onShow() throws Exception {
        selectedConfigurationProviderGroupPage.show();
        currentConfigurationProvider = selectedConfigurationProviderGroupPage.getSelectConfigurationProvider();
        orgList = service.findOrgsByConfigurationProvider(currentConfigurationProvider);
    }

    public Object showTechnologicalMaps() throws Exception{
        technologicalMapListPage.setSelectedConfigurationProvider(currentConfigurationProvider);
        //Показать и удаленный
        technologicalMapListPage.setDeletedStatusSelected(false);
        technologicalMapListPage.reload();
        technologicalMapListPage.show();
        return null;
    }

    public Object showProducts() throws Exception{
        productListPage.setSelectedConfigurationProvider(currentConfigurationProvider);
        /* Показать и удаленный */
        productListPage.setDeletedStatusSelected(false);
        productListPage.reload();
        productListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/view";
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }

    public List<Org> getOrgList() {
        return orgList;
    }

    public Boolean getOrgEmpty(){
        return orgList == null || orgList.isEmpty();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
