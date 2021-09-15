/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductListPage;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductGroupViewPage extends BasicWorkspacePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductGroupViewPage.class);
    private ProductGroup currentProductGroup;
    private Long countProducts;
    private Org currentOrg;
    private ConfigurationProvider currentConfigurationProvider;
    @Autowired
    private SelectedProductGroupGroupPage selectedProductGroupGroupPage;
    @Autowired
    private ProductListPage productListPage;
    @Autowired
    private DAOService service;


    @Override
    public void onShow() throws Exception {
        selectedProductGroupGroupPage.onShow();
        currentProductGroup = selectedProductGroupGroupPage.getCurrentProductGroup();
        countProducts = service.countProductsByProductGroup(currentProductGroup);
        currentOrg = DAOReadonlyService.getInstance().findOrg(currentProductGroup.getOrgOwner());
        currentConfigurationProvider = service.getConfigurationProvider(currentProductGroup.getIdOfConfigurationProvider());
    }

    public Object showProducts() throws Exception{
        productListPage.setSelectedProductGroup(currentProductGroup);
        /* Показать и удаленный */
        productListPage.setSelectedConfigurationProvider(currentConfigurationProvider);
        productListPage.setDeletedStatusSelected(true);
        productListPage.reload();
        productListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/product/group/view";
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public void setCurrentProductGroup(ProductGroup currentProductGroup) {
        this.currentProductGroup = currentProductGroup;
    }

    public Long getCountProducts() {
        return countProducts;
    }

    public Org getCurrentOrg() {
        return currentOrg;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }
}
