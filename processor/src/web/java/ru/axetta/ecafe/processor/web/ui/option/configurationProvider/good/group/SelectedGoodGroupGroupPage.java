/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
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
public class SelectedGoodGroupGroupPage extends BasicWorkspacePage {

    private String title;
    private GoodGroup currentGoodGroup;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        if (null == currentGoodGroup) {
            this.title = null;
        } else {
            this.title = String.format("%s", currentGoodGroup.getNameOfGoodsGroup());
        }
    }

    public String getTitle() {
        return title;
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public void setCurrentGoodGroup(GoodGroup currentGoodGroup) {
        this.currentGoodGroup = currentGoodGroup;
    }
}
