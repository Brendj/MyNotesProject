/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;


import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductCreatePage extends BasicWorkspacePage implements ProductGroupSelect, ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(ProductCreatePage.class);
    private Product product;
    private ConfigurationProvider currentConfigurationProvider;
    private ProductGroup currentProductGroup;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        product = new Product();
        currentConfigurationProvider = null;
        currentProductGroup = null;
    }

    public Object onSave(){
        try {
            if(currentConfigurationProvider==null){
                printError("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            if(currentProductGroup==null){
                printError("Поле 'Группа продуктов' обязательное.");
                return null;
            }
            if(product.getFullName()==null || product.getFullName().equals("")){
                printError("Поле 'Полное наименование пищевого продукта' обязательное.");
                return null;
            }
            if(product.getProductName()==null || product.getProductName().equals("")){
                printError("Поле 'Товарное название' обязательное.");
                return null;
            }
            product.setCreatedDate(new Date());
            product.setDeletedState(false);
            product.setGuid(UUID.randomUUID().toString());
            product.setGlobalVersion(daoService.getVersionByDistributedObjects(Product.class));
            product.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());

            MainPage mainPage = MainPage.getSessionInstance();
            product.setUserCreate(mainPage.getCurrentUser());
            product.setProductGroup(currentProductGroup);
            daoService.setConfigurationProviderInDO(ProductGroup.class, currentProductGroup.getGlobalId(),
                    currentConfigurationProvider.getIdOfConfigurationProvider());

            daoService.persistEntity(product);
            currentConfigurationProvider = new ConfigurationProvider();
            reload();
            printMessage("Продукт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии продукта.");
            logger.error("Error create product",e);
        }
        return null;
    }

    public Object selectConfigurationProvider() throws Exception{
        configurationProviderItemsPanel.reload();
        if(currentConfigurationProvider!=null){
            configurationProviderItemsPanel.setSelectConfigurationProvider(currentConfigurationProvider);
        }
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        currentConfigurationProvider = configurationProvider;
    }

    public Object selectProductGroup() throws Exception{
        productGroupItemsPanel.reload();
        if(currentProductGroup!=null){
            productGroupItemsPanel.setSelectProductGroup(currentProductGroup);
        }
        productGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ProductGroup productGroup) {
        currentProductGroup = productGroup;
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/create";
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
