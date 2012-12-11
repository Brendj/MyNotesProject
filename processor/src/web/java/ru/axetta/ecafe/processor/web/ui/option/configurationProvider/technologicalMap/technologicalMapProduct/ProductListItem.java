/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class ProductListItem {

    private Boolean checked=false;
    private Product product;

    public ProductListItem(Boolean checked, Product product) {
        this.checked = checked;
        this.product = product;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
