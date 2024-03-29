/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEditPage.class);
    private Product currentProduct;
    private ConfigurationProvider currentConfigurationProvider;
    private ProductGroup currentProductGroup;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;
    @Autowired
    private SelectedProductGroupPage selectedProductGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedProductGroupPage.onShow();
        currentProduct = selectedProductGroupPage.getCurrentProduct();
        if(currentProduct.getIdOfConfigurationProvider()!=null){
            currentConfigurationProvider = entityManager.find(ConfigurationProvider.class,
                    currentProduct.getIdOfConfigurationProvider());
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
            p.setGlobalVersion(daoService.updateVersionByDistributedObjects(Product.class.getSimpleName()));
            daoService.mergeDistributedObject(p, p.getGlobalVersion()+1);
            currentProduct = entityManager.find(Product.class, currentProduct.getGlobalId());
            selectedProductGroupPage.setCurrentProduct(currentProduct);
            printMessage("Продукт сохранен успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении продукта.");
            LOGGER.error("Error saved Product", e);
        }
        return null;
    }

    public Object removeProduct(){
        remove();
        return null;
    }

    protected void remove(){
        if(!currentProduct.getDeletedState()) {
            printError("Продукт не может быть удален со статусом неудален.");
        }
        try{
            daoService.removeProduct(currentProduct);
            printMessage("Продукт успешно удален.");
        }  catch (Exception e){
            printError("Ошибка при удалении продукта.");
            LOGGER.error("Error by delete Product.", e);
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
        currentConfigurationProvider = configurationProvider;
    }

    public Object selectProductGroup() throws Exception{
        productGroupItemsPanel.reload();
        productGroupItemsPanel.setSelectProductGroup(currentProductGroup);
        productGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ProductGroup productGroup) {
        currentProductGroup = productGroup;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/product/edit";
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
