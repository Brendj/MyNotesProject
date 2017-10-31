/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        selectedProviders.clear();
    }

    @Override
    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/basicGood/create";
    }

    @Override
    public Object create() {
        try {
            List<Long> list = new ArrayList<Long>();
            for (ConfigurationProviderItem item : selectedProviders) {
                list.add(item.getIdOfConfigurationProvider());
            }
            Boolean result = daoService.createBasicGood(currentItem.getNameOfGood(), currentItem.getUnitsScale(), currentItem.getNetWeight(), list);
            if(result){
                //getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
                printMessage(currentItem + " успешно создан.");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при создании: " + currentItem, e);
        }
        return null;
    }

}
