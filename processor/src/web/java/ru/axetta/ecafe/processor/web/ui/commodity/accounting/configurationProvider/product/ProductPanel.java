/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    private final Queue<ProductSelect> completeHandlerLists = new LinkedList<ProductSelect>();

    private List<Product> productList;
    private Product selectProduct;
    private String filter;

    @PersistenceContext
    private EntityManager entityManager;

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
    public void reload() throws Exception {
         productList = new ArrayList<Product>();
         filter="";
    }

    public Object updateProductSelectPage(){
        productList = retrieveProduct();
        return null;
    }

    @Transactional
    private List<Product> retrieveProduct(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(productName) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from Product "+ where + " order by id";
        return entityManager.createQuery(query, Product.class).getResultList();
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

}
