/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SelectedGoodGroupPage extends BasicWorkspacePage {

    private String title;
    private Good currentGood;

    @Override
    public void onShow() throws Exception {
        if (null == currentGood) {
            this.title = null;
        } else {
            this.title = String.format("%s", currentGood.getNameOfGood());
        }
    }

    public String getTitle() {
        return title;
    }

    public Good getCurrentGood() {
        return currentGood;
    }

    public void setCurrentGood(Good currentGood) {
        this.currentGood = currentGood;
    }
}
