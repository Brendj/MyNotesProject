/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.product;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.product.group.ProductGroupMenu;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
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
public class ProductCreatePage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductCreatePage.class);
    private Product product = new Product();
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
    public Object onSave(){
        try {
            product.setCreatedDate(new Date());
            product.setDeletedState(false);
            product.setGuid(UUID.randomUUID().toString());
            product.setGlobalVersion(0L);
            product.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
            product.setProductGroup(
                    DAOService.getInstance().findRefDistributedObject(ProductGroup.class, currentIdOfProductGroup));
            DAOService.getInstance().persistEntity(product);
            product = new Product();
            printMessage("Продукт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии продукта.");
            logger.error("Error create product",e);
        }
        return null;
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || productGroupList==null || productGroupList.isEmpty());
    }

    public String getPageFilename() {
        return "option/product/create";
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

    public Long getCurrentIdOfConfigurationProvider() {
        return currentIdOfConfigurationProvider;
    }

    public void setCurrentIdOfConfigurationProvider(Long currentIdOfConfigurationProvider) {
        this.currentIdOfConfigurationProvider = currentIdOfConfigurationProvider;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void setConfigurationProviderMenu(ConfigurationProviderMenu configurationProviderMenu) {
        this.configurationProviderMenu = configurationProviderMenu;
    }
}
