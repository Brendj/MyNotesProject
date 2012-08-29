/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;


import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
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
public class ProductCreatePage extends BasicWorkspacePage implements ProductGroupSelect {
    private static final Logger logger = LoggerFactory.getLogger(ProductCreatePage.class);
    private Product product;
    private ProductGroup currentProductGroup;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        product = new Product();
        currentProductGroup = null;
    }

    public Object onSave(){
        try {
            if(currentProductGroup==null){
                printError("Поле 'Группа продуктов' обязательное.");
                return null;
            }
            if(product.getProductName()==null || product.getProductName().equals("")){
                printError("Поле 'Товарное название' обязательное.");
                return null;
            }
            product.setCreatedDate(new Date());
            product.setDeletedState(false);
            product.setGuid(UUID.randomUUID().toString());
            product.setGlobalVersion(daoService.updateVersionByDistributedObjects(Product.class.getSimpleName()));
            product.setIdOfConfigurationProvider(currentProductGroup.getIdOfConfigurationProvider());
            product.setOrgOwner(currentProductGroup.getOrgOwner());

            MainPage mainPage = MainPage.getSessionInstance();
            product.setUserCreate(mainPage.getCurrentUser());
            product.setProductGroup(currentProductGroup);

            daoService.persistEntity(product);
            reload();
            printMessage("Продукт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии продукта.");
            logger.error("Error create product",e);
        }
        return null;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
