/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.product;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.product.group.ProductGroupMenu;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group.TechnologicalMapGroupCreatePage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class ProductEditPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductEditPage.class);
    private Product currentProduct;

    private Long currentIdOfConfigurationProvider;
    private Long currentIdOfProductGroup;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private ProductGroupMenu productGroupMenu = new ProductGroupMenu();
    private List<ConfigurationProvider> configurationProviderList;
    private List<ProductGroup> productGroupList;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        currentProduct = entityManager.merge(currentProduct);
        configurationProviderList = DAOService.getInstance().getDistributedObjects(
                ConfigurationProvider.class);
        productGroupList = DAOService.getInstance().getDistributedObjects(ProductGroup.class);

        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
            productGroupMenu.readAllItems(productGroupList);
        } else {
            printError("Отсутсвуют поставщики или группы продуктов.");
        }

    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || productGroupList==null || productGroupList.isEmpty());
    }

    public Object onSave(){
        try {
            currentProduct = (Product) DAOService.getInstance().mergeDistributedObject(currentProduct,currentProduct.getGlobalVersion()+1);
            printMessage("Продукт сохранен успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении продукта.");
            logger.trace("Error saved Product",e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentProduct.getDeletedState()) {
            printMessage("Продукт не может быть удалена.");
            return;
        }
        try{
            ProductGroup pg = entityManager.getReference(ProductGroup.class, currentProduct.getGlobalId());
            entityManager.remove(pg);
            printMessage("Продукт удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении продукта.");
            logger.error("Error by delete Product.", e);
        }
    }

    @Transactional
    private void reload() throws Exception{
        currentProduct = entityManager.merge(currentProduct);
    }

    @Transactional
    private void save() throws Exception{
        currentProduct = (Product) DAOService.getInstance().mergeDistributedObject(currentProduct,currentProduct.getGlobalVersion()+1);
    }

    public String getPageFilename() {
        return "option/product/edit";
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void setConfigurationProviderMenu(ConfigurationProviderMenu configurationProviderMenu) {
        this.configurationProviderMenu = configurationProviderMenu;
    }

    public Long getCurrentIdOfConfigurationProvider() {
        return currentIdOfConfigurationProvider;
    }

    public void setCurrentIdOfConfigurationProvider(Long currentIdOfConfigurationProvider) {
        this.currentIdOfConfigurationProvider = currentIdOfConfigurationProvider;
    }

    public Long getCurrentIdOfProductGroup() {
        return currentIdOfProductGroup;
    }

    public void setCurrentIdOfProductGroup(Long currentIdOfProductGroup) {
        this.currentIdOfProductGroup = currentIdOfProductGroup;
    }

    public ProductGroupMenu getProductGroupMenu() {
        return productGroupMenu;
    }

    public void setProductGroupMenu(ProductGroupMenu productGroupMenu) {
        this.productGroupMenu = productGroupMenu;
    }
}
