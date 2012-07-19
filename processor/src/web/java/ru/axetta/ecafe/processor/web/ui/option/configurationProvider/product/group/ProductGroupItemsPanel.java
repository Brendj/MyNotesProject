/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductGroupItemsPanel extends BasicPage {

    private final Stack<ProductGroupSelect> completeHandlerLists = new Stack<ProductGroupSelect>();

    private List<ProductGroup> productGroupList = new ArrayList<ProductGroup>();
    private ProductGroup selectProductGroup = new ProductGroup();
    private String filter="";

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandler(ProductGroupSelect handler) {
        completeHandlerLists.push(handler);
    }

    public Object addProductGroup(){
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().select(selectProductGroup);
            completeHandlerLists.pop();
        }
        return null;
    }

    public void reload() throws Exception {
        productGroupList = retrieveProduct();
        selectProductGroup = new ProductGroup();
    }

    public Object updateConfigurationProviderSelectPage(){
        productGroupList = retrieveProduct();
        return null;
    }

    @Transactional
    private List<ProductGroup> retrieveProduct(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from ProductGroup "+ where + " order by id";
        return entityManager.createQuery(query, ProductGroup.class).getResultList();
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<ProductGroup> getProductGroupList() {
        return productGroupList;
    }

    public void setProductGroupList(List<ProductGroup> productGroupList) {
        this.productGroupList = productGroupList;
    }

    public ProductGroup getSelectProductGroup() {
        return selectProductGroup;
    }

    public void setSelectProductGroup(ProductGroup selectProductGroup) {
        this.selectProductGroup = selectProductGroup;
    }
}
