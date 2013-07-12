/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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
public class ProductListPage extends BasicWorkspacePage implements ProductGroupSelect {

    private static final Logger logger = LoggerFactory.getLogger(ProductListPage.class);
    private List<Product> productList;
    private Boolean deletedStatusSelected = Boolean.FALSE;
    private ProductGroup selectedProductGroup;
    private ConfigurationProvider selectedConfigurationProvider;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onShow() { }

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("Product onSearch error: ", e);
        }
        return null;
    }

    public Object onClear() throws Exception{
        selectedProductGroup = null;
        return null;
    }

    public void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(selectedProductGroup!=null){
            if(selectedConfigurationProvider!=null){
                if(orgOwners==null || orgOwners.isEmpty()){
                    productList = daoService.findProductByConfigurationProvider(selectedProductGroup, selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
                } else {
                    productList = daoService.findProductByConfigurationProvider(selectedProductGroup, selectedConfigurationProvider.getIdOfConfigurationProvider(),orgOwners, deletedStatusSelected);
                }
            } else {
                if(orgOwners==null || orgOwners.isEmpty()){
                    productList = daoService.findProductByConfigurationProvider(selectedProductGroup, deletedStatusSelected);
                } else {
                    productList = daoService.findProductByConfigurationProvider(selectedProductGroup, orgOwners, deletedStatusSelected);
                }
            }
        } else {
            if(selectedConfigurationProvider!=null){
                if(orgOwners==null || orgOwners.isEmpty()){
                    productList = daoService.findProductByConfigurationProvider(selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
                } else {
                    productList = daoService.findProductByConfigurationProvider(selectedConfigurationProvider.getIdOfConfigurationProvider(),orgOwners, deletedStatusSelected);
                }
            } else {
                if(orgOwners==null || orgOwners.isEmpty()){
                    productList = daoService.findProductByConfigurationProvider(deletedStatusSelected);
                } else {
                    productList = daoService.findProductByConfigurationProvider(orgOwners, deletedStatusSelected);
                }
            }
        }

    }

    public Object selectProductGroup() throws Exception{
        productGroupItemsPanel.reload();
        productGroupItemsPanel.setSelectProductGroup(selectedProductGroup);
        productGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ProductGroup productGroup) {
        selectedProductGroup = productGroup;
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (productList==null?0:productList.size()));
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/product/list";
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

    public Boolean getEmptyProductList(){
        return  this.productList == null || this.productList.isEmpty();
    }

    public ProductGroup getSelectedProductGroup() {
        return selectedProductGroup;
    }

    public void setSelectedProductGroup(ProductGroup selectedProductGroup) {
        this.selectedProductGroup = selectedProductGroup;
    }

    public ConfigurationProvider getSelectedConfigurationProvider() {
        return selectedConfigurationProvider;
    }

    public void setSelectedConfigurationProvider(ConfigurationProvider selectedConfigurationProvider) {
        this.selectedConfigurationProvider = selectedConfigurationProvider;
    }
}
