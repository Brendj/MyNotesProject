/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
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
public class BasicGoodEditPage extends AbstractEditPage<BasicGoodItem> {

    private List<SelectItem> unitsScaleSelectItemList;

    @Autowired
    private DAOService daoService;

    @Override
    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/basicGood/edit";
    }

    public BasicGoodEditPage getAp() {
        return (BasicGoodEditPage) MainPage.getSessionInstance().getCurrentWorkspacePage();
    }

    public List<SelectItem> getUnitsScaleSelectItemList() {
        if (unitsScaleSelectItemList == null) {
            unitsScaleSelectItemList = new ArrayList<SelectItem>();
            for (UnitScale unitScale: UnitScale.values()){
                this.unitsScaleSelectItemList.add(new SelectItem(unitScale,unitScale.toString()));
            }
        }
        return unitsScaleSelectItemList;
    }

    public void setUnitsScaleSelectItemList(List<SelectItem> unitsScaleSelectItemList) {
        this.unitsScaleSelectItemList = unitsScaleSelectItemList;
    }

    @Override
    public Object save() {
        try {
            Boolean result = daoService.updateBasicGood(currentItem.getIdOfBasicGood(), currentItem.getNameOfGood(), currentItem.getUnitsScale(), currentItem.getNetWeight());
            if(result){
                getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
                printMessage(currentItem + " успешно изменен.");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при изменении: " + currentItem, e);
        }
        return null;
    }
}
