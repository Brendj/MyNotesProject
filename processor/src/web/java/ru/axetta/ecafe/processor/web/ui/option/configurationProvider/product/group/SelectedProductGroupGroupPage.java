/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
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
public class SelectedProductGroupGroupPage extends BasicWorkspacePage {

    private String title;
    private Long idOfProductGroup;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        ProductGroup productGroup = entityManager.find(ProductGroup.class, idOfProductGroup);
        if (null == productGroup) {
            this.title = null;
        } else {
            this.title = String.format("%d: %s", productGroup.getGlobalId(),
                    productGroup.getShortNameOfGroup());
        }
    }

    public String getTitle() {
        return title;
    }

    public Long getIdOfProductGroup() {
        return idOfProductGroup;
    }

    public void setIdOfProductGroup(Long idOfProductGroup) {
        this.idOfProductGroup = idOfProductGroup;
    }
}
