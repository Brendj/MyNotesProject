/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    private final static Logger logger = LoggerFactory.getLogger(ProductGroupItemsPanel.class);

    private final Queue<ProductGroupSelect> completeHandlerLists = new LinkedList<ProductGroupSelect>();

    private List<ProductGroup> productGroupList;
    private ProductGroup selectProductGroup;
    private String filter;
    @Autowired
    private DAOReadonlyService daoReadonlyService;
    @Autowired
    private ContextDAOServices contextDAOServices;


    public void pushCompleteHandler(ProductGroupSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addProductGroup(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectProductGroup:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void postConstruct() {
        productGroupList = new ArrayList<ProductGroup>();
        selectProductGroup = new ProductGroup();
        filter="";
    }

    public void reload() throws Exception {
         productGroupList = new ArrayList<ProductGroup>();
         selectProductGroup = new ProductGroup();
         filter="";
        retrieveProduct();
    }

    public Object updateConfigurationProviderSelectPage(){
        try {
            retrieveProduct();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page",e);
        }
        return null;
    }

    private void retrieveProduct() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(orgOwners==null || orgOwners.isEmpty()){
            if (StringUtils.isEmpty(filter)){
                productGroupList = daoReadonlyService.findProductGroupByConfigurationProvider(false);
            } else{
                productGroupList = daoReadonlyService.findProductGroupByConfigurationProvider(filter);
            }
        } else {
            if (StringUtils.isEmpty(filter)){
                productGroupList = daoReadonlyService.findProductGroupByConfigurationProvider(orgOwners, false);
            } else{
                productGroupList = daoReadonlyService.findProductGroupByConfigurationProvider(orgOwners, filter);
            }
        }
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


    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
