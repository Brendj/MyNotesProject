/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SelectedProductGroupPage extends BasicWorkspacePage {

    private String title;
    private Product currentProduct;

    @Override
    public void onShow() throws Exception {
        if (null == currentProduct) {
            this.title = null;
        } else {
            this.title = String.format("%s", currentProduct.getProductName());
        }
    }

    public String getTitle() {
        return title;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }
}
