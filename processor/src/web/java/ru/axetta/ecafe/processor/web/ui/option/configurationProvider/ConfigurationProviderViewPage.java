/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductListPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    @Autowired
    private SelectedConfigurationProviderGroupPage selectedConfigurationProviderGroupPage;
    @Autowired
    private TechnologicalMapListPage technologicalMapListPage;
    @Autowired
    private ProductListPage productListPage;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        selectedConfigurationProviderGroupPage.show();
        currentConfigurationProvider = selectedConfigurationProviderGroupPage.getSelectConfigurationProvider();
    }

    public Object showTechnologicalMaps() throws Exception{
        technologicalMapListPage.setSelectedConfigurationProvider(currentConfigurationProvider);
        //Показать и удаленный
        technologicalMapListPage.setDeletedStatusSelected(true);
        technologicalMapListPage.reload();
        technologicalMapListPage.show();
        return null;
    }

    public Object showProducts() throws Exception{
        productListPage.setSelectedConfigurationProvider(currentConfigurationProvider);
        /* Показать и удаленный */
        productListPage.setDeletedStatusSelected(true);
        productListPage.reload();
        productListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/view";
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }



}
