/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

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
public class ProductPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(ProductPanel.class);
    private final Queue<ProductSelect> completeHandlerLists = new LinkedList<ProductSelect>();

    private List<Product> productList;
    private Product selectProduct;
    private String filter;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;
    @Autowired
    private DAOReadonlyService daoReadonlyService;

    public void pushCompleteHandler(ProductSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addProduct(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectProduct:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void postConstruct() {
        productList = new ArrayList<Product>();
        filter="";
    }

    public void reload() throws Exception {
         productList = new ArrayList<Product>();
         filter="";
        retrieveProduct();
    }

    public Object updateProductSelectPage(){
        try {
            retrieveProduct();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveProduct() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if (!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()))
            productList = daoReadonlyService.findProductByConfigurationProvider(orgOwners, true, filter);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Product getSelectProduct() {
        return selectProduct;
    }

    public void setSelectProduct(Product selectProduct) {
        this.selectProduct = selectProduct;
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
