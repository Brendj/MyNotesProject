/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ProductViewPage.class);
    private Product currentProduct;
    private ConfigurationProvider configurationProvider;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SelectedProductGroupPage selectedProductGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedProductGroupPage.onShow();
        currentProduct = selectedProductGroupPage.getCurrentProduct();
        if(currentProduct.getIdOfConfigurationProvider()!=null){
            configurationProvider = entityManager.find(ConfigurationProvider.class, currentProduct.getIdOfConfigurationProvider());
        }
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/view";
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }
}
