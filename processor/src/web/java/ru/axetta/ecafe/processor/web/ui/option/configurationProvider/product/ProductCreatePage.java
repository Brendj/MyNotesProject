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
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
public class ProductCreatePage extends BasicWorkspacePage implements ProductGroupSelect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductCreatePage.class);
    private Product product = new Product();
    private Long currentIdOfConfigurationProvider;
    private ProductGroup currentProductGroup;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private ProductGroupMenu productGroupMenu = new ProductGroupMenu();
    private List<ConfigurationProvider> configurationProviderList;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        configurationProviderList = DAOService.getInstance().getDistributedObjects(
                ConfigurationProvider.class);

        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
        } else {
            printError("Отсутсвуют поставщики продуктов.");
        }
    }
    public Object onSave(){
        try {
            product.setCreatedDate(new Date());
            product.setDeletedState(false);
            product.setGuid(UUID.randomUUID().toString());
            product.setGlobalVersion(0L);
            product.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);

            MainPage mainPage = MainPage.getSessionInstance();
            product.setUserCreate(mainPage.getCurrentUser());

            product.setProductGroup(currentProductGroup);
            DAOService.getInstance().setConfigurationProviderInDO(ProductGroup.class, currentProductGroup.getGlobalId(), currentIdOfConfigurationProvider);

            DAOService.getInstance().persistEntity(product);
            product = new Product();
            printMessage("Продукт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии продукта.");
            logger.error("Error create product",e);
        }
        return null;
    }

    public Object selectProductGroup() throws Exception{
        RuntimeContext.getAppContext().getBean(ProductGroupItemsPanel.class).reload();
        RuntimeContext.getAppContext().getBean(ProductGroupItemsPanel.class).pushCompleteHandler(RuntimeContext.getAppContext().getBean(getClass()));
        return null;
    }

    @Override
    public void select(ProductGroup productGroup) {
        if(productGroup!=null){
            currentProductGroup = productGroup;
        }
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty());
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/create";
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
