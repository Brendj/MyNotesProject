/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductListPage extends BasicWorkspacePage implements ProductGroupSelect, ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(ProductListPage.class);
    private List<Product> productList;
    private Boolean deletedStatusSelected = Boolean.FALSE;
    private ConfigurationProvider selectedConfigurationProvider;
    private ProductGroup selectedProductGroup;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ProductGroupItemsPanel productGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Override
    public void onShow() { }

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception{
        selectedConfigurationProvider = null;
        selectedProductGroup = null;
        return null;
    }

    @Transactional
    public void reload() throws Exception{
        String where = "";
        if(selectedConfigurationProvider!=null){
            where = " idOfConfigurationProvider=" + selectedConfigurationProvider.getIdOfConfigurationProvider();
        }
        if(selectedProductGroup!=null){
            where = (where.equals("")?"":where + " and ") + " productGroup=:productGroup";
        }
        where = (where.equals("")?"":" where ") + where;
        TypedQuery<Product> query = entityManager.createQuery("from Product " + where, Product.class);
        if(selectedProductGroup!=null){
            query.setParameter("productGroup", selectedProductGroup);
        }
        productList = query.getResultList();
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
        return super.getPageTitle() + String.format(" (%d)", (productList==null?"нет":productList.size()));
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

    public Boolean getEmptyProductList(){
        return  this.productList == null || this.productList.isEmpty();
    }

    public ConfigurationProvider getSelectedConfigurationProvider() {
        return selectedConfigurationProvider;
    }

    public void setSelectedConfigurationProvider(ConfigurationProvider selectedConfigurationProvider) {
        this.selectedConfigurationProvider = selectedConfigurationProvider;
    }

    public ProductGroup getSelectedProductGroup() {
        return selectedProductGroup;
    }

    public void setSelectedProductGroup(ProductGroup selectedProductGroup) {
        this.selectedProductGroup = selectedProductGroup;
    }

}
