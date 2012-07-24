/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductGroupViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ProductGroupViewPage.class);
    private ProductGroup currentProductGroup;
    private Integer countProducts;
    @Autowired
    private SelectedProductGroupGroupPage selectedProductGroupGroupPage;
    @Autowired
    private ProductListPage productListPage;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        selectedProductGroupGroupPage.onShow();
        currentProductGroup = selectedProductGroupGroupPage.getCurrentProductGroup();
        TypedQuery<Product> query = entityManager.createQuery("from Product where productGroup=:productGroup", Product.class);
        query.setParameter("productGroup",currentProductGroup);
        countProducts = query.getResultList().size();
    }

    public Object showProducts() throws Exception{
        productListPage.setSelectedProductGroup(currentProductGroup);
        /* Показать и удаленный */
        productListPage.setDeletedStatusSelected(true);
        productListPage.reload();
        productListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/group/view";
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public void setCurrentProductGroup(ProductGroup currentProductGroup) {
        this.currentProductGroup = currentProductGroup;
    }

    public Integer getCountProducts() {
        return countProducts;
    }
}
