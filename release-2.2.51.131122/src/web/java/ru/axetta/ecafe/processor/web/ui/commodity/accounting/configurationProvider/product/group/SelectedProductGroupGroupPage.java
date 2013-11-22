/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
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
public class SelectedProductGroupGroupPage extends BasicWorkspacePage {

    private String title;
    private ProductGroup currentProductGroup;

    @Override
    public void onShow() throws Exception {
        if (null == currentProductGroup) {
            this.title = null;
        } else {
            this.title = String.format("%s", currentProductGroup.getShortNameOfGroup());
        }
    }

    public String getTitle() {
        return title;
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public void setCurrentProductGroup(ProductGroup currentProductGroup) {
        this.currentProductGroup = currentProductGroup;
    }
}
