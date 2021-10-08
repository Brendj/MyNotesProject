/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;

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
public class ProductGroupListPage extends BasicWorkspacePage implements ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(ProductGroupListPage.class);
    private List<ProductGroup> productGroupList;
    private Boolean deletedStatusSelected = Boolean.FALSE;
    private ConfigurationProvider selectedConfigurationProvider;
    @Autowired
    private DAOReadonlyService daoService;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onShow() {}

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("ProductGroup onSearch error: ", e);
        }
        return null;
    }

    public Object onClear() throws Exception{
        selectedConfigurationProvider = null;
        return null;
    }

    private void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(selectedConfigurationProvider!=null){
            if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                productGroupList = daoService.findProductGroupByConfigurationProvider(selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
            } else {
                productGroupList = daoService.findProductGroupByConfigurationProvider(selectedConfigurationProvider.getIdOfConfigurationProvider(),orgOwners, deletedStatusSelected);
            }
        } else {
            if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                productGroupList = daoService.findProductGroupByConfigurationProvider(deletedStatusSelected);
            } else {
                productGroupList = daoService.findProductGroupByConfigurationProvider(orgOwners,deletedStatusSelected);
            }
        }
    }

    public Object selectConfigurationProvider() throws Exception{
        configurationProviderItemsPanel.reload();
        if(selectedConfigurationProvider !=null){
            configurationProviderItemsPanel.setSelectConfigurationProvider(selectedConfigurationProvider);
        }
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        selectedConfigurationProvider = configurationProvider;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/product/group/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (productGroupList==null?0:productGroupList.size()));
    }

    public List<ProductGroup> getProductGroupList() {
        return productGroupList;
    }

    public Boolean getEmptyProductGroupList(){
        return  this.productGroupList == null || this.productGroupList.isEmpty();
    }

    public void setProductGroupList(List<ProductGroup> productGroupList) {
        this.productGroupList = productGroupList;
    }

    public ConfigurationProvider getSelectedConfigurationProvider() {
        return selectedConfigurationProvider;
    }

    public void setSelectedConfigurationProvider(ConfigurationProvider selectedConfigurationProvider) {
        this.selectedConfigurationProvider = selectedConfigurationProvider;
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }
}
