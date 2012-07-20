/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductEditPage extends BasicWorkspacePage implements ProductGroupSelect, ConfigurationProviderSelect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductEditPage.class);
    private Product currentProduct;
    private ConfigurationProvider currentConfigurationProvider;
    private ProductGroup currentProductGroup;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        currentProduct = entityManager.merge(currentProduct);
        if(currentConfigurationProvider.getIdOfConfigurationProvider()!=null){
            currentConfigurationProvider = entityManager.find(ConfigurationProvider.class, currentConfigurationProvider.getIdOfConfigurationProvider());
        }
        if(currentProduct.getProductGroup()!=null){
            currentProductGroup = currentProduct.getProductGroup();
        }
    }

    public Object onSave(){
        try {
            if(currentConfigurationProvider==null){
                printError("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }

            if(currentProduct.getFullName()==null || currentProduct.getFullName().equals("")){
                printError("Поле 'Полное наименование пищевого продукта' обязательное.");
                return null;
            }
            if(currentProduct.getProductName()==null || currentProduct.getProductName().equals("")){
                printError("Поле 'Товарное название' обязательное.");
                return null;
            }
            Product p = entityManager.find(Product.class, currentProduct.getGlobalId());
            p.fill(currentProduct);
            p.setLastUpdate(new Date());
            p.setDeletedState(currentProduct.getDeletedState());
            p.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());

            MainPage mainPage = MainPage.getSessionInstance();
            if(p.getDeletedState().equals(Boolean.TRUE) && currentProduct.getDeletedState().equals(Boolean.FALSE)){
                p.setUserDelete(mainPage.getCurrentUser());
            } else {
                p.setUserEdit(mainPage.getCurrentUser());
            }

            p.setProductGroup(currentProductGroup);
            daoService.setConfigurationProviderInDO(ProductGroup.class, currentProductGroup.getGlobalId(),
                    currentConfigurationProvider.getIdOfConfigurationProvider());

            currentProduct = (Product) daoService.mergeDistributedObject(p, currentProduct.getGlobalVersion() + 1);
            printMessage("Продукт сохранен успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении продукта.");
            logger.error("Error saved Product", e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentProduct.getDeletedState()) {
            printMessage("Продукт не может быть удален.");
            return;
        }
        try{
            ProductGroup pg = entityManager.getReference(ProductGroup.class, currentProduct.getGlobalId());
            entityManager.remove(pg);
            printMessage("Продукт успешно удален.");
        }  catch (Exception e){
            printError("Ошибка при удалении продукта.");
            logger.error("Error by delete Product.", e);
        }
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
        if(configurationProvider!=null){
            currentConfigurationProvider = configurationProvider;
        }
    }

    public Object selectProductGroup() throws Exception{
        productGroupItemsPanel.reload();
        productGroupItemsPanel.setSelectProductGroup(currentProductGroup);
        productGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ProductGroup productGroup) {
        if(productGroup!=null){
            currentProductGroup = productGroup;
        }
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/edit";
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
