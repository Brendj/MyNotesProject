/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.web.ui.org.contract.ContractItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class BasicGoodCreatePage extends BasicGoodEditPage {

    @Override
    public boolean isCreateMode() {
        return true;
    }
    @Override
    public boolean isEditMode() {
        return false;
    }
    @Override
    public void onShow() throws Exception {
        currentItem = new BasicGoodItem();
    }
}
