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
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupMenu;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
public class ProductListPage extends BasicWorkspacePage {

    private static final Long ALL = -2L;
    private static final Long NONE = -1L;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductListPage.class);
    private List<Product> productList;

    private Long currentIdOfConfigurationProvider=NONE;
    private Long currentIdOfProductGroup=NONE;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private ProductGroupMenu productGroupMenu = new ProductGroupMenu();
    private List<ConfigurationProvider> configurationProviderList;
    private List<ProductGroup> productGroupList;
    private Boolean deletedStatusSelected = Boolean.FALSE;


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() {
        try {
            RuntimeContext.getAppContext().getBean(getClass()).reload();
        } catch (Exception e) {
            printError("Ошибка при загрузке списка групп.");
        }
    }

    public Object onChange() throws Exception{
        reload();
        return null;
    }

    @Transactional
    private void reload() throws Exception{
        configurationProviderList = DAOService.getInstance().getDistributedObjects(
                ConfigurationProvider.class);
        productGroupList = DAOService.getInstance().getDistributedObjects(ProductGroup.class);
        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
            productGroupMenu.readAllItems(productGroupList);
        }
        String where="";
        if(!currentIdOfConfigurationProvider.equals(ALL)){
            where = where+ " idOfConfigurationProvider="+currentIdOfConfigurationProvider;
        }
        ProductGroup pg = null;
        if(!currentIdOfProductGroup.equals(ALL)){
            pg = DAOService.getInstance().findRefDistributedObject(ProductGroup.class,currentIdOfProductGroup);
            if(pg!=null){
                if(!where.equals("")) where = where + " and ";
                where = where+ " productGroup=:productGroup";
            }
        }
        if(!deletedStatusSelected){
            if(!where.equals("")) where = where + " and ";
            where = where+" deletedState=FALSE";
        }
        if(!where.equals("")) where  =" where "+ where;
        TypedQuery<Product> query = entityManager.createQuery("FROM Product "+where+" ORDER BY globalId",Product.class);
        if(pg!=null) query.setParameter("productGroup", pg);
        productList = query.getResultList();
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || productGroupList==null || productGroupList.isEmpty());
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (productList==null?0:productList.size()));
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/list";
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
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
